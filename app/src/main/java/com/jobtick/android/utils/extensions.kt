@file:JvmName("MyExtensions")

package com.jobtick.android.utils

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.ConnectivityManager
import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.jobtick.android.R
import java.lang.reflect.Type
import java.util.regex.Pattern
import kotlin.math.floor
import kotlin.math.round
import kotlin.math.roundToInt

const val MILLION = 1000000L
const val BILLION = 1000000000L
const val TRILLION = 1000000000000L

fun Int.dpToPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
fun Int.pxToDp(): Int = (this / Resources.getSystem().displayMetrics.density).toInt()
fun String.isNumeric(): Boolean {
    return this.matches(".*\\d.*".toRegex())
}
fun String.isLetter(): Boolean {
    return this.all { it.isLetter() }
}
fun String.removeClearRound(): String {
    try {
        if (this.split(".")[1] == "00") {
            return this.split(".")[0]
        }
    } catch (e: Exception) {
    }
    return this
}

fun String.cleanRound(): String {
    try {
        if (this.split(".")[1].length > 1) {
            return this.split(".")[0] + "." + this.split(".")[1].substring(0, 1)
        }
    } catch (e: Exception) {
    }
    return this
}

fun Double.round(): String {
    return if (this % 1.0 != 0.0)
        String.format("%s", this)
    else
        String.format("%.0f", this)
}

fun Double.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return round(this * multiplier) / multiplier
}

