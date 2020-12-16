package pl.covid19.util

object Constants {
    const val CHANNEL_ID_PERIOD_WORK = "PERIODIC_APP_UPDATES"
    const val CHANNEL_ID_ONE_TIME_WORK = "INSTANT_APP_UPDATES"
    const val WORK_TAG = "PeriodicWorkerTag"
    const val BASE_URL = "TODO"
    const val PRIVACY_URL = "https://covidmonitor.pl/PolitykaPrywatnosci.html"
    const val PRIVACY_NAME = "Polityka Prywatności"
    enum class internetStatus { LOADING, ERROR, DONE }
}