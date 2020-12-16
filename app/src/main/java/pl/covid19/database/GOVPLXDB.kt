package pl.covid19.database

import androidx.room.*
import com.squareup.moshi.Json
import java.sql.Date

@Entity(tableName = "govplx",indices = arrayOf(Index(value = ["Date","id"], unique = true)))
data class GOVPLXDB(
        val Date: String ="",
        var Woj: String="",
        var Pow: String="",
        var Liczba: Int=0,
        var Liczba10tys:String="",
        var WszystkieSmiertelne: Int=0,
        var SmiertelneCovid: Int=0,
        var SmiertelneInne: Int=0,
        var id: String="",
        var Liczba10tysAvg7: String?=null,
        var idFazy: Int=0,
        @PrimaryKey(autoGenerate = true)
        var autoId: Long = 0L,
        @ColumnInfo(name = "Created2")
        var startTime: Long = System.currentTimeMillis(),
        ) {
}

data class AreaDBGOVPLXDB (
        @Embedded var area: AreaDB = AreaDB(),
        @Embedded var govpl: GOVPLXDB = GOVPLXDB()
        ) {
}

data class AreaDBGOVPLXDBFazyDB (
        @Embedded var area: AreaDB = AreaDB(),
        @Embedded var govpl: GOVPLXDB = GOVPLXDB(),
        @Embedded var fazy: FazyDB = FazyDB(),
) {
}