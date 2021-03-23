package com.jobtick.android.utils

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import com.jobtick.android.widget.ExtendedEntryText
import java.util.*

class NumberTextWatcherForSlash(internal var editText: ExtendedEntryText) : TextWatcher {
    private var sPrev = ""
    private var iMon = 0
    private var iYear = 0
    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

        try {
            val sNew = s.toString()
            val newLen = sNew.length
            if (sNew == sPrev) {
                return
            }
            when (newLen) {
                0 -> {
                    iMon = 0
                    iYear = 0
                    sPrev = sNew
                }
                1 -> {
                    iMon = sNew.toInt()
                    iYear = 0
                    if (sPrev.length == 0 && iMon > 1) {    // Si se escribe un número mayor que 1, lo tomo como mes
                        sPrev = String.format("%02d/", iMon)
                    } else {
                        sPrev = sNew
                    }
                }
                2 -> {
                    iMon = sNew.toInt()
                    iYear = 0
                    if (sPrev.length == 1) {
                        // Si ya es un mes válido, lo completo, sino dejo
                        // sPrev sin cambios hasta que se ingrese algo válido
                        if (iMon >= 1 && iMon <= 12) {
                            sPrev = String.format("%02d/", iMon)
                        }
                    } else {
                        sPrev = sNew
                    }
                }
                3 -> {
                    iMon = sNew.substring(0, 2).toInt()
                    iYear = 0
                    if (sPrev.length == 2) {
                        iMon = sNew.substring(0, 2).toInt()
                        iYear = sNew.substring(2, 3).toInt()
                        sPrev = String.format("%02d/%d", iMon, iYear)
                    } else {
                        sPrev = sNew
                    }
                }
                4, 5 -> {
                    iMon = sNew.substring(0, 2).toInt()
                    iYear = sNew.substring(3, newLen).toInt()
                    sPrev = sNew
                }
                else -> sPrev = sNew
            }
            editText.setText(sPrev)
            editText.setSelection(sPrev.length)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun afterTextChanged(s: Editable) {}
}