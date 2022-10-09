package ru.dyrelosh.tapmessage.models

//общая модель приложения
data class Common(
    var id: String = "",
    var username: String = "",
    var fullName: String = "",
    var phone: String = "",
    var email: String = "",
    var state: String = "",
    var bio: String = "",
    var photoUrl: String = "",

    var text: String = "",
    var type: String = "",
    var from: String = "",
    var timeStamp: Any = ""



) : java.io.Serializable {
    override fun equals(other: Any?): Boolean {
        return (other as Common).id == id
    }
}
