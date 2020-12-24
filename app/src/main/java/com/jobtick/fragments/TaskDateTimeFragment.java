package com.jobtick.fragments;

import android.app.DatePickerDialog;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.jobtick.R;
import android.annotation.SuppressLint;
import timber.log.Timber;
import com.jobtick.text_view.TextViewMedium;
import com.jobtick.activities.TaskCreateActivity;
import com.jobtick.models.DueTimeModel;
import com.jobtick.models.TaskModel;
import com.jobtick.utils.Tools;
import com.jobtick.widget.ExtendedEntryText;

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
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_date)
    ExtendedEntryText txtDate;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.cb_morning)
    CheckBox cbMorning;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.cb_midday)
    CheckBox cbMidday;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.cb_afternoon)
    CheckBox cbAfternoon;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.cb_evening)
    CheckBox cbEvening;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.bottom_sheet)
    FrameLayout bottomSheet;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.img_details)
    ImageView imgDetails;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_details)
    TextView txtDetails;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.img_date_time)
    ImageView imgDateTime;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_date_time)
    TextView txtDateTime;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.img_budget)
    ImageView imgBudget;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_budget)
    TextView txtBudget;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_btn_details)
    LinearLayout lytBtnDetails;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_bnt_date_time)
    LinearLayout lytBntDateTime;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_btn_budget)
    LinearLayout lytBtnBudget;


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
        taskCreateActivity = (TaskCreateActivity) requireActivity();
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
            cbMorning.setChecked(task.getDueTime().getMorning());
            cbAfternoon.setChecked(task.getDueTime().getAfternoon());
            cbEvening.setChecked(task.getDueTime().getEvening());
            cbMidday.setChecked(task.getDueTime().getMidday());

        } else {
            cbMorning.setChecked(false);
            cbMidday.setChecked(false);
            cbAfternoon.setChecked(false);
            cbEvening.setChecked(false);
        }
        selectDateTimeBtn();
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

        txtDate.setExtendedViewOnClickListener(() ->{
            Calendar calendar = Calendar.getInstance();
            cyear = calendar.get(Calendar.YEAR);
            cmonth = calendar.get(Calendar.MONTH);
            cday = calendar.get(Calendar.DAY_OF_MONTH);
            showBottomSheetDialogDate();
        });

    }

    @OnClick({R.id.img_morning, R.id.txt_title_morning, R.id.txt_subtitle_morning,
            R.id.rlt_btn_morning, R.id.img_midday, R.id.txt_title_midday, R.id.txt_subtitle_midday,
            R.id.rlt_btn_midday, R.id.img_afternoon, R.id.txt_title_afternoon, R.id.txt_subtitle_afternoon,
            R.id.rlt_btn_afternoon, R.id.img_evening, R.id.txt_title_evening, R.id.txt_subtitle_evening,
            R.id.rlt_btn_evening, R.id.lyt_btn_back, R.id.lyt_btn_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_morning:
            case R.id.txt_title_morning:
            case R.id.txt_subtitle_morning:
            case R.id.rlt_btn_morning:
                cbMorning.setChecked(!cbMorning.isChecked());
                break;
            case R.id.img_midday:
            case R.id.txt_title_midday:
            case R.id.txt_subtitle_midday:
            case R.id.rlt_btn_midday:
                cbMidday.setChecked(!cbMidday.isChecked());
                break;
            case R.id.img_afternoon:
            case R.id.txt_title_afternoon:
            case R.id.txt_subtitle_afternoon:
            case R.id.rlt_btn_afternoon:
                cbAfternoon.setChecked(!cbAfternoon.isChecked());
                break;
            case R.id.img_evening:
            case R.id.txt_title_evening:
            case R.id.txt_subtitle_evening:
            case R.id.rlt_btn_evening:
                cbEvening.setChecked(!cbEvening.isChecked());
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
                        operationsListener.onBackClickDateTime(
                                Tools.getDayMonthDateTimeFormat(txtDate.getText().toString().trim()),
                                getDueTimeModel()
                        );
                    //    txtDate.setError("Please select date");
                        operationsListener.onValidDataFilledDateTimeBack();
                        break;
                    case 2:
                        operationsListener.onBackClickDateTime(
                                Tools.getDayMonthDateTimeFormat(txtDate.getText().toString().trim()),
                                getDueTimeModel()
                        );
                     //   taskCreateActivity.showToast("Select Due time", taskCreateActivity);
                        operationsListener.onValidDataFilledDateTimeBack();
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
        dueTimeModel.setMorning(cbMorning.isChecked());
        dueTimeModel.setAfternoon(cbAfternoon.isChecked());
        dueTimeModel.setEvening(cbEvening.isChecked());
        dueTimeModel.setMidday(cbMidday.isChecked());

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
        return Tools.getTimeStamp(date) < Tools.getTimeStamp(formattedDate);
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
        Timber.e("destory");
        super.onDestroy();

    }

    @Override
    public void onDestroyView() {
        Timber.e("destoryview");
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

        TextView txtCancel = view.findViewById(R.id.txt_cancel);
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

    private void selectDateTimeBtn() {
        ColorStateList csl_primary = AppCompatResources.getColorStateList(getContext(), R.color.colorPrimary);
        imgDateTime.setImageTintList(csl_primary);
        txtDateTime.setTextColor(getResources().getColor(R.color.colorPrimary));
        ColorStateList csl_grey = AppCompatResources.getColorStateList(getContext(), R.color.greyC4C4C4);
        ColorStateList csl_green = AppCompatResources.getColorStateList(getContext(), R.color.green);
        imgDetails.setImageTintList(csl_green);
        imgBudget.setImageTintList(csl_grey);
        txtDetails.setTextColor(getResources().getColor(R.color.green));
        txtBudget.setTextColor(getResources().getColor(R.color.colorGrayC9C9C9));
        tabClickListener();
    }

    private void tabClickListener() {
        if (getActivity() != null && getValidationCode() == 0) {
            lytBtnDetails.setOnClickListener(v -> ((TaskCreateActivity) requireActivity()).onViewClicked(v));
            lytBntDateTime.setOnClickListener(v -> ((TaskCreateActivity) requireActivity()).onViewClicked(v));
            lytBtnBudget.setOnClickListener(v -> ((TaskCreateActivity) requireActivity()).onViewClicked(v));
        }
    }
}
