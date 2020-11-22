package ru.netology.First_Chat

import java.util.*

data class Massage(
    var name: String = "",
    val textMassage: String = "",
    val massageTime: Date = Calendar.getInstance().time

)