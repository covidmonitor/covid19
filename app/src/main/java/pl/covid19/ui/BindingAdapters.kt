package pl.covid19.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Html
import android.text.Spanned
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import pl.covid19.R
import pl.covid19.database.AreaDBGOVPLXDBFazyDB
import pl.covid19.util.Constants


@BindingAdapter("customVisibility")
fun setVisibility(view: View, visible: Boolean) {
    view.visibility = if (visible) View.VISIBLE else View.INVISIBLE
}

@BindingAdapter("mybackground")
fun mybackg(view: View, it: String?) {
    view.background = it.let{ ColorDrawable(Color.parseColor(it.toString()))}
}

@BindingAdapter("evalAvgIndicator10")
fun TextView.indicator(it: AreaDBGOVPLXDBFazyDB?) {
    //wyznaczenie +/-10%
    val procent= 0.10
    val licz10tys = it?.govpl?.Liczba10tys?.toFloat()?:0.0f
    val licz10tysAvg =it?.govpl?.Liczba10tysAvg7?.toFloat()?:0.0f

    val licz10tys7_5 = (1+procent)  * licz10tysAvg
    val licz10tys7__5 = (1-procent) * licz10tysAvg
        if  (licz10tys > licz10tys7_5)
            this.text = this.getContext().getString(R.string.up)
        else if  (licz10tys < licz10tys7__5)
            this.text = this.getContext().getString(R.string.down)
        else
            this.text = this.getContext().getString(R.string.same)
}



@BindingAdapter("android:htmlText")
fun setHtmlTextValue(textView: TextView, htmlText: String?) {
    if (htmlText == null) return
    val result: Spanned
    result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(htmlText, Html.FROM_HTML_MODE_LEGACY)
    } else {
        Html.fromHtml(htmlText)
    }
    textView.text = result
}

@BindingAdapter("goneIfNotNull")
fun goneIfNotNull(view: View, it: Any?) {
    view.visibility = if (it != null) View.GONE else View.VISIBLE
}

@BindingAdapter("internetStatus")
fun bindStatus(statusImageView: ImageView, status: Constants.internetStatus?) {
    when (status) {
        Constants.internetStatus.LOADING -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.loading_animation)
        }
        Constants.internetStatus.ERROR -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.ic_connection_error)
        }
        Constants.internetStatus.DONE -> {
            statusImageView.visibility = View.GONE
        }
    }
}
