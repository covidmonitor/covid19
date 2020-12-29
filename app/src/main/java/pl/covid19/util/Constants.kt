package pl.covid19.util

object Constants {
    const val CHANNEL_ID_PERIOD_WORK = "PERIODIC_APP_UPDATES"
    const val CHANNEL_ID_ONE_TIME_WORK = "INSTANT_APP_UPDATES"
    const val WORK_TAG = "PeriodicWorkerTag"
    const val BASEHOST_URL ="covidmonitor.pl"
    const val BASE_URL = "https://"+BASEHOST_URL
    const val BASEAPI_URL = BASE_URL+"/testapi/"
    const val PRIVACY_URL = BASE_URL+"/PolitykaPrywatnosci.html"
    const val PRIVACY_NAME = "Polityka Prywatności"
    const val SHARE_NAME = "Zaproś znajonych"
    const val PLANED_NAME ="Planowane zmainy"
    const val PLANED_URL =BASE_URL+"/index.html#plan"
    const val ABOUT_NAME ="O tej aplikacji"
    const val ABOUT_URL =BASE_URL+"/index.html#app"
    enum class internetStatus { LOADING, ERROR, DONE }
}