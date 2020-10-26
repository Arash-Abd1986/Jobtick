package com.jobtick.fragments;

import android.view.View;

import androidx.fragment.app.Fragment;
import com.jobtick.interfaces.HasEditTextRegular;
import com.jobtick.utils.Helper;

import org.jetbrains.annotations.NotNull;

public class FragmentBase extends Fragment implements HasEditTextRegular {

    public FragmentBase(){ }

    @Override
    public void editTextOnClick(@NotNull View view) {
            view.requestFocus();
            Helper.openKeyboard(requireActivity());
    }
}
