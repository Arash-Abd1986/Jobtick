package com.jobtick.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.jobtick.R;
import com.jobtick.models.TaskModel;
import com.jobtick.utils.ConstantKey;

public class RescheduleNoticeBottomSheet extends BottomSheetDialogFragment {

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


    private NoticeListener listener;

    public static RescheduleNoticeBottomSheet newInstance(TaskModel taskModel, int pos){
        Bundle bundle = new Bundle();
        bundle.putParcelable(ConstantKey.TASK, taskModel);
        bundle.putInt(ConstantKey.POSITION, pos);
        RescheduleNoticeBottomSheet fragment = new RescheduleNoticeBottomSheet();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.ThemeOverlay_BottomSheetDialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.bottom_sheet_reschedule_notice, container, false);

        assert getArguments() != null;
        taskModel = getArguments().getParcelable(ConstantKey.TASK);
        pos = getArguments().getInt(ConstantKey.POSITION);


        name = view.findViewById(R.id.name);
        description = view.findViewById(R.id.description);
        previousDate = view.findViewById(R.id.txt_previous_date);
        previousTime = view.findViewById(R.id.txt_previous_time);
        newTime = view.findViewById(R.id.txt_new_time);
        reason = view.findViewById(R.id.reason_description);
        decline = view.findViewById(R.id.btn_decline);
        accept = view.findViewById(R.id.btn_accept);

        decline.setOnClickListener(v -> {
            listener.onRescheduleTimeDeclineClick(this, pos);
        });

        accept.setOnClickListener(v -> {
            listener.onRescheduleTimeAcceptClick(this, pos);
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
            listener = (NoticeListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(this.toString()
                    + " must implement NoticeListener");
        }
    }


    public interface NoticeListener {
        void onRescheduleTimeAcceptClick(DialogFragment dialog, int pos);
        void onRescheduleTimeDeclineClick(DialogFragment dialog, int pos);
    }
}
