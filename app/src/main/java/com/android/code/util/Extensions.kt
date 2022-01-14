package com.android.code.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.*
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.provider.Settings
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.util.TypedValue
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import androidx.core.text.HtmlCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.android.code.App
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.*
import java.text.*
import java.util.*
import kotlin.math.roundToInt

// Json
inline fun <reified T> String.fromJson(): T {
    val reader = JsonReader(StringReader(this)).apply {
        isLenient = true
    }
    return Gson().fromJson(reader, T::class.java)
}

inline fun <reified T> String.fromJsonWithTypeToken(): T {
    val reader = JsonReader(StringReader(this)).apply {
        isLenient = true
    }
    return Gson().fromJson(reader, object : TypeToken<T>() {}.type)
}

fun Any.toJson(): String = Gson().toJson(this)

fun Int.toDp(): Float = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    this.toFloat(),
    App.instance.resources.displayMetrics
)

fun Int.toDp(context: Context): Float = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    this.toFloat(),
    context.resources.displayMetrics
)

fun Int.toPx(): Int = (this * App.instance.resources.displayMetrics.density).toInt()

fun Float.toPx(): Float = (this * App.instance.resources.displayMetrics.density)

fun Float.toToolTipDialogPx(context: Context): Int =
    (this * context.resources.displayMetrics.density + 0.5f).toInt()

fun Context.getStatusBarHeight(): Int {
    var result = 0
    val resourceId = this.resources.getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId > 0) {
        result = this.resources.getDimensionPixelOffset(resourceId)
    }
    return result
}

fun View.getBitmapFromView(): Bitmap {
    val bitmap = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)
    this.layout(this.left, this.top, this.right, this.bottom)
    this.draw(Canvas(bitmap))
    return bitmap
}

data class Quadruple<out A, out B, out C, out D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
)

fun String.htmlColor(@ColorInt hexCode: Int): String {
    val convertHexCode =
        String.format("#%06X", 0xFFFFFF and hexCode)
    return "<font color='$convertHexCode'>$this</font>"
}

fun String.htmlUnderBar(): String {
    return "<u>$this</u>"
}

@ColorInt
fun Int.adjustAlpha(factor: Float): Int {
    val alpha = (Color.alpha(this) * factor).roundToInt()
    val red = Color.red(this)
    val green = Color.green(this)
    val blue = Color.blue(this)
    return Color.argb(alpha, red, green, blue)
}

fun String.copyClipboard(context: Context, key: String) {
    val clipboard =
        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val data = ClipData.newPlainText(
        key,
        this
    )
    clipboard.setPrimaryClip(data)
}

