package pl.covid19.database



import androidx.room.*

@Entity(tableName = "version",primaryKeys = ["typ","dateod"])
data class VersionDB(
    val typ: String,
    val version: String,
    val decription: String,
    val dateod: String) {
}
