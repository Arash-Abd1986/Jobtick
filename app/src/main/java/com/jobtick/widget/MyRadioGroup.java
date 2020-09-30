package com.jobtick.widget;

import android.view.View;
import android.view.ViewParent;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import java.util.ArrayList;
import java.util.List;

public class MyRadioGroup {
    List<RadioButton> radios = new ArrayList<RadioButton>();

    /**
     * Constructor, which allows you to pass number of RadioButton instances,
     * making a group.
     *
     * @param radios One RadioButton or more.
     */
    public MyRadioGroup(RadioButton... radios) {
        super();

        for (RadioButton rb : radios) {
            this.radios.add(rb);
            rb.setOnClickListener(onClick);
        }
    }

    /**
     * Constructor, which allows you to pass number of RadioButtons
     * represented by resource IDs, making a group.
     *
     * @param dashboardActivity
     * @param radiosIDs One RadioButton or more.
     */
    public MyRadioGroup(View view, int... radiosIDs) {
        super();

        for (int radioButtonID : radiosIDs) {
            RadioButton rb = (RadioButton) view.findViewById(radioButtonID);
            if (rb != null) {
                this.radios.add(rb);
                rb.setOnClickListener(onClick);
            }
        }
    }

    /**
     * This occurs everytime when one of RadioButtons is clicked,
     * and deselects all others in the group.
     */
    View.OnClickListener onClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            // let's deselect all radios in group
            for (RadioButton rb : radios) {

                ViewParent p = rb.getParent();
                if (p.getClass().equals(RadioGroup.class)) {
                    // if RadioButton belongs to RadioGroup,
                    // then deselect all radios in it
                    RadioGroup rg = (RadioGroup) p;
                    rg.clearCheck();
                } else {
                    // if RadioButton DOES NOT belong to RadioGroup,
                    // just deselect it
                    rb.setChecked(false);
                }
            }

            // now let's select currently clicked RadioButton
            if (v.getClass().equals(RadioButton.class)) {
                RadioButton rb = (RadioButton) v;
                rb.setChecked(true);
            }

        }
    };

}
