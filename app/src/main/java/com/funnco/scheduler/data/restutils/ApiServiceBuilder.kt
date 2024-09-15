@file:OptIn(ExperimentalStdlibApi::class)

import android.util.Log
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.jackson.jackson
import kotlinx.coroutines.*
import org.example.common.restutils.ApiException
import org.example.common.restutils.DELETE
import org.example.common.restutils.GET
import org.example.common.restutils.Header
import org.example.common.restutils.POST
import org.example.common.restutils.PUT
import org.example.common.restutils.Param
import org.example.common.restutils.Path
import org.example.common.restutils.RestClient
import org.example.common.restutils.RestRequestArgsWrapper
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Parameter
import java.lang.reflect.Proxy
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.javaType
import kotlin.reflect.jvm.javaMethod
import kotlin.reflect.jvm.kotlinFunction

class ApiServiceBuilder<T : Any>(private val apiInterface: Class<T>) {
    private var client: HttpClient? = null

    fun configureClient(configure: HttpClientConfig<*>.() -> Unit): ApiServiceBuilder<T> {
        client = HttpClient(CIO) {
            install(ContentNegotiation) {
                jackson {
                    configure(SerializationFeature.INDENT_OUTPUT, true)
                    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                    registerModule(JavaTimeModule())
                }
            }
            install(Logging) {
                level = LogLevel.ALL
            }
            configure()
        }
        return this
    }

    fun build(): T {
        val restClientAnnotation = apiInterface.getAnnotation(RestClient::class.java)
            ?: throw IllegalArgumentException("Interface must have @RestClient annotation")

        val baseUrl = restClientAnnotation.baseUrl

        val client = client ?: HttpClient(CIO) {
            install(ContentNegotiation) {
                jackson {
                    configure(SerializationFeature.INDENT_OUTPUT, true)
                    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                    registerModule(JavaTimeModule())
                }
            }
            install(Logging) {
                level = LogLevel.ALL
            }
        }

        return Proxy.newProxyInstance(
            apiInterface.classLoader,
            arrayOf(apiInterface),
            RestClientInvocationHandler(client, baseUrl, apiInterface)
        ) as T
    }

    private class RestClientInvocationHandler<T : Any>(
        private val client: HttpClient,
        private val baseUrl: String,
        private val apiInterface: Class<T>
    ) : InvocationHandler {

        private val methodMap = apiInterface.methods.associateBy { it.name }

        override fun invoke(proxy: Any?, method: Method, args: Array<out Any>?): Any? {
            val argsWrapper = RestRequestArgsWrapper(args ?: emptyArray())

            val originalMethod = methodMap[method.name]
                ?: throw NotImplementedError("Method ${method.name} is not implemented in the interface")

            val annotation = originalMethod.annotations.firstOrNull() ?: return NotImplementedError(
                "HTTP method not specified"
            )

            val url = buildUrl(
                when (annotation) {
                    is GET -> annotation.url
                    is POST -> annotation.url
                    is PUT -> annotation.url
                    is DELETE -> annotation.url
                    else -> throw NotImplementedError("Unsupported annotation ${annotation::class.simpleName}")
                },
                originalMethod.parameters,
                argsWrapper
            )

            val httpMethod = when (annotation) {
                is GET -> HttpMethod.Get
                is POST -> HttpMethod.Post
                is PUT -> HttpMethod.Put
                is DELETE -> HttpMethod.Delete
                else -> throw NotImplementedError("Unsupported HTTP method")
            }

            val headers = originalMethod.parameters
                .mapNotNull { param ->
                    param.getAnnotation(Header::class.java)?.let { headerAnnotation ->
                        val headerIndex = originalMethod.parameters.indexOf(param)
                        val headerValue = args?.getOrNull(headerIndex)?.toString() ?: ""
                        headerAnnotation.key to headerValue
                    }
                }.toMap()

            return runBlocking {
                executeRequest(
                    client,
                    "$baseUrl/$url",
                    httpMethod,
                    argsWrapper,
                    originalMethod,
                    headers
                )
            }
        }

        private suspend fun executeRequest(
            client: HttpClient,
            url: String,
            method: HttpMethod,
            args: RestRequestArgsWrapper,
            originalMethod: Method,
            headers: Map<String, String>
        ): Any {
            val response: HttpResponse = when (method) {
                HttpMethod.Get -> client.get(url) {
                    headers.forEach { (key, value) -> header(key, value) }
                }
                HttpMethod.Post -> client.post(url) {
                    headers.forEach { (key, value) -> header(key, value) }
                    setBody(args.args.firstOrNull())
                }
                HttpMethod.Put -> client.put(url) {
                    headers.forEach { (key, value) -> header(key, value) }
                    setBody(args.args.lastOrNull())
                }
                HttpMethod.Delete -> client.delete(url) {
                    headers.forEach { (key, value) -> header(key, value) }
                }
                else -> throw IllegalArgumentException("Unsupported HTTP method")
            }

            if (response.status.value in 200..299) {
                val objectMapper = jacksonObjectMapper()
                    .configure(SerializationFeature.INDENT_OUTPUT, true)
                    .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                    .registerModule(JavaTimeModule())
                val kFunction = originalMethod.kotlinFunction ?: throw IllegalArgumentException("Method is not a Kotlin function")
                val kType = kFunction.returnType
                val javaType = objectMapper.typeFactory.constructType(kType.javaType)
                try {
                    return objectMapper.readValue(response.bodyAsText(), javaType)
                } catch (any: Exception){
                    return response.status.value
                }
            } else {
                throw ApiException("HTTP error ${response.status.value}: ${response.status.description}")
            }
        }

        private fun buildUrl(
            urlTemplate: String,
            parameters: Array<Parameter>,
            args: RestRequestArgsWrapper
        ): String {
            var url = urlTemplate
            val queryParams = mutableListOf<String>()
            parameters.forEachIndexed { index, parameter ->
                val pathAnnotation = parameter.getAnnotation(Path::class.java)
                val paramAnnotation = parameter.getAnnotation(Param::class.java)
                when {
                    pathAnnotation != null -> {
                        url = url.replace("{${pathAnnotation.value}}", args.args[index].toString())
                    }
                    paramAnnotation != null -> {
                        queryParams.add("${paramAnnotation.value}=${args.args[index]}")
                    }
                }
            }
            if (queryParams.isNotEmpty()) {
                url += "?" + queryParams.joinToString("&")
            }
            return url
        }
    }
}