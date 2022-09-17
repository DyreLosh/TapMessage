package ru.dyrelosh.tapmessage.models

data class User(
    val id: String = "",
    var username: String = "",
    var fullName: String = "",
    var phone: String = "",
    var email: String = "",
    var status: String = "",
    var bio: String = "",
    var photoUrl: String = ""
)