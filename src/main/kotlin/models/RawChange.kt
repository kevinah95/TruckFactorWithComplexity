package models

import com.google.gson.annotations.SerializedName
import java.util.Date

data class RawChange(
    @SerializedName(value = "dev_name")
    val devName: String
) {
    var _package: String? = null
    var _class: String? = null
    var method: String = ""
    var date: Date? = null
    @SerializedName(value = "dev_email")
    var devEmail: String = ""
    var type: String = ""
    @SerializedName(value = "cur_m_cx")
    var complexity: Int = 0
}