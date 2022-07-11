package com.jobtick.android.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.jobtick.android.R
import com.jobtick.android.activities.AuthActivity
import com.jobtick.android.activities.AuthActivity.EditTextError
import com.jobtick.android.utils.ExternalIntentHelper
import com.jobtick.android.utils.Helper
import com.jobtick.android.widget.ExtendedEntryText
import com.jobtick.android.widget.ExtendedEntryTextDiffId

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
    private var cbTerms: AppCompatCheckBox? = null
    private var authActivity: AuthActivity? = null
    private var fragmentView: View? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
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
        cbTerms = requireView().findViewById(R.id.cb_terms)
    }

    private fun onViewClick() {
        lytBtnSignUp!!.setOnClickListener {
            authActivity!!.Signup(
                edtEmailAddress!!.text.trim { it <= ' ' },
                edtPassword!!.text.trim { it <= ' ' },
                cbTerms!!.isChecked
            )
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

    override fun onEmailError(emailError: String?) {
        edtEmailAddress!!.setError(emailError)
    }

    override fun onPasswordError(passwordError: String?) {
        edtPassword!!.setError(passwordError)
    }
}
