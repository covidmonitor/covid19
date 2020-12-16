package pl.covid19.database

import androidx.room.*

@Entity(tableName = "fazy",indices = arrayOf(Index(value = ["idFazyKey"], unique = true)))
data class FazyDB(
        @PrimaryKey(autoGenerate = false)
        val idFazyKey: Int=-1,
        val DataOgloszenia: String="",
        val progLiczba10tys: String?= null,
        val Nazwa: String="",
        val img: String="",
        val Color: String? = null,
        val Opis: String="",
        @ColumnInfo(name = "Created3")
        var startTime: Long = System.currentTimeMillis(),
        ) {
}


