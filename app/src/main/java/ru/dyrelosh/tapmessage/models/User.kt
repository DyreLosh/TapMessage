package ru.dyrelosh.tapmessage.models

data class User(
    var id: String = "",
    var username: String = "",
    var fullName: String = "",
    var phone: String = "",
    var email: String = "",
    var state: String = "",
    var bio: String = "",
    var photoUrl: String = ""
)