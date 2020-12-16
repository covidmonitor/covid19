
package pl.covid19.domain


import com.squareup.moshi.Json
import java.sql.Date

/**
 * Domain objects are plain Kotlin data classes that represent the things in our app. These are the
 * objects that should be displayed on screen, or manipulated by the app.
 *
 * @see database for objects that are mapped to the database
 * @see network for objects that parse or prepare network calls
 */

data class Govplx(
    val date: String,
    val Woj: String,
    val Pow: String,
    val Liczba: Int,
    val Liczba10tys: String,
    val WszystkieSmiertelne: Int,
    val SmiertelneCovidval: Int,
    val SmiertelneInne: Int,
    val id: String,
    val Liczba10tysAvg7:String?,
    val idFazy: Int,
)
{}


data class Fazy(
    val id: Int,
    val DataOgloszenia: String,
    val progLiczba10tys: String? = null,
    val Nazwa: String,
    val img: String,
    val Color: String? = null,
    val Opis: String
)
{}