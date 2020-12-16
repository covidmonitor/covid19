package pl.covid19.database

import androidx.room.*

@Entity(tableName = "areas",indices = arrayOf(Index(value = ["idGus"], unique = true)))
data class AreaDB(
        @PrimaryKey(autoGenerate = true)
        var areaAutoId: Long = 0L,

        @ColumnInfo(name = "Created")
        var startTimeMilli: Long = System.currentTimeMillis(),

        var name: String? = null,

        var type: String? = null,

       // var idApi: String? = null,

        var idGus: String? = null
)
{
        @Ignore
        fun typetoItem(): String {
                return when(type)
                {null -> ""
                name-> ""
                else -> "("+type?.take(3)+")"}
        }
        @Ignore
        fun colortoItem(): Boolean {
                return when(type)
                {"Powiat"-> true
                 "Województwo"-> true
                 else -> false}
        }
}

