package com.funnco.scheduler.domain

object DateUtils {
    val mappedShortDays = mapOf<Int, String>(1 to "пн",2 to "вт",3 to "ср",4 to "чт",5 to "пт",6 to "сб",7 to "вс",)
    val mappedDays = mapOf<Int, String>(1 to "Понедельник",2 to "Вторник",3 to "Среда",4 to "Четверг",5 to "Пятница",6 to "Суббота",7 to "Воскресенье",)
    val mappedWeekOffset = mapOf<Int, String>(
        -4 to "4 недели назад",
        -3 to "3 недели назад",
        -2 to "2 недели назад",
        -1 to "Прошлая неделя",
        0 to "Текущая неделя",
        1 to "Следующая неделя",
        2 to "Через 2 недели",
        3 to "Через 3 недели",
        4 to "Через 4 недели"
    )
    val mappedDayOffset = mapOf<Int, String>(
        -2 to "Позавчера",
        -1 to "Вчера",
        0 to "Сегодня",
        1 to "Завтра",
        2 to "Послезавтра"
    )
}