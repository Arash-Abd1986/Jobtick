package com.jobtick.android.fragments

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.jobtick.android.R
import com.jobtick.android.activities.ReferAFriendActivity
import com.jobtick.android.utils.SessionManager


class InviteFriendsFragment : Fragment() {
    private var link = "https://www.jobtick.com/?auth=invite&referrer="
    private var sessionManager: SessionManager? = null
    private lateinit var txtInviteLink: TextView
    private lateinit var rlInvite: RelativeLayout
    private lateinit var copy: TextView
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_invite_friends, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(context)
        link += sessionManager!!.userAccount.id
        txtInviteLink = requireView().findViewById(R.id.txt_invite_link)
        rlInvite = requireView().findViewById(R.id.rl_invite)
        copy = requireView().findViewById(R.id.copy)
        copy.setOnClickListener {
            copyLink()
        }
        rlInvite.setOnClickListener {
            shareLink()
        }
        txtInviteLink.text = link

    }

    private fun shareLink() {
        try {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_TEXT,
                    """Say yes to ${capitalize(sessionManager!!.userAccount.fname, true)} ${capitalize(sessionManager!!.userAccount.lname, false)} VIP Invitation and receive $10 towards your first job completion. 
Sign-up using the link to join our professional and welcoming community and start your journey on JOBTICK. 

$link""")
            sendIntent.type = "text/plain"
            startActivity(sendIntent)
        } catch (e: Exception) {
        }
    }

    fun capitalize(str: String?, isFname: Boolean): String {
        return if (str == null || str.isEmpty()) {
            if (isFname) "Jobtick" else "user"
        } else str.substring(0, 1).toUpperCase() + str.substring(1)
    }

    private var clipboardManager: ClipboardManager? = null
    private fun copyLink() {
        val clip = ClipData.newPlainText("link", link)
        clipboardManager = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboardManager!!.setPrimaryClip(clip)
        (requireActivity() as ReferAFriendActivity).showSuccessToast("Link copied", requireContext())
    }
}