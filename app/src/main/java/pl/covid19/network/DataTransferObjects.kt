
package  pl.covid19.network

import com.squareup.moshi.JsonClass
import pl.covid19.database.FazyDB
import pl.covid19.database.GOVPLXDB
import pl.covid19.database.VersionDB
import pl.covid19.domain.Fazy
import pl.covid19.domain.Govplx
import pl.covid19.domain.Version


@JsonClass(generateAdapter = true)
data class Networkgovplx(
    val Date: String,
    val Woj: String,
    val Pow: String,
    val Liczba: Int,
    val Liczba10tys: String,
    val WszystkieSmiertelne: Int,
    val SmiertelneCovid: Int,
    val SmiertelneInne: Int,
    val id: String,
    var Liczba10tysAvg7: String?,
    var idFazy: Int,
)

@JsonClass(generateAdapter = true)
data class NetworkGovplxContainer(val Plx: List<Networkgovplx>)

fun NetworkGovplxContainer.asDomainModel(): List<Govplx> {
    return Plx.map {
        Govplx(it.Date,it.Woj,it.Pow,it.Liczba,it.Liczba10tys,it.WszystkieSmiertelne,it.SmiertelneCovid,it.SmiertelneInne,it.id,it.Liczba10tysAvg7, it.idFazy)
    }
}

fun NetworkGovplxContainer.asDatabaseModel(): Array<GOVPLXDB>
{
    return Plx.map {
        GOVPLXDB(it.Date, it.Woj, it.Pow, it.Liczba, it.Liczba10tys,it.WszystkieSmiertelne,it.SmiertelneCovid,it.SmiertelneInne,it.id, it.Liczba10tysAvg7, it.idFazy)
    }.toTypedArray()
}

//Fazy
@JsonClass(generateAdapter = true)
data class NetworkFazy(
    val idFazyKey: Int,
    val DataOgloszenia: String,
    val progLiczba10tys: String? = null,
    val Nazwa: String,
    val img: String,
    val Color: String? = null,
    val Opis: String,
)

@JsonClass(generateAdapter = true)
data class NetworkFazyContainer(val fazy: List<NetworkFazy>)

fun NetworkFazyContainer.asDomainModel(): List<Fazy> {
    return fazy.map {
        Fazy(it.idFazyKey, it.DataOgloszenia,it.progLiczba10tys,it.Nazwa,it.img,it.Color,it.Opis)
    }
}
fun NetworkFazyContainer.asDatabaseModel(): Array<FazyDB>
{
    return fazy.map {
        FazyDB(it.idFazyKey, it.DataOgloszenia, it.progLiczba10tys, it.Nazwa, it.img,it.Color,it.Opis)
    }.toTypedArray()
}
//Version
@JsonClass(generateAdapter = true)
data class NetworkVersion(
        val typ: String="",
        val version: String="",
        val decription: String="",
        val dateod: String="")

@JsonClass(generateAdapter = true)
data class NetworkVersionContainer(val version: List<NetworkVersion>)

fun NetworkVersionContainer.asDomainModel(): List<Version> {
    return version.map {
        Version(it.typ,it.version,it.decription,it.dateod)
    }
}

fun NetworkVersionContainer.asDatabaseModel(): Array<VersionDB>
{
    return version.map {
        VersionDB( it.typ, it.version, it.decription, it.dateod)
    }.toTypedArray()
}