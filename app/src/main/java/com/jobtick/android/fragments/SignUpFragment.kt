package com.jobtick.android.fragments

import com.jobtick.android.activities.AuthActivity.EditTextError
import android.annotation.SuppressLint
import butterknife.BindView
import com.jobtick.android.R
import com.jobtick.android.widget.ExtendedEntryTextDiffId
import com.jobtick.android.widget.ExtendedEntryText
import com.google.android.material.button.MaterialButton
import android.widget.LinearLayout
import android.widget.TextView
import com.jobtick.android.activities.AuthActivity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.fragment.app.Fragment
import butterknife.ButterKnife
import butterknife.OnClick
import com.jobtick.android.fragments.SignInFragment
import com.jobtick.android.utils.ExternalIntentHelper
import com.jobtick.android.utils.Helper

/**
 * A simple [Fragment] subclass.
 */
class SignUpFragment : Fragment(), EditTextError {

    private var edtEmailAddress: ExtendedEntryTextDiffId? = null
    private var edtPassword: ExtendedEntryText? = null
    private var edtRepeatPassword: ExtendedEntryText? = null
    private var lytBtnSignUp: MaterialButton? = null
    private var lytBtnGoogle: LinearLayout? = null
    private var lytBtnFacebook: LinearLayout? = null
    private var txtBtnSignIn: TextView? = null
    private var txtBtnTerms: TextView? = null
    private var authActivity: AuthActivity? = null
    private var fragmentView: View? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (fragmentView != null) {
            return fragmentView
        }
        val view = inflater.inflate(R.layout.fragment_sign_up, container, false)
        fragmentView = view
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setIDS()
        onViewClick()
        authActivity = requireActivity() as AuthActivity
        if (authActivity != null) {
            authActivity!!.setEditTextError(this)
        }
    }

    private fun setIDS() {
        edtEmailAddress = requireView().findViewById(R.id.email)
        edtPassword = requireView().findViewById(R.id.password)
        edtRepeatPassword = requireView().findViewById(R.id.confirm_password)
        lytBtnSignUp = requireView().findViewById(R.id.lyt_btn_sign_up)
        txtBtnSignIn = requireView().findViewById(R.id.txt_btn_sign_in)
        txtBtnTerms = requireView().findViewById(R.id.txt_btn_terms)
        lytBtnGoogle = requireView().findViewById(R.id.lyt_btn_google)
        lytBtnFacebook = requireView().findViewById(R.id.lyt_btn_facebook)
    }

    private fun onViewClick() {
        lytBtnSignUp!!.setOnClickListener {
            authActivity!!.Signup(
                edtEmailAddress!!.text.trim { it <= ' ' },
                edtPassword!!.text.trim { it <= ' ' })
        }
        lytBtnFacebook!!.setOnClickListener { authActivity!!.facebookLogin(true) }
        lytBtnGoogle!!.setOnClickListener { authActivity!!.signInWithGoogle(true) }
        txtBtnSignIn!!.setOnClickListener {
            Helper.closeKeyboard(authActivity)
            val fragment: Fragment = SignInFragment()
            val ft = authActivity!!.supportFragmentManager.beginTransaction()
            ft.replace(R.id.auth_layout, fragment)
                .addToBackStack(SignInFragment::class.java.name)
            ft.commit()
        }
        txtBtnTerms!!.setOnClickListener {
            ExternalIntentHelper.openLink(
                requireActivity(),
                "https://www.jobtick.com/terms"
            )
        }

    }

    override fun onEmailError(emailError: String) {
        edtEmailAddress!!.setError(emailError)
    }

    override fun onPasswordError(passwordError: String) {
        edtPassword!!.setError(passwordError)
    }
}