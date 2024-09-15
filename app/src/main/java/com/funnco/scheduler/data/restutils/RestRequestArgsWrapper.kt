package org.example.common.restutils

data class RestRequestArgsWrapper(val args: Array<out Any>) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RestRequestArgsWrapper

        return args.contentEquals(other.args)
    }

    override fun hashCode(): Int {
        return args.contentHashCode()
    }
}
