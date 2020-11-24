package com.jobtick.fragments;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.jobtick.R;
import com.jobtick.models.TaskModel;
import com.jobtick.utils.ConstantKey;

import org.jetbrains.annotations.NotNull;

public class RescheduleNoticeDialogFragment extends DialogFragment {

    View close;
    TextView name;
    TextView description;
    TextView previousDate;
    TextView previousTime;
    TextView newTime;
    TextView reason;
    Button decline;
    Button accept;

    private TaskModel taskModel;
    private int pos;

    private Dialog dialog;


    private NoticeDialogListener listener;

    public static RescheduleNoticeDialogFragment newInstance(TaskModel taskModel, int pos){
        Bundle bundle = new Bundle();
        bundle.putParcelable(ConstantKey.TASK, taskModel);
        bundle.putInt(ConstantKey.POSITION, pos);
        RescheduleNoticeDialogFragment fragment = new RescheduleNoticeDialogFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_reschedule_notice_dialog, container, false);

        assert getArguments() != null;
        taskModel = getArguments().getParcelable(ConstantKey.TASK);
        pos = getArguments().getInt(ConstantKey.POSITION);


        close = view.findViewById(R.id.close);
        name = view.findViewById(R.id.name);
        description = view.findViewById(R.id.description);
        previousDate = view.findViewById(R.id.txt_previous_date);
        previousTime = view.findViewById(R.id.txt_previous_time);
        newTime = view.findViewById(R.id.txt_new_time);
        reason = view.findViewById(R.id.reason_description);
        decline = view.findViewById(R.id.btn_decline);
        accept = view.findViewById(R.id.btn_accept);

        decline.setOnClickListener(v -> {
            listener.onDialogNegativeClick(this, pos);
        });

        accept.setOnClickListener(v -> {
            listener.onDialogPositiveClick(this, pos);
        });

        close.setOnClickListener(v -> {
            dismiss();
        });

        init();
        return view;
    }

    private void init(){
        name.setText(taskModel.getPoster().getName());
        description.setText(taskModel.getTitle());
        reason.setText(taskModel.getRescheduleReqeust().get(pos).getReason());
        newTime.setText(taskModel.getRescheduleReqeust().get(pos).getNew_duedate());
        previousDate.setText(taskModel.getDueDate());

        if(taskModel.getDueTime().getMidday())
            previousTime.setText(R.string.anytime);
        if(taskModel.getDueTime().getMorning())
            previousTime.setText(R.string.morning);
        if(taskModel.getDueTime().getEvening())
            previousTime.setText(R.string.evening);
        if(taskModel.getDueTime().getAfternoon())
            previousTime.setText(R.string.afternoon);
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (NoticeDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(this.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    public @NotNull Dialog onCreateDialog(Bundle savedInstanceState) {
        dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = dialog.getWindow();

//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        window.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        int height = displayMetrics.heightPixels;
//        int width = displayMetrics.widthPixels;
//
//        WindowManager.LayoutParams params = window.getAttributes();
//        params.width = 800;
//        params.height = 1200;
//        window.setAttributes(params);

        return dialog;
    }

    public interface NoticeDialogListener {
        void onDialogPositiveClick(DialogFragment dialog, int pos);
        void onDialogNegativeClick(DialogFragment dialog, int pos);
    }
}
