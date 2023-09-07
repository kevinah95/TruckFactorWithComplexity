package models

data class RawChange(
    val devName: String
) {
    var _package: String? = null
    var _packageBefore: String? = null
    var _class: String? = null
    var _classBefore: String? = null
    var method: String = ""
    var newPath: String = ""
    var oldPath: String = ""
    var date: String = ""
    var devEmail: String = ""
    var type: String = ""
    var complexity: Int = 0
}