package com.jobtick.android.utils;

import android.content.Context;

import com.jobtick.android.R;

public class StateHelper {

    private final String[] abrs;
    private final String[] states;

    public StateHelper(Context context){
         abrs = context.getResources().getStringArray(R.array.au_states_array_abr);
         states = context.getResources().getStringArray(R.array.au_states_array);
    }


    public String getStateAbr(String state) {
        int index = 0;
        for (; index < 8; index++) {
            if (states[index].equalsIgnoreCase(state.trim()))
                break;
        }
        if(index == 8)
            throw new IllegalStateException("state is not in the list");

        return abrs[index];
    }

    public String getStateName(String abr) {
        int index = 0;
        for (; index < 8; index++) {
            if (abr.equalsIgnoreCase(abrs[index]))
                break;
        }
        if(index == 8)
            throw new IllegalStateException("selected state is not in the list");

        return states[index];
    }

    public String[] getStates() {
        return states;
    }

    public boolean isCorrectState(String state){
        int index = 0;
        for (; index < 8; index++) {
            if (states[index].equalsIgnoreCase(state.trim()))
                break;
        }
        return index != 8;
    }

    public String[] getAbrs() {
        return abrs;
    }
}
