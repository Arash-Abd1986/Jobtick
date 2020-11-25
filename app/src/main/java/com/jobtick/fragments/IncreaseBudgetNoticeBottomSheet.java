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

import com.jobtick.R;
import com.jobtick.models.TaskModel;
import com.jobtick.utils.ConstantKey;

public class IncreaseBudgetNoticeBottomSheet extends AbstractStateExpandedBottomSheet {


    TextView name;
    TextView description;
    TextView oldPrice;
    TextView newPrice;
    TextView reason;
    Button decline;
    Button accept;

    private TaskModel taskModel;
    private int pos;


    private NoticeListener listener;

    public static IncreaseBudgetNoticeBottomSheet newInstance(TaskModel taskModel, int pos){
        Bundle bundle = new Bundle();
        bundle.putParcelable(ConstantKey.TASK, taskModel);
        bundle.putInt(ConstantKey.POSITION, pos);
        IncreaseBudgetNoticeBottomSheet fragment = new IncreaseBudgetNoticeBottomSheet();
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

        View view = inflater.inflate(R.layout.bottom_sheet_increase_budget_notice, container, false);

        assert getArguments() != null;
        taskModel = getArguments().getParcelable(ConstantKey.TASK);
        pos = getArguments().getInt(ConstantKey.POSITION);


        name = view.findViewById(R.id.name);
        description = view.findViewById(R.id.description);
        newPrice = view.findViewById(R.id.new_price);
        oldPrice = view.findViewById(R.id.old_price);
        reason = view.findViewById(R.id.reason_description);
        decline = view.findViewById(R.id.btn_decline);
        accept = view.findViewById(R.id.btn_accept);

        decline.setOnClickListener(v -> {
            listener.onIncreaseBudgetAcceptClick();
        });

        accept.setOnClickListener(v -> {
            listener.onIncreaseBudgetRejectClick();
        });

        init();
        return view;
    }

    private void init(){
        name.setText(taskModel.getPoster().getName());
        description.setText(taskModel.getTitle());
//        reason.setText(taskModel.getRescheduleReqeust().get(pos).getReason());
//        newPrice.setText(taskModel.getRescheduleReqeust().get(pos).getNew_duedate());
//        oldPrice.setText(taskModel.getDueDate());
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
        void onIncreaseBudgetAcceptClick();
        void onIncreaseBudgetRejectClick();
    }
}