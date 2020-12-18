package pl.covid19.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.webkit.WebSettings
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import java.text.SimpleDateFormat
import java.util.*


private val PUNCTUATION = listOf(", ", "; ", ": ", " ")

/**
 * Truncate long text with a preference for word boundaries and without trailing punctuation.
 */
fun String.smartTruncate(length: Int): String {
    val words = split(" ")
    var added = 0
    var hasMore = false
    val builder = StringBuilder()
    for (word in words) {
        if (builder.length > length) {
            hasMore = true
            break
        }
        builder.append(word)
        builder.append(" ")
        added += 1
    }

    PUNCTUATION.map {
        if (builder.endsWith(it)) {
            builder.replace(builder.length - it.length, builder.length, "")
        }
    }

    if (hasMore) {
        builder.append("...")
    }
    return builder.toString()
}
fun TodayToStringSql(days: Int = 0):String
{
    val cal = Calendar.getInstance()
    cal.add(Calendar.DATE, days)
    val mon= cal.get(Calendar.MONTH)+1
    var monStr=mon.toString()
    if (mon<10) monStr="0"+mon
    val day = cal.get(Calendar.DAY_OF_MONTH)
    var dayStr=day.toString()
    if (day<10) dayStr="0"+day
    return cal.get(Calendar.YEAR).toString()+"-"+ monStr +"-"+ dayStr
}

fun calendarToString(calen: Calendar):String
{
    val cal = calen
    val mon= cal.get(Calendar.MONTH)+1
    var monStr=mon.toString()
    if (mon<10) monStr="0"+mon
    val day = cal.get(Calendar.DAY_OF_MONTH)
    var dayStr=day.toString()
    if (day<10) dayStr="0"+day
    return cal.get(Calendar.YEAR).toString()+"-"+ monStr +"-"+ dayStr
}

fun atPlRange():Boolean
{
    //TODO 12 get from Webserwis
    val min= java.sql.Time.valueOf("10:00:00")
    val max= java.sql.Time.valueOf("13:30:01")
    //TODO 12 set as local polish time  +1
    val formatter = SimpleDateFormat("HH:mm:ss")
    val d =java.sql.Time.valueOf(formatter.format(Calendar.getInstance().getTime()))
    return d.after(min) && d.before(max)
}
fun enableJava(webSettings: WebSettings, notcache: Boolean=true) {
    webSettings.javaScriptEnabled = true
    webSettings.setAppCacheEnabled(true)
    if (notcache)
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK)
    else
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ONLY)
    webSettings.domStorageEnabled = true
    webSettings.setAllowFileAccess(true)
    /*val appCacheDir = parentFragment?.context?.getDir("cache", Context.MODE_PRIVATE)?.getPath()
    webSettings.setAppCachePath(appCacheDir)*/
    }
fun Context.color(resource: Int): Int {
    return ContextCompat.getColor(this, resource)
}