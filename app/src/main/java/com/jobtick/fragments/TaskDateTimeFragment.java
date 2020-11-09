package com.jobtick.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.jobtick.R;
import com.jobtick.TextView.TextViewMedium;
import com.jobtick.activities.TaskCreateActivity;
import com.jobtick.models.DueTimeModel;
import com.jobtick.models.TaskModel;
import com.jobtick.utils.Tools;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TaskDateTimeFragment extends Fragment {

    TaskModel task;
    OperationsListener operationsListener;
    @BindView(R.id.txt_date)
    TextView txtDate;
    @BindView(R.id.cb_morning)
    CheckBox cbMorning;
    @BindView(R.id.cb_midday)
    CheckBox cbMidday;
    @BindView(R.id.cb_afternoon)
    CheckBox cbAfternoon;
    @BindView(R.id.cb_evening)
    CheckBox cbEvening;
    @BindView(R.id.bottom_sheet)
    FrameLayout bottomSheet;


    private TaskCreateActivity taskCreateActivity;
    private int cyear, cmonth, cday;
    private String str_due_date = null;
    private BottomSheetDialog mBottomSheetDialog;

    public static TaskDateTimeFragment newInstance(String due_date, DueTimeModel due_time, OperationsListener operationsListener) {

        Bundle args = new Bundle();
        args.putString("DUE_DATE", due_date);
        args.putParcelable("DUE_TIME", due_time);
        TaskDateTimeFragment fragment = new TaskDateTimeFragment();
        fragment.operationsListener = operationsListener;
        fragment.setArguments(args);
        return fragment;
    }

    public TaskDateTimeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_task_date_time, container, false);
        ButterKnife.bind(this, view);
        taskCreateActivity = (TaskCreateActivity) getActivity();
        task = new TaskModel();
        task.setDueDate(Tools.getDayMonthDateTimeFormat(getArguments().getString("DUE_DATE")));
        task.setDueTime(getArguments().getParcelable("DUE_TIME"));


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DatePickerDialog.OnDateSetListener mDateSetListener = (view1, year, month, dayOfMonth) -> {
            month = month + 1;
            str_due_date = Tools.getDayMonthDateTimeFormat(year + "-" + month + "-" + dayOfMonth);
            txtDate.setText(str_due_date);
        };
        txtDate.setText(task.getDueDate());
        if (task.getDueTime() != null) {
            if (task.getDueTime().getMorning()) {
                cbMorning.setChecked(true);
            } else {
                cbMorning.setChecked(false);
            }
            if (task.getDueTime().getAfternoon()) {
                cbAfternoon.setChecked(true);
            } else {
                cbAfternoon.setChecked(false);
            }
            if (task.getDueTime().getEvening()) {
                cbEvening.setChecked(true);
            } else {
                cbEvening.setChecked(false);
            }
            if (task.getDueTime().getMidday()) {
                cbMidday.setChecked(true);
            } else {
                cbMidday.setChecked(false);
            }

        } else {
            cbMorning.setChecked(false);
            cbMidday.setChecked(false);
            cbAfternoon.setChecked(false);
            cbEvening.setChecked(false);
        }

        taskCreateActivity.setActionDraftDateTime(taskModel -> {
            if (taskModel.getDueDate() != null) {
                operationsListener.draftTaskDateTime(taskModel, true);
            } else {
                if (!TextUtils.isEmpty(txtDate.getText().toString().trim()) && !checkDateTodayOrOnwords()) {
                    taskModel.setDueDate(Tools.getDayMonthDateTimeFormat(txtDate.getText().toString().trim()));
                }
                taskModel.setDueTime(getDueTimeModel());
                operationsListener.draftTaskDateTime(taskModel, false);
            }
        });

        cbMorning.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                cbAfternoon.setChecked(false);
                cbEvening.setChecked(false);
                cbMidday.setChecked(false);
            }
        });

        cbAfternoon.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                cbMorning.setChecked(false);
                cbEvening.setChecked(false);
                cbMidday.setChecked(false);
            }
        });

        cbEvening.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                cbAfternoon.setChecked(false);
                cbMorning.setChecked(false);
                cbMidday.setChecked(false);
            }
        });


        cbMidday.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                cbAfternoon.setChecked(false);
                cbEvening.setChecked(false);
                cbMorning.setChecked(false);
            }
        });

    }

    @OnClick({R.id.txt_date, R.id.img_morning, R.id.txt_title_morning, R.id.txt_subtitle_morning,
            R.id.rlt_btn_morning, R.id.img_midday, R.id.txt_title_midday, R.id.txt_subtitle_midday,
            R.id.rlt_btn_midday, R.id.img_afternoon, R.id.txt_title_afternoon, R.id.txt_subtitle_afternoon,
            R.id.rlt_btn_afternoon, R.id.img_evening, R.id.txt_title_evening, R.id.txt_subtitle_evening,
            R.id.rlt_btn_evening, R.id.lyt_btn_back, R.id.lyt_btn_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.txt_date:
                Calendar calendar = Calendar.getInstance();
                cyear = calendar.get(Calendar.YEAR);
                cmonth = calendar.get(Calendar.MONTH);
                cday = calendar.get(Calendar.DAY_OF_MONTH);
                showBottomSheetDialogDate();
                break;
            case R.id.img_morning:
            case R.id.txt_title_morning:
            case R.id.txt_subtitle_morning:
            case R.id.rlt_btn_morning:
                if (cbMorning.isChecked()) {
                    cbMorning.setChecked(false);
                } else {
                    cbMorning.setChecked(true);
                }
                break;
            case R.id.img_midday:
            case R.id.txt_title_midday:
            case R.id.txt_subtitle_midday:
            case R.id.rlt_btn_midday:
                if (cbMidday.isChecked()) {
                    cbMidday.setChecked(false);
                } else {
                    cbMidday.setChecked(true);
                }
                break;
            case R.id.img_afternoon:
            case R.id.txt_title_afternoon:
            case R.id.txt_subtitle_afternoon:
            case R.id.rlt_btn_afternoon:
                if (cbAfternoon.isChecked()) {
                    cbAfternoon.setChecked(false);
                } else {
                    cbAfternoon.setChecked(true);
                }
                break;
            case R.id.img_evening:
            case R.id.txt_title_evening:
            case R.id.txt_subtitle_evening:
            case R.id.rlt_btn_evening:
                if (cbEvening.isChecked()) {
                    cbEvening.setChecked(false);
                } else {
                    cbEvening.setChecked(true);
                }
                break;
            case R.id.lyt_btn_back:

                switch (getValidationCode()) {
                    case 0:
                        //success
                        operationsListener.onBackClickDateTime(
                                Tools.getDayMonthDateTimeFormat(txtDate.getText().toString().trim()),
                                getDueTimeModel()
                        );
                        operationsListener.onValidDataFilledDateTimeBack();
                        break;
                    case 1:
                        txtDate.setError("Please select date");
                        break;
                    case 2:
                        taskCreateActivity.showToast("Select Due time", taskCreateActivity);
                        break;
                }

                break;
            case R.id.lyt_btn_next:
                switch (getValidationCode()) {
                    case 0:
                        //success
                        operationsListener.onNextClickDateTime(
                                txtDate.getText().toString().trim(),
                                getDueTimeModel()
                        );
                        operationsListener.onValidDataFilledDateTimeNext();
                        break;
                    case 1:
                        txtDate.setError("Please select date");
                        break;
                    case 2:
                        taskCreateActivity.showToast("Select Due time", taskCreateActivity);
                        break;
                    case 3:
                        taskCreateActivity.showToast("Due Date is old", taskCreateActivity);
                        break;
                }
                break;
        }
    }

    private DueTimeModel getDueTimeModel() {
        DueTimeModel dueTimeModel = new DueTimeModel();
        if (cbMorning.isChecked()) {
            dueTimeModel.setMorning(true);
        } else {
            dueTimeModel.setMorning(false);
        }
        if (cbAfternoon.isChecked()) {
            dueTimeModel.setAfternoon(true);
        } else {
            dueTimeModel.setAfternoon(false);
        }
        if (cbEvening.isChecked()) {
            dueTimeModel.setEvening(true);
        } else {
            dueTimeModel.setEvening(false);
        }
        if (cbMidday.isChecked()) {
            dueTimeModel.setMidday(true);
        } else {
            dueTimeModel.setMidday(false);
        }

        return dueTimeModel;
    }


    private int getValidationCode() {
        if (TextUtils.isEmpty(txtDate.getText().toString().trim())) {
            return 1;
        } else if (!cbMorning.isChecked() && !cbEvening.isChecked() && !cbAfternoon.isChecked() && !cbMidday.isChecked()) {
            return 2;
        } else if (checkDateTodayOrOnwords()) {
            return 3;
        }
        return 0;
    }

    private boolean checkDateTodayOrOnwords() {
        String date = txtDate.getText().toString().trim();
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        if (Tools.getTimeStamp(date) >= Tools.getTimeStamp(formattedDate)) {
            return false;
        } else {
            return true;
        }
    }

    public interface OperationsListener {
        void onNextClickDateTime(String due_date, DueTimeModel dueTimeModel);

        void onBackClickDateTime(String due_date, DueTimeModel dueTimeModel);

        void onValidDataFilledDateTimeNext();

        void onValidDataFilledDateTimeBack();

        void draftTaskDateTime(TaskModel taskModel, boolean moveForword);
    }

    @Override
    public void onDestroy() {
        Log.e("datetime", "destory");
        super.onDestroy();

    }

    @Override
    public void onDestroyView() {
        Log.e("datetime", "destoryview");
        super.onDestroyView();
    }


    private void showBottomSheetDialogDate() {

        final View view = getLayoutInflater().inflate(R.layout.sheet_date, null);

        mBottomSheetDialog = new BottomSheetDialog(taskCreateActivity);
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);


        CalendarView calendarView = view.findViewById(R.id.calenderView);
        calendarView.setMinDate(System.currentTimeMillis());


        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, 2); // subtract 2 years from now

        calendarView.setMaxDate(c.getTimeInMillis());

        TextViewMedium txtCancel = view.findViewById(R.id.txt_cancel);
        txtCancel.setOnClickListener(v -> {
            mBottomSheetDialog.dismiss();
        });

        LinearLayout lytBtnDone = view.findViewById(R.id.lyt_btn_done);

        Calendar calendar = Calendar.getInstance();
        cyear = calendar.get(Calendar.YEAR);

        cmonth = calendar.get(Calendar.MONTH);
        cmonth = cmonth + 1;

        cday = calendar.get(Calendar.DAY_OF_MONTH);
        lytBtnDone.setOnClickListener(v -> {


            str_due_date = Tools.getDayMonthDateTimeFormat(cyear + "-" + cmonth + "-" + cday);
            txtDate.setText(str_due_date);

            mBottomSheetDialog.dismiss();

        });

        calendarView.setOnDateChangeListener((arg0, year, month, date) -> {

            cmonth = month + 1;
            cyear = year;
            cday = date;
        });


        // set background transparent
        ((View) view.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));

        mBottomSheetDialog.show();
        mBottomSheetDialog.setOnDismissListener(dialog -> mBottomSheetDialog = null);

    }


}
