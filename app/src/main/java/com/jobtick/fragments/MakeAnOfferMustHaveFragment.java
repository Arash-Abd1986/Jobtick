package com.jobtick.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.jobtick.R;
import android.annotation.SuppressLint;

import com.jobtick.activities.ActivityBase;
import com.jobtick.text_view.TextViewBold;
import com.jobtick.activities.MakeAnOfferActivity;
import com.jobtick.activities.TaskDetailsActivity;
import com.jobtick.adapers.MustHaveListAdapter;
import com.jobtick.models.MakeAnOfferModel;
import com.jobtick.models.MustHaveModel;
import com.jobtick.models.TaskModel;
import com.jobtick.models.UserAccountModel;
import com.jobtick.utils.ConstantKey;
import com.jobtick.utils.SessionManager;
import com.jobtick.utils.Tools;
import com.jobtick.widget.SpacingItemDecoration;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class MakeAnOfferMustHaveFragment extends Fragment implements View.OnClickListener {

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;

    private MakeAnOfferModel makeAnOfferModel;
    // private TaskModel taskModel;
    private MakeAnOfferActivity makeAnOfferActivity;
    MustHaveCallbackFunction budgetCallbackFunction;
    private UserAccountModel userAccountModel;
    private SessionManager sessionManager;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txtMustHave)
    TextView txtMustHave;

    private MustHaveListAdapter adapter;
    private ArrayList<MustHaveModel> addTagList;
    private TaskModel taskModel;


    public static MakeAnOfferMustHaveFragment newInstance(MakeAnOfferModel makeAnOfferModel, MustHaveCallbackFunction budgetCallbackFunction) {

        Bundle args = new Bundle();
        args.putParcelable(ConstantKey.MAKE_AN_OFFER_MODEL, makeAnOfferModel);
        // args.putParcelable(ConstantKey.TASK, taskModel);
        MakeAnOfferMustHaveFragment fragment = new MakeAnOfferMustHaveFragment();
        fragment.budgetCallbackFunction = budgetCallbackFunction;
        fragment.setArguments(args);
        return fragment;
    }

    public MakeAnOfferMustHaveFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_make_an_offer_must_have, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        makeAnOfferActivity = (MakeAnOfferActivity) requireActivity();
        if (makeAnOfferActivity != null) {
            sessionManager = new SessionManager(makeAnOfferActivity);
        }
        addTagList = new ArrayList<>();
        taskModel = TaskDetailsActivity.taskModel;
        makeAnOfferModel = new MakeAnOfferModel();
        userAccountModel = new UserAccountModel();
        userAccountModel = sessionManager.getUserAccount();
        if (getArguments() != null && getArguments().getParcelable(ConstantKey.MAKE_AN_OFFER_MODEL) != null) {
            makeAnOfferModel = getArguments().getParcelable(ConstantKey.MAKE_AN_OFFER_MODEL);
        }
        /*if (getArguments() != null && getArguments().getParcelable(ConstantKey.TASK) != null) {
            taskModel = getArguments().getParcelable(ConstantKey.TASK);
        }*/

        toolbar.setNavigationOnClickListener(MakeAnOfferMustHaveFragment.this);
        init();
    }


    public void init() {

        txtMustHave.setText(String.format("%s has a few must-haves for this task. Please check these before you make an offer.", taskModel.getPoster().getName()));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(getContext(), 5), true));
        recyclerView.setHasFixedSize(true);


        for (int i = 0; i < taskModel.getMusthave().size(); i++) {
            MustHaveModel mustHaveModel = new MustHaveModel();
            mustHaveModel.setMustHaveTitle(taskModel.getMusthave().get(i));
            mustHaveModel.setChecked(false);
            addTagList.add(mustHaveModel);
        }
        adapter = new MustHaveListAdapter(getContext(), addTagList);
        recyclerView.setAdapter(adapter);
        // adapter.setOnItemClickListener(this);
    }


    @OnClick({R.id.lyt_btn_continue})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.lyt_btn_continue:
                if (adapter.isAllSelected()) {
                    budgetCallbackFunction.continueMustHave(makeAnOfferModel);
                } else {
                    ((ActivityBase)requireActivity()).showToast("Please select all must-have requirement", requireContext());
                }
                break;
        }
    }


    @Override
    public void onClick(View v) {
        if (budgetCallbackFunction != null) {
            budgetCallbackFunction.backButtonMustHave();
        }
    }

    public interface MustHaveCallbackFunction {
        void backButtonMustHave();

        void continueMustHave(MakeAnOfferModel makeAnOfferModel);
    }
}
