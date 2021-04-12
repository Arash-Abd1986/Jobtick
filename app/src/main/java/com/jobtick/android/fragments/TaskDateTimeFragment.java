package com.jobtick.android.fragments;

import android.app.DatePickerDialog;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.CalendarView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.jobtick.android.R;

import android.annotation.SuppressLint;

import timber.log.Timber;

import com.jobtick.android.activities.TaskCreateActivity;
import com.jobtick.android.models.DueTimeModel;
import com.jobtick.android.models.TaskModel;
import com.jobtick.android.utils.Tools;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TaskDateTimeFragment extends Fragment implements TextWatcher {

    TaskModel task;
    OperationsListener operationsListener;

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
    @BindView(R.id.card_date_time)
    CardView cardDateTime;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.card_details)
    CardView cardDetails;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_btn_budget)
    LinearLayout lytBtnBudget;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_btn_next)
    MaterialButton btnNext;


    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.spinner_items)
    LinearLayout spinnerItems;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.edt_time_spinner)
    MaterialTextView edtTimeSpinner;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.spinner_morning)
    MaterialTextView spinnerMorning;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.spinner_afternoon)
    MaterialTextView spinnerAfternoon;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.spinner_evening)
    MaterialTextView spinnerEvening;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.spinner_anytime)
    MaterialTextView spinnerAnytime;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.spinner_arrow)
    AppCompatImageView spinnerArrow;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.calenderView)
    CalendarView calendarView;
    private Boolean isSpinnerOpen = false;


    private TaskCreateActivity taskCreateActivity;
    private int cyear, cmonth, cday;
    private String str_due_date = null;
    private String txtDate = "";

    private Boolean cbMorning = (false);
    private Boolean cbAnyTime = (false);
    private Boolean cbAfternoon = (false);
    private Boolean cbEvening = (false);

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

        //lytBntDateTime.setBackgroundResource(R.drawable.rectangle_round_white_with_shadow);
        cardDateTime.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
        cardDetails.setOnClickListener(v -> {
            switch (getValidationCode()) {
                case 0:
                    //success
                    operationsListener.onBackClickDateTime(
                            Tools.getDayMonthDateTimeFormat(txtDate.trim()),
                            getDueTimeModel()
                    );
                    operationsListener.onValidDataFilledDateTimeBack();
                    break;
                case 1:
                    operationsListener.onBackClickDateTime(
                            Tools.getDayMonthDateTimeFormat(txtDate.trim()),
                            getDueTimeModel()
                    );
                    //    txtDate.setError("Please select date");
                    operationsListener.onValidDataFilledDateTimeBack();
                    break;
                case 2:
                    operationsListener.onBackClickDateTime(
                            Tools.getDayMonthDateTimeFormat(txtDate.trim()),
                            getDueTimeModel()
                    );
                    //   taskCreateActivity.showToast("Select Due time", taskCreateActivity);
                    operationsListener.onValidDataFilledDateTimeBack();
            }
        });
        Calendar calendar = Calendar.getInstance();
        cyear = calendar.get(Calendar.YEAR);
        cmonth = calendar.get(Calendar.MONTH);
        cday = calendar.get(Calendar.DAY_OF_MONTH);
        edtTimeSpinner.addTextChangedListener(this);
        DatePickerDialog.OnDateSetListener mDateSetListener = (view1, year, month, dayOfMonth) -> {
            month = month + 1;
            str_due_date = Tools.getDayMonthDateTimeFormat(year + "-" + month + "-" + dayOfMonth);
            txtDate = str_due_date;
        };
        txtDate = task.getDueDate();
        if (task.getDueTime() != null) {
            cbMorning = (task.getDueTime().getMorning());
            cbAfternoon = (task.getDueTime().getAfternoon());
            cbEvening = (task.getDueTime().getEvening());
            cbAnyTime = (task.getDueTime().getAnytime());

        } else {
            cbMorning = (false);
            cbAnyTime = (false);
            cbAfternoon = (false);
            cbEvening = (false);
        }
        selectDateTimeBtn();
        taskCreateActivity.setActionDraftDateTime(taskModel -> {
            if (taskModel.getDueDate() != null) {
                operationsListener.draftTaskDateTime(taskModel, true);
            } else {
                if (!TextUtils.isEmpty(txtDate.trim()) && !checkDateTodayOrOnwords()) {
                    taskModel.setDueDate(Tools.getDayMonthDateTimeFormat(txtDate.trim()));
                }
                taskModel.setDueTime(getDueTimeModel());
                operationsListener.draftTaskDateTime(taskModel, false);
            }
        });

        spinnerMorning.setOnClickListener(v -> {
            cbMorning = true;
            cbAfternoon = (false);
            cbEvening = (false);
            cbAnyTime = (false);
            edtTimeSpinner.setText(R.string.morning_s);
            hideSpinner();
        });

        spinnerAfternoon.setOnClickListener(v -> {
            cbAfternoon = true;
            cbMorning = (false);
            cbEvening = (false);
            cbAnyTime = (false);

            edtTimeSpinner.setText(R.string.afternoon_s);
            hideSpinner();
        });

        spinnerEvening.setOnClickListener(v -> {
            cbEvening = true;
            cbAfternoon = (false);
            cbMorning = (false);
            cbAnyTime = (false);

            edtTimeSpinner.setText(R.string.evening_s);
            hideSpinner();
        });


        spinnerAnytime.setOnClickListener(v -> {
            cbAnyTime = true;
            cbAfternoon = (false);
            cbEvening = (false);
            cbMorning = (false);

            edtTimeSpinner.setText(R.string.anytime_s);
            hideSpinner();
        });


        edtTimeSpinner.setOnClickListener(v -> {
            if (isSpinnerOpen) {
                hideSpinner();
            } else {
                showSpinner();
            }
        });

        confDate();
    }

    @OnClick({R.id.txt_title_morning, R.id.txt_subtitle_morning,
            R.id.rlt_btn_morning, R.id.txt_title_anytime, R.id.txt_subtitle_anytime,
            R.id.rlt_btn_anytime, R.id.txt_title_afternoon, R.id.txt_subtitle_afternoon,
            R.id.rlt_btn_afternoon, R.id.txt_title_evening,R.id.lyt_btn_back, R.id.txt_subtitle_evening,
            R.id.rlt_btn_evening, R.id.lyt_btn_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.txt_title_morning:
            case R.id.txt_subtitle_morning:
            case R.id.rlt_btn_morning:
                cbMorning = (!cbMorning);
                break;
            case R.id.txt_title_anytime:
            case R.id.txt_subtitle_anytime:
            case R.id.rlt_btn_anytime:
                cbAnyTime = (!cbAnyTime);
                break;
            case R.id.txt_title_afternoon:
            case R.id.txt_subtitle_afternoon:
            case R.id.rlt_btn_afternoon:
                cbAfternoon = (!cbAfternoon);
                break;
            case R.id.txt_title_evening:
            case R.id.txt_subtitle_evening:
            case R.id.rlt_btn_evening:
                cbEvening = (!cbEvening);
                break;
            case R.id.lyt_btn_back:

                switch (getValidationCode()) {
                    case 0:
                        //success
                        operationsListener.onBackClickDateTime(
                                Tools.getDayMonthDateTimeFormat(txtDate.trim()),
                                getDueTimeModel()
                        );
                        operationsListener.onValidDataFilledDateTimeBack();
                        break;
                    case 1:
                        operationsListener.onBackClickDateTime(
                                Tools.getDayMonthDateTimeFormat(txtDate.trim()),
                                getDueTimeModel()
                        );
                        //    txtDate.setError("Please select date");
                        operationsListener.onValidDataFilledDateTimeBack();
                        break;
                    case 2:
                        operationsListener.onBackClickDateTime(
                                Tools.getDayMonthDateTimeFormat(txtDate.trim()),
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
                                txtDate.trim(),
                                getDueTimeModel()
                        );
                        operationsListener.onValidDataFilledDateTimeNext();
                        break;
                    case 1:
                        // txtDate.setError("Please select date");
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
        dueTimeModel.setMorning(cbMorning);
        dueTimeModel.setAfternoon(cbAfternoon);
        dueTimeModel.setEvening(cbEvening);
        dueTimeModel.setAnytime(cbAnyTime);

        return dueTimeModel;
    }


    private int getValidationCode() {
        if (TextUtils.isEmpty(txtDate.trim())) {
            return 1;
        } else if (!cbMorning && !cbEvening && !cbAfternoon && !cbAnyTime) {
            return 2;
        } else if (checkDateTodayOrOnwords()) {
            return 3;
        }
        return 0;
    }

    private boolean checkDateTodayOrOnwords() {
        String selectedDate = txtDate.trim();

        Calendar rightNow = Calendar.getInstance();
        Date rightNowTime = rightNow.getTime();
        int currentHourIn24Format = rightNow.get(Calendar.HOUR_OF_DAY);
        System.out.println("Current time => " + rightNowTime);

        SimpleDateFormat df = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
        String formattedRightNowDate = df.format(rightNowTime);

        if(Tools.getTimeStamp(selectedDate).equals(Tools.getTimeStamp(formattedRightNowDate))){
            if(cbMorning){
                if( currentHourIn24Format >= 12)
                    return true;

            }
            if(cbAfternoon){
                if( currentHourIn24Format >= 18)
                    return true;

            }
        }

        return !(Tools.getTimeStamp(selectedDate) >= Tools.getTimeStamp(formattedRightNowDate));
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (getValidationCode() == 0) {
            ((TaskCreateActivity) getActivity()).setDateTimeComplete(true);
            btnNext.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.shape_rounded_back_button_active));
        } else {
            ((TaskCreateActivity) getActivity()).setDateTimeComplete(false);
            btnNext.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.shape_rounded_back_button_deactive));
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
        Timber.e("destory");
        super.onDestroy();

    }

    @Override
    public void onDestroyView() {
        Timber.e("destoryview");
        super.onDestroyView();
    }


    private void confDate() {
        str_due_date = Tools.getDayMonthDateTimeFormat(cyear + "-" + cmonth + "-" + cday);
        txtDate = (str_due_date);
        calendarView.setMinDate(System.currentTimeMillis());
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, 2); // subtract 2 years from now
        calendarView.setMaxDate(c.getTimeInMillis());

        Calendar calendar = Calendar.getInstance();
        cyear = calendar.get(Calendar.YEAR);

        cmonth = calendar.get(Calendar.MONTH);
        cmonth = cmonth + 1;
        cday = calendar.get(Calendar.DAY_OF_MONTH);
        str_due_date = Tools.getDayMonthDateTimeFormat(cyear + "-" + cmonth + "-" + cday);
        txtDate = (str_due_date);
        if (getValidationCode() == 0) {
            ((TaskCreateActivity) getActivity()).setDateTimeComplete(true);
            btnNext.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.shape_rounded_back_button_active));
        } else {
            ((TaskCreateActivity) getActivity()).setDateTimeComplete(false);
            btnNext.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.shape_rounded_back_button_deactive));
        }
        calendarView.setOnDateChangeListener((arg0, year, month, date) -> {
            cmonth = month + 1;
            cyear = year;
            cday = date;
            str_due_date = Tools.getDayMonthDateTimeFormat(cyear + "-" + cmonth + "-" + cday);
            txtDate = (str_due_date);
            if (getValidationCode() == 0) {
                ((TaskCreateActivity) getActivity()).setDateTimeComplete(true);
                btnNext.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.shape_rounded_back_button_active));
            } else {
                ((TaskCreateActivity) getActivity()).setDateTimeComplete(false);
                btnNext.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.shape_rounded_back_button_deactive));
            }
        });


    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void selectDateTimeBtn() {
        ColorStateList csl_primary = AppCompatResources.getColorStateList(getContext(), R.color.colorPrimary);
        imgDateTime.setImageTintList(csl_primary);
        imgDateTime.setImageDrawable(getResources().getDrawable(R.drawable.ic_date_time_big));
        txtDateTime.setTextColor(getResources().getColor(R.color.colorPrimary));
        Typeface face= ResourcesCompat.getFont(getActivity(), R.font.roboto_medium);
        txtDateTime.setTypeface(face);
        ColorStateList csl_grey = AppCompatResources.getColorStateList(getContext(), R.color.greyC4C4C4);
        ColorStateList csl_green = AppCompatResources.getColorStateList(getContext(), R.color.green);
        imgDetails.setImageTintList(csl_green);
        imgBudget.setImageTintList(csl_grey);
        txtDetails.setTextColor(getResources().getColor(R.color.green));
        txtBudget.setTextColor(getResources().getColor(R.color.colorGrayC9C9C9));

        tabClickListener();
    }

    public void checkTabs(){
        if (((TaskCreateActivity) getActivity()).isBudgetComplete()){
            ColorStateList csl_green = AppCompatResources.getColorStateList(getContext(), R.color.green);
            imgBudget.setImageTintList(csl_green);
            txtBudget.setTextColor(getResources().getColor(R.color.green));
        }
    }

    private void tabClickListener() {
        if (getActivity() != null && getValidationCode() == 0) {
            lytBtnDetails.setOnClickListener(v -> ((TaskCreateActivity) requireActivity()).onViewClicked(v));
            lytBntDateTime.setOnClickListener(v -> ((TaskCreateActivity) requireActivity()).onViewClicked(v));
            lytBtnBudget.setOnClickListener(v -> ((TaskCreateActivity) requireActivity()).onViewClicked(v));
        }
    }


    private void showSpinner() {
        edtTimeSpinner.setTextColor(ContextCompat.getColor(getActivity(), R.color.P300));
        spinnerArrow.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_up_arrow_g));
        isSpinnerOpen = true;
        spinnerItems.setVisibility(View.VISIBLE);
    }

    private void hideSpinner() {
        isSpinnerOpen = false;
        spinnerArrow.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_d_arrow_g));
        edtTimeSpinner.setTextColor(ContextCompat.getColor(getActivity(), R.color.N9001));
        spinnerItems.setVisibility(View.GONE);
    }
}