fun String.setColorText(color: Int, start: Int, end: Int) =
    SpannableStringBuilder(this).apply {
        this.setSpan(ForegroundColorSpan(color), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

fun String.makeTextWithUnderline() =
    SpannableStringBuilder(this).apply {
        this.setSpan(UnderlineSpan(), 0, this.length, 0)
    }

fun String?.fromHtml(): Spanned {
    if (this.isNullOrBlank()) {
        return SpannableString(String.empty())
    }

    return HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_COMPACT)
}

object HtmlTagConstant {

    const val SPLIT_CHAR = ">"
    const val FIND_LARGE_OPEN_TAG = "ql-size-large"
    const val ADD_LARGE_OPEN_TAG = "><big>"
    const val CLOSE_LARGE_TAG = "</big></"
    const val FIND_HUGE_OPEN_TAG = "ql-size-huge"
    const val ADD_HUGE_OPEN_TAG = "><big><big>"
    const val CLOSE_HUGE_TAG = "</big></big></"

    const val TAG_UL = "ULL"
    const val TAG_OL = "OLL"
    const val TAG_LI = "LII"

    const val FIND_CLOSE_TAG = "</"

    const val FIND_RGB_OPEN_TAG = "rgb("
    const val ADD_RGB_OPEN_TAG = "><font color="
    const val ADD_RGB_CLOSE_TAG = ">"
    const val CLOSE_RGB_TAG = "</font></"
    const val FIND_RGB_SPLIT = ")"
    const val FIND_COLOR_SPLIT = ","
}

fun String?.fromHtmlConvertColor(): String {
    val result = mutableListOf<String>()
    if (!this.isNullOrBlank()) {
        // convert rgb to font color
        val splitArray = this.split(HtmlTagConstant.SPLIT_CHAR)
        val rgbIndexList = mutableListOf<Int>()
        splitArray.mapIndexed { _index, _line ->
            when {
                _line.contains(HtmlTagConstant.FIND_RGB_OPEN_TAG) -> {
                    try {
                        // ex) 51, 51, 51 > #333333
                        val colorValues =
                            _line.split(HtmlTagConstant.FIND_RGB_OPEN_TAG, ignoreCase = true)[1]
                                .split(HtmlTagConstant.FIND_RGB_SPLIT)[0]
                                .split(HtmlTagConstant.FIND_COLOR_SPLIT)
                                .map { it.trim().toInt() }
                        val replacedColor = HtmlTagConstant.ADD_RGB_OPEN_TAG +
                            String.format(
                                "#%02X%02X%02X", *colorValues.toTypedArray()
                            ) + HtmlTagConstant.ADD_RGB_CLOSE_TAG
                        result.add(_line.plus(replacedColor))
                        rgbIndexList.add(_index)
                    } catch (e: Exception) {
                        result.add(_line.plus(HtmlTagConstant.SPLIT_CHAR))
                    }
                }
                else -> {
                    if (_index != splitArray.lastIndex) {
                        result.add(_line.plus(HtmlTagConstant.SPLIT_CHAR))
                    } else {
                        result.add(_line)
                    }
                }
            }
        }
        rgbIndexList.forEach {
            result[it + 1] = result[it + 1].replace(
                HtmlTagConstant.FIND_CLOSE_TAG,
                HtmlTagConstant.CLOSE_RGB_TAG
            )
        }
    }
    return result.joinToString(String.empty())
}

fun String?.fromHtmlConvertFont(): Spanned {
    var htmlString = SpannableString(String.empty()).toString()
    if (!this.isNullOrBlank()) {
        // Convert Huge, Large to Big
        val splitArray = this.split(HtmlTagConstant.SPLIT_CHAR)

        val largeFindIndexList = mutableListOf<Int>()
        val hugeFindIndexList = mutableListOf<Int>()
        val result = mutableListOf<String>()

        splitArray.mapIndexed { _index, _line ->
            when {
                _line.contains(HtmlTagConstant.FIND_LARGE_OPEN_TAG) -> {
                    result.add(_line.plus(HtmlTagConstant.ADD_LARGE_OPEN_TAG))
                    largeFindIndexList.add(_index)
                }
                _line.contains(HtmlTagConstant.FIND_HUGE_OPEN_TAG) -> {
                    result.add(_line.plus(HtmlTagConstant.ADD_HUGE_OPEN_TAG))
                    hugeFindIndexList.add(_index)
                }
                else -> {
                    if (_index != splitArray.lastIndex) {
                        result.add(_line.plus(HtmlTagConstant.SPLIT_CHAR))
                    } else {
                        result.add(_line)
                    }
                }
            }
        }
        largeFindIndexList.forEach {
            result[it + 1] = result[it + 1].replace(
                HtmlTagConstant.FIND_CLOSE_TAG,
                HtmlTagConstant.CLOSE_LARGE_TAG
            )
        }
        hugeFindIndexList.forEach {
            result[it + 1] = result[it + 1].replace(
                HtmlTagConstant.FIND_CLOSE_TAG,
                HtmlTagConstant.CLOSE_HUGE_TAG
            )
        }

        htmlString = result.joinToString(String.empty()).fromHtmlConvertColor()
    }
    return HtmlCompat.fromHtml(htmlString, HtmlCompat.FROM_HTML_MODE_COMPACT)
}

fun Boolean.toVisibleOrGone(): Int {
    return if (this) {
        View.VISIBLE
    } else {
        View.GONE
    }
}

fun Boolean.toVisibleOrInvisible(): Int {
    return if (this) {
        View.VISIBLE
    } else {
        View.INVISIBLE
    }
}

fun String.Companion.empty() = ""
fun String.Companion.space() = " "
fun Int.Companion.zero() = 0
fun Long.Companion.zero() = 0L

fun View.hideKeypad() {
    val imm = context.getSystemService<InputMethodManager>() ?: return
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun Activity.hideKeypad() {
    currentFocus?.hideKeypad()
}

fun View.showKeypad() {
    val imm = context.getSystemService<InputMethodManager>() ?: return
    imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

fun Activity.showKeypad() {
    currentFocus?.showKeypad()
}

/**
 * Global scope exception 처리
 */
val coroutineExceptionHandler: CoroutineExceptionHandler =
    CoroutineExceptionHandler { coroutineContext, throwable ->
        Timber.e("CoroutineExceptionHandler - coroutineContext : $coroutineContext throwable : $throwable")
    }


@SuppressLint("HardwareIds")
fun getAndroidId(): String =
    Settings.Secure.getString(App.instance.contentResolver, Settings.Secure.ANDROID_ID)

fun String.isNotEmpty(block: (String) -> Unit) {
    if (this.isNotEmpty()) {
        block(this)
    }
}