package ru.dyrelosh.tapmessage.models

data class Common(
    var id: String = "",
    var username: String = "",
    var fullName: String = "",
    var phone: String = "",
    var email: String = "",
    var state: String = "",
    var bio: String = "",
    var photoUrl: String = ""
) : java.io.Serializable
