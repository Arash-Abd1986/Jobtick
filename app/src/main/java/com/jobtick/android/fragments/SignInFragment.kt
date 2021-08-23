package com.jobtick.android.fragments

import com.jobtick.android.activities.AuthActivity.EditTextError
import com.jobtick.android.activities.AuthActivity
import com.jobtick.android.R
import com.jobtick.android.widget.ExtendedEntryTextDiffId
import com.jobtick.android.widget.ExtendedEntryText
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import android.widget.LinearLayout
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.fragment.app.Fragment
import com.jobtick.android.utils.Helper

/**
 * A simple [Fragment] subclass.
 */
class SignInFragment : Fragment(), EditTextError {
    private var authActivity: AuthActivity? = null

    var edtEmailAddress: ExtendedEntryTextDiffId? = null
    var edtPassword: ExtendedEntryText? = null
    var txtForgotPassword: TextView? = null
    var lytBtnSignIn: MaterialButton? = null
    var txtBtnSignUp: TextView? = null
    var socialBox: View? = null
    var lytBtnGoogle: LinearLayout? = null
    var lytBtnFacebook: LinearLayout? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_in, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setIDS()
        onViewClick()
        setSocialBox()
        authActivity = requireActivity() as AuthActivity
        if (authActivity != null) {
            authActivity!!.setEditTextError(this)
        }
    }


    private fun setIDS() {
        edtEmailAddress = requireView().findViewById(R.id.email)
        edtPassword = requireView().findViewById(R.id.password)
        txtForgotPassword = requireView().findViewById(R.id.txt_forgot_password)
        lytBtnSignIn = requireView().findViewById(R.id.lyt_btn_sign_in)
        txtBtnSignUp = requireView().findViewById(R.id.txt_btn_sign_up)
        socialBox = requireView().findViewById(R.id.socialBox)
        lytBtnGoogle = requireView().findViewById(R.id.lyt_btn_google)
        lytBtnFacebook = requireView().findViewById(R.id.lyt_btn_facebook)
    }

    private fun setSocialBox() {
        val tvGoogle = socialBox!!.findViewById<TextView>(R.id.tvGoogle)
        val tvFB = socialBox!!.findViewById<TextView>(R.id.tvFB)
        val other = socialBox!!.findViewById<TextView>(R.id.other)
        tvGoogle.text = getString(R.string.log_in_google_text)
        tvFB.text = getString(R.string.log_in_facebook_text)
        other.text = getString(R.string.other_login)
    }


    private  fun onViewClick() {
        txtForgotPassword!!.setOnClickListener {
            Helper.closeKeyboard(authActivity)
            val fragment: Fragment = ForgotPassword1Fragment()
            val ft = authActivity!!.supportFragmentManager.beginTransaction()
            ft.replace(R.id.auth_layout, fragment)
                .addToBackStack(ForgotPassword1Fragment::class.java.name)
            ft.addToBackStack(null)
            ft.commit()
        }

        lytBtnSignIn!!.setOnClickListener {
            if (validation()) {
                authActivity!!.login(
                    edtEmailAddress!!.text.trim { it <= ' ' },
                    edtPassword!!.text.trim { it <= ' ' })
            }
        }

        lytBtnGoogle!!.setOnClickListener {
            authActivity!!.signInWithGoogle(false)
        }
        lytBtnFacebook!!.setOnClickListener {
            authActivity!!.facebookLogin(false)
        }
        txtBtnSignUp!!.setOnClickListener {
            var fragment: Fragment = ForgotPassword1Fragment()
            var ft = authActivity!!.supportFragmentManager.beginTransaction()
            Helper.closeKeyboard(authActivity)
            fragment = SignUpFragment()
            ft = authActivity!!.supportFragmentManager.beginTransaction()
            ft.replace(R.id.auth_layout, fragment)
                .addToBackStack(SignUpFragment::class.java.name)
            ft.commit()
        }

    }

    private fun validation(): Boolean {
        if (TextUtils.isEmpty(edtEmailAddress!!.text.trim { it <= ' ' })) {
            edtEmailAddress!!.setError("Check your email address")
            return false
        } else if (TextUtils.isEmpty(edtPassword!!.text.trim { it <= ' ' })) {
            edtPassword!!.setError("Enter your password")
            return false
        } else if (edtPassword!!.text.trim { it <= ' ' }.length < 8) {
            edtPassword!!.setError("Password must be atleast 8 characters.")
            return false
        }
        return true
    }

    override fun onEmailError(emailError: String?) {
        edtEmailAddress!!.setError(emailError)
    }

    override fun onPasswordError(passwordError: String?) {
        edtPassword!!.setError(passwordError)
    }
}