package com.jobtick.android.fragments;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.jobtick.android.R;
import com.jobtick.android.activities.ActivityBase;
import com.jobtick.android.activities.TaskCreateActivity;
import com.jobtick.android.activities.TaskDetailsActivity;
import com.jobtick.android.models.TaskModel;
import com.jobtick.android.utils.Constant;
import com.jobtick.android.widget.ExtendedEntryText;
import com.jobtick.android.widget.ExtendedEntryTextNewDesign;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class TaskBudgetFragment extends Fragment implements TextWatcher {


    TaskCreateActivity taskCreateActivity;
    View view;

    OperationsListener operationsListener;
    TaskModel task;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_bnt_date_time)
    LinearLayout lytBntDateTime;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_btns)
    LinearLayout card_btn_post_task;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_btn_details)
    LinearLayout lytBtnDetails;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_btn_date_time)
    LinearLayout lytBtnDateTime;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_btn_budget)
    LinearLayout lytBtnBudget;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.card_budget)
    CardView cardBudget;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.card_date_time)
    CardView cardDatetime;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.rb_hourly)
    RadioButton rbHourly;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.rb_total)
    RadioButton rbTotal;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.rg_hourly_total)
    RadioGroup rgHourlyTotal;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_estimated_budget_h)
    TextView txtEstimatedBudget;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_us_h)
    TextView txtUs;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_btn_back)
    LinearLayout lytBtnBack;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.card_button)
    RelativeLayout cardButton;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_btn_post_task)
    LinearLayout lytBtnPostTask;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.card_post)
    RelativeLayout cardPost;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_button)
    LinearLayout lytButton;
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

    private LinearLayout estimatedH, estimatedT;
    private ExtendedEntryTextNewDesign edtBudgetT, edtBudgetH, edtHours;
    private TextView txtBudgetT, txtBudgetH;
    int hours = 0;
    int budgetH = 0;
    int budgetT = 0;

    public static TaskBudgetFragment newInstance(int budget, int hour_budget, int total_hours,
                                                 String payment_type, OperationsListener operationsListener) {

        Bundle args = new Bundle();
        args.putInt("BUDGET", budget);
        args.putInt("HOUR_BUDGET", hour_budget);
        args.putInt("TOTAL_HOURS", total_hours);
        args.putString("PAYMENT_TYPE", payment_type);
        TaskBudgetFragment fragment = new TaskBudgetFragment();
        fragment.operationsListener = operationsListener;
        fragment.setArguments(args);
        return fragment;
    }

    public TaskBudgetFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_task_budget, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        taskCreateActivity = (TaskCreateActivity) requireActivity();
        task = new TaskModel();
        edtBudgetT = view.findViewById(R.id.edt_budgetT);
        estimatedT = view.findViewById(R.id.card_estimated_t);
        txtBudgetT = view.findViewById(R.id.txt_budget_t);

        lytBtnBack.setOnClickListener(view1 -> {

            operationsListener.onBackClickBudget(budgetT, budgetH, hours, rbHourly.isChecked() ? "hourly " : "fixed");
            operationsListener.onValidDataFilledBudgetBack();
        });

        edtBudgetH = view.findViewById(R.id.edt_budgetH);
        edtHours = view.findViewById(R.id.edt_hours);
        estimatedH = view.findViewById(R.id.card_estimated_h);
        txtBudgetH = view.findViewById(R.id.txt_budget_h);

        radioBtnClick();
        edtText();
        selectBudgetBtn();

        task.setBudget(getArguments().getInt("BUDGET"));
        if (task.getBudget() != null && !task.getBudget().toString().equals("0"))
            edtBudgetT.setText(task.getBudget().toString());

        task.setHourlyRate(getArguments().getInt("HOUR_BUDGET"));
        if (task.getHourlyRate() != null && !task.getHourlyRate().toString().equals("0"))
            edtBudgetH.setText(task.getHourlyRate().toString());

        task.setTotalHours(getArguments().getInt("TOTAL_HOURS"));
        if (task.getTotalHours() != null && !task.getTotalHours().toString().equals("0"))
            edtHours.setText(task.getTotalHours().toString());

        task.setPaymentType(getArguments().getString("PAYMENT_TYPE"));
        if (task.getPaymentType() == null || task.getPaymentType().equalsIgnoreCase("fixed")) {
            rbTotal.setChecked(true);
            rbHourly.setChecked(false);
            edtHours.setVisibility(View.GONE);
            edtBudgetH.setVisibility(View.GONE);
            edtBudgetT.setVisibility(View.VISIBLE);

            estimatedH.setVisibility(View.GONE);
            estimatedT.setVisibility(View.VISIBLE);
        } else {
            rbHourly.setChecked(true);
            rbTotal.setChecked(false);
            //   rbHourly.setTextColor(taskCreateActivity.getResources().getColor(R.color.black));
            //    rbTotal.setTextColor(taskCreateActivity.getResources().getColor(R.color.white));
            edtHours.setVisibility(View.VISIBLE);
            edtBudgetH.setVisibility(View.VISIBLE);
            edtBudgetT.setVisibility(View.GONE);
            estimatedH.setVisibility(View.VISIBLE);
            estimatedT.setVisibility(View.GONE);
        }
        showEstimatedBudget();

        taskCreateActivity.setActionDraftTaskBudget(taskModel -> {
            if (rbTotal.isChecked()) {
                taskModel.setTaskType("fixed");
                taskModel.setBudget(Integer.parseInt(edtBudgetT.getText()));
            } else {
                taskModel.setTaskType("hourly");
                taskModel.setTotalHours(Integer.parseInt(edtHours.getText()));
                taskModel.setHourlyRate(Integer.parseInt(edtBudgetH.getText()));
            }
            operationsListener.draftTaskBudget(taskModel);
        });

        cardPost.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.shape_rounded_back_button_active));

        txtBudgetT.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (getValidationCode(false)) {
                    ((TaskCreateActivity) getActivity()).setBudgetComplete(true);
                    cardPost.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.shape_rounded_back_button_active));
                } else {
                    ((TaskCreateActivity) getActivity()).setBudgetComplete(false);
                    cardPost.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.shape_rounded_back_button_deactive));
                }
            }
        });

        txtBudgetH.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (getValidationCode(false)) {
                    ((TaskCreateActivity) getActivity()).setBudgetComplete(true);
                    cardPost.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.shape_rounded_back_button_active));
                } else {
                    ((TaskCreateActivity) getActivity()).setBudgetComplete(false);
                    cardPost.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.shape_rounded_back_button_deactive));
                }
            }
        });
    }

    private void edtText() {
        edtBudgetH.addTextChangedListener(this);
        edtHours.addTextChangedListener(this);
        edtBudgetT.addTextChangedListener(this);
    }

    private void radioBtnClick() {
        rgHourlyTotal.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton rb_btn = (RadioButton) view.findViewById(checkedId);
            if (rb_btn.getText().equals("Hourly")) {
                edtHours.setVisibility(View.VISIBLE);
                edtBudgetT.setVisibility(View.GONE);
                edtBudgetH.setVisibility(View.VISIBLE);
                estimatedH.setVisibility(View.VISIBLE);
                estimatedT.setVisibility(View.GONE);
                showEstimatedBudget();
            } else {
                edtHours.setVisibility(View.GONE);
                edtBudgetT.setVisibility(View.VISIBLE);
                edtBudgetH.setVisibility(View.GONE);
                estimatedH.setVisibility(View.GONE);
                estimatedT.setVisibility(View.VISIBLE);
            }
        });
    }

    private void showEstimatedBudget() {

        if (rbHourly.isChecked()) {

            if (edtHours.getText().toString().trim().length() != 0)
                hours = Integer.parseInt(edtHours.getText().toString().trim());
            else hours = 0;
            if (edtBudgetH.getText().toString().trim().length() != 0)
                budgetH = Integer.parseInt(edtBudgetH.getText().toString().trim());
            else budgetH = 0;

            txtBudgetH.setText(String.valueOf(hours * budgetH));
        } else {
            if (edtBudgetT.getText().toString().trim().length() != 0)
                budgetT = Integer.parseInt(edtBudgetT.getText().toString().trim());
            else budgetT = 0;

            txtBudgetT.setText(String.valueOf(budgetT));
        }
    }

    @OnClick({R.id.lyt_btn_details, R.id.lyt_btn_date_time, R.id.lyt_btn_budget,
            R.id.lyt_btn_back, R.id.lyt_btn_post_task})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.lyt_btn_details:

                break;
            case R.id.lyt_btn_date_time:

                break;
            case R.id.lyt_btn_budget:
                break;
            case R.id.lyt_btn_back:
                //  if (edtBudgetH.getText().toString().trim().length() > 0 || edtBudgetT.getText().toString().trim().length() > 0) {
                operationsListener.onBackClickBudget(
                        Integer.parseInt(edtBudgetT.getText().toString().trim()),
                        Integer.parseInt(edtBudgetH.getText().toString().trim()),
                        Integer.parseInt(edtHours.getText().toString().trim()),
                        Constant.TASK_PAYMENT_TYPE_HOURLY
                );
                operationsListener.onValidDataFilledBudgetBack();
                //  } else {
                //     edtBudgetH.setError("Please enter your budget");
                //      edtBudgetT.setError("Please enter your budget");
                //  }
                break;
            case R.id.lyt_btn_post_task:
                if (!getValidationCode(true)) return;

                operationsListener.onNextClickBudget(budgetT, budgetH, hours, rbHourly.isChecked() ? "hourly " : "fixed");
                operationsListener.onValidDataFilledBudgetNext();
                break;
        }
    }


    private boolean getValidationCode(boolean showToast) {
        if (rbTotal.isChecked()) {
            return validation(txtBudgetT, showToast);
        } else {
            return validation(txtBudgetH, showToast);
        }
    }

    private boolean validation(TextView edtBudget, boolean showToast) {
        if (edtBudget.getText().length() == 0) {
            if (showToast)
                ((ActivityBase) requireActivity()).showToast("Please enter your estimate budget.", requireContext());
            return false;
        }
        if (Integer.parseInt(edtBudget.getText().toString()) < 5) {
            if (showToast)
                ((ActivityBase) requireActivity()).showToast("Your estimate budget can't lower than 5 dollars.", requireContext());
            return false;
        }
        if (Integer.parseInt(edtBudget.getText().toString()) > 9999) {
            if (showToast)
                ((ActivityBase) requireActivity()).showToast("Your estimate budget can't more than 9999 dollars.", requireContext());
            return false;
        }
        return true;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        showEstimatedBudget();
    }

    public interface OperationsListener {
        void onNextClickBudget(int budget, int hour_budget, int total_hours, String payment_type);

        void onBackClickBudget(int budget, int hour_budget, int total_hours, String payment_type);

        void onValidDataFilledBudgetNext();

        void onValidDataFilledBudgetBack();

        void draftTaskBudget(TaskModel taskModel);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void selectBudgetBtn() {
        cardBudget.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
        ColorStateList csl_primary = AppCompatResources.getColorStateList(getContext(), R.color.colorPrimary);
        imgBudget.setImageTintList(csl_primary);
        imgBudget.setImageDrawable(getResources().getDrawable(R.drawable.ic_budget_big));
        txtBudget.setTextColor(getResources().getColor(R.color.colorPrimary));
        Typeface face = ResourcesCompat.getFont(getActivity(), R.font.roboto_medium);
        txtBudget.setTypeface(face);
        ColorStateList csl_green = AppCompatResources.getColorStateList(getContext(), R.color.green);
        imgDateTime.setImageTintList(csl_green);
        imgDetails.setImageTintList(csl_green);
        txtDateTime.setTextColor(getResources().getColor(R.color.green));
        txtDetails.setTextColor(getResources().getColor(R.color.green));
        tabClickListener();
        cardDatetime.setOnClickListener(v -> {
            operationsListener.onBackClickBudget(budgetT, budgetH, hours, rbHourly.isChecked() ? "hourly " : "fixed");
            operationsListener.onValidDataFilledBudgetBack();
        });
    }

    private void tabClickListener() {
        if (getActivity() != null && getValidationCode(false)) {
            lytBtnDetails.setOnClickListener(v -> ((TaskCreateActivity) requireActivity()).onViewClicked(v));
            lytBntDateTime.setOnClickListener(v -> ((TaskCreateActivity) requireActivity()).onViewClicked(v));
            lytBtnBudget.setOnClickListener(v -> ((TaskCreateActivity) requireActivity()).onViewClicked(v));
        }
    }
}