fun Activity.hideKeyboard() {
    val imm: InputMethodManager =
        getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager

    var view: View? = currentFocus
    if (view == null) {
        view = View(this)
    }

    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun EditText.onFocus(bottomSheet: BottomSheetDialogFragment) {
    this.setOnFocusChangeListener { v, hasFocus ->
        if (hasFocus) {
            val bottomSheetInternal =
                bottomSheet.dialog!!.findViewById<View>(R.id.design_bottom_sheet)
            val height = Resources.getSystem().displayMetrics.heightPixels
            BottomSheetBehavior.from(bottomSheetInternal).peekHeight = (height * 6) / 7
        }
    }
}

fun View.setBackgroundShape(
    backgroundColor: Int,
    borderColor: Int,
    redii: FloatArray,
    strokeSize: Int,
    shapeIn: Int
) {
    val shape = GradientDrawable()
    shape.shape = shapeIn
    shape.cornerRadii = redii
    shape.setColor(backgroundColor)
    shape.setStroke(strokeSize.dpToPx(), borderColor)
    this.background = shape
}

fun View.setBackgroundShape(
    backgroundColor: Int,
    borderColor: Int,
    radius: Int,
    strokeSize: Int,
    shapeIn: Int
) {
    val shape = GradientDrawable()
    shape.shape = shapeIn
    shape.cornerRadii = floatArrayOf(
        (radius).dpToPx().toFloat(),
        (radius).dpToPx().toFloat(),
        (radius).dpToPx().toFloat(),
        (radius).dpToPx().toFloat(),
        (radius).dpToPx().toFloat(),
        (radius).dpToPx().toFloat(),
        (radius).dpToPx().toFloat(),
        (radius).dpToPx().toFloat()
    )
    shape.setColor(backgroundColor)
    shape.setStroke(strokeSize.dpToPx(), borderColor)
    this.background = shape
}

fun View.setBackgroundShape(backgroundColor: Int, radius: Int, shapeIn: Int) {
    val shape = GradientDrawable()
    shape.shape = shapeIn
    shape.cornerRadii = floatArrayOf(
        (radius).dpToPx().toFloat(),
        (radius).dpToPx().toFloat(),
        (radius).dpToPx().toFloat(),
        (radius).dpToPx().toFloat(),
        (radius).dpToPx().toFloat(),
        (radius).dpToPx().toFloat(),
        (radius).dpToPx().toFloat(),
        (radius).dpToPx().toFloat()
    )
    shape.setColor(backgroundColor)
    this.background = shape
}

fun TextView.setSpanColor(start: Int, end: Int, color: Int) {
    val wordToSpan: Spannable = SpannableString(this.text)

    wordToSpan.setSpan(
        ForegroundColorSpan(color),
        start,
        end,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    this.text = wordToSpan
}

fun <T : Any> String.toClass(clazz: Class<T>): T {
    val gson = Gson()
    return gson.fromJson(this, clazz)
}

fun <T> Gson.myFromJson(json: String, type: Type) = fromJson<T>(json, type)

fun TextView.setSpanFont(start: Int, end: Int, textSize: Float) {
    val smallSizeText = RelativeSizeSpan(textSize)
    val ssBuilder = SpannableStringBuilder(text)
    ssBuilder.setSpan(
        smallSizeText,
        start,
        end,
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    this.text = ssBuilder
}

fun Context.isNetworkAvailable(): Boolean {
    val connectivityManager =
        getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetworkInfo = connectivityManager.activeNetworkInfo

    return activeNetworkInfo?.isConnected ?: false
}

fun String.isMailValid(): Boolean {
    val regExpn = (
        "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@" +
            "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?" +
            "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\." +
            "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?" +
            "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|" +
            "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
        )

    val inputStr: CharSequence = this

    val pattern =
        Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE)
    val matcher = pattern.matcher(inputStr)

    return matcher.matches()
}

fun String.normalPhoneNumber(): String {
    var number = this

    if (number.startsWith("+1"))
        number = number.removePrefix("+")

    if (number.startsWith("+98"))
        number = number.replace("+98", "0")

    return number
}

fun String.getState(): String {
    when {
        this.toUpperCase().contains(", NSW") -> {
            return "NSW"
        }
        this.toUpperCase().contains(", QLD") -> {
            return "QLD"
        }
        this.toUpperCase().contains(", SA") -> {
            return "SA"
        }
        this.toUpperCase().contains(", TAS") -> {
            return "TAS"
        }
        this.toUpperCase().contains(", VIC") -> {
            return "VIC"
        }
        this.toUpperCase()
            .contains(", WA") -> {
            return "WA"
        }
        else -> return ""
    }
}

fun String.getShortAddress(): String {
    when {
        this.contains("New South Wales") -> {
            return this.replace("New South Wales", "NSW").replace(", Australia", "")
        }
        this.contains("Queensland") -> {
            return this.replace("Queensland", "QLD").replace(", Australia", "")
        }
        this.contains("South Australia") -> {
            return this.replace("South Australia", "SA").replace(", Australia", "")
        }
        this.contains("Tasmania") -> {
            return this.replace("Tasmania", "TAS").replace(", Australia", "")
        }
        this.contains("Victoria") -> {
            return this.replace("Victoria", "VIC").replace(", Australia", "")
        }
        this.contains("Western Australia") -> {
            return this.replace("Western Australia", "WA").replace(", Australia", "")
        }
        else -> return this.replace(", Australia", "")
    }
}

fun Double.appendMBT(): String {
    return when {
        this < MILLION -> this.roundToInt().toString().insertComma()
        this < BILLION -> "${floor(this.times(100).div(MILLION).times(0.01) * 100) / 100}M"
        this < TRILLION -> "${floor(this.times(100).div(BILLION).times(0.01) * 100) / 100}B"
        else -> "${floor(this.times(100).div(TRILLION).times(0.01) * 100) / 100}T"
    }
}

fun String.insertComma(): String {
    var amount = this
    if (amount.trim { it <= ' ' } == "") {
        return amount
    }
    if (Build.MANUFACTURER.toLowerCase().equals("motorola", ignoreCase = true)) return amount
    val buf = StringBuffer()
    var isNegative = false
    var isFloat = false
    var asharPart: String? = ""
    var retStr: String?
    val dotIndex = amount.indexOf(".")
    if (dotIndex != -1) {
        asharPart = amount.substring(dotIndex)
        amount = amount.substring(0, dotIndex)
        isFloat = true
    }
    var reversedAmount = StringBuffer(amount.trim { it <= ' ' }).reverse().toString()
    if (reversedAmount[amount.length - 1] == '-') {
        reversedAmount =
            reversedAmount.substring(0, amount.length - 1)
        isNegative = true
    }
    for (i in 0 until reversedAmount.length) {
        if (i > 0 && i % 3 == 0) {
            buf.append(reversedAmount, i - 3, i)
            buf.append(",")
        }
    }
    val length = reversedAmount.length
    val reminder: Int = length % 3
    if (length >= 3 && reminder == 0) {
        buf.append(reversedAmount, length - 3, length)
    } else {
        buf.append(reversedAmount, length - reminder, length)
    }
    retStr = if (isNegative) {
        '-'.toString() + buf.reverse().toString()
    } else {
        buf.reverse().toString()
    }
    if (isFloat) {
        retStr += asharPart
    }

    return retStr
}

fun String.cardFormat(): String {
    val str = this
    return if (Build.MANUFACTURER.toLowerCase().equals("motorola", ignoreCase = true)) {
        str
    } else str.substring(0, 4) + "-" + str.substring(4, 8) + "-" + str.substring(
        8,
        12
    ) + "-" + str.substring(12)
}

fun String.convertToEnglishDigits(): String {
    return try {
        this.replace("١".toRegex(), "1").replace("٢".toRegex(), "2")
            .replace("٣".toRegex(), "3").replace("٤".toRegex(), "4").replace("٥".toRegex(), "5")
            .replace("٦".toRegex(), "6").replace("٧".toRegex(), "7").replace("٨".toRegex(), "8")
            .replace("٩".toRegex(), "9").replace("٠".toRegex(), "0")
            .replace("۱".toRegex(), "1").replace("۲".toRegex(), "2").replace("۳".toRegex(), "3")
            .replace("۴".toRegex(), "4").replace("۵".toRegex(), "5")
            .replace("۶".toRegex(), "6").replace("۷".toRegex(), "7").replace("۸".toRegex(), "8")
            .replace("۹".toRegex(), "9").replace("۰".toRegex(), "0")
    } catch (e: Exception) {
        e.printStackTrace()
        this
    }
}

fun String.removeDash(): String = this.replace("-".toRegex(), "")

fun String.removeComma(): String = this.replace(",".toRegex(), "").replace("٫", "")

fun TextView.changeToRequired() {

    val wordToSpan: Spannable =
        SpannableString(this.text.toString() + " *")

    wordToSpan.setSpan(
        ForegroundColorSpan(Color.RED),
        this.text.toString().length + 1,
        this.text.toString().length + 2,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    this.text = wordToSpan
}

// fun AppCompatImageView.setShowHide(editText: EditText) {
//    this.setOnClickListener {
//        if (editText.inputType == InputType.TYPE_CLASS_TEXT) {
//            editText.inputType = 129
//            this.setImageResource(R.drawable.ic_eye)
//        } else {
//            editText.inputType = InputType.TYPE_CLASS_TEXT
//            this.setImageResource(R.drawable.ic_eye_invisible)
//        }
//    }
//
// }

fun String.isPhoneNumber(): Boolean {
    val number = this
    return when {
        (number.length < 11) or (number.length > 13) or (number.length == 12) -> false
        !((number.startsWith("+98")) or (number.startsWith("09"))) -> false
        else -> true
    }
}

fun Char.isPersian(): Boolean {
    val rtlCharacters =
        Pattern.compile("[\\u0600-\\u06FF\\u0750-\\u077F\\u0590-\\u05FF\\uFE70-\\uFEFF]")
    val matcher = rtlCharacters.matcher(this.toString())
    return matcher.find()
}
