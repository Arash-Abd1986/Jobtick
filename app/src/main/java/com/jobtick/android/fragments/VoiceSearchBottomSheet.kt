package com.jobtick.android.fragments

import android.annotation.SuppressLint
import android.content.Intent
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.widget.FrameLayout
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import com.jobtick.android.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import com.jobtick.android.utils.voicerecorder.MyRecognitionListener
import com.jobtick.android.utils.voicerecorder.RippleView

class VoiceSearchBottomSheet : BottomSheetDialogFragment() {
    private var close: AppCompatImageView? = null
    private var rvMic: RippleView? = null
    private var micButton: AppCompatImageView? = null
    private var title: TextView? = null

    lateinit var voiceRecorderInterface: VoiceRecorderInterface
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottom_sheet_voice_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        close = view.findViewById(R.id.close)
        rvMic = view.findViewById(R.id.rv_mic)
        title = view.findViewById(R.id.txt_title)
        micButton = view.findViewById(R.id.mic_button)
        close!!.setOnClickListener { dismiss() }
        rvMic!!.setOnClickListener {
            startRecord()
        }
        micButton!!.setOnClickListener {
            startRecord()
        }
        startRecord()
    }

    private fun startRecord() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                "com.domain.app")
        val recognizer = SpeechRecognizer
                .createSpeechRecognizer(requireContext())
        val listener = object : MyRecognitionListener() {
            @SuppressLint("SetTextI18n")
            override fun onResults(results: Bundle?) {
                val voiceResults = results!!.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (voiceResults == null) {
                    voiceRecorderInterface.onVoiceRecordResult(false, "")
                    println("No voice results")
                    title!!.text = getString(R.string.didnt_get_you)
                } else {
                    title!!.text = voiceResults[0]
                    voiceRecorderInterface.onVoiceRecordResult(true, voiceResults[0])

                }
            }

            override fun onError(error: Int) {
                title!!.text = getString(R.string.didnt_get_you)
                voiceRecorderInterface.onVoiceRecordResult(false, "")
                System.err.println("Error listening for speech: $error")
            }

            @SuppressLint("SetTextI18n")
            override fun onRmsChanged(p0: Float) {
                title!!.text = getString(R.string.listening)
                if (p0 > 6)
                    rvMic!!.newRipple()
                super.onRmsChanged(p0)
            }


        }
        recognizer.setRecognitionListener(listener)
        recognizer.startListening(intent)
    }

    interface VoiceRecorderInterface {
        fun onVoiceRecordResult(status: Boolean, result: String)
    }
}