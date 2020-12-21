package pl.covid19.util

object Constants {
    const val CHANNEL_ID_PERIOD_WORK = "PERIODIC_APP_UPDATES"
    const val CHANNEL_ID_ONE_TIME_WORK = "INSTANT_APP_UPDATES"
    const val WORK_TAG = "PeriodicWorkerTag"
    //const val BASE_URL = "https://covidmonitor.pl/testapi/"
    const val BASE_URL = "https://covidmonitor.pl/api/"
    const val PRIVACY_URL = "https://covidmonitor.pl/PolitykaPrywatnosci.html"
    const val PRIVACY_NAME = "Polityka Prywatności"
    const val SHARE_NAME = "Zaproś znajonych"
    const val PLANED_NAME ="Planowane zmainy"
    const val PLANED_URL ="https://covidmonitor.pl/index.html#plan"
    const val ABOUT_NAME ="O tej aplikacji"
    const val ABOUT_URL ="https://covidmonitor.pl/index.html#app"
    enum class internetStatus { LOADING, ERROR, DONE }
}