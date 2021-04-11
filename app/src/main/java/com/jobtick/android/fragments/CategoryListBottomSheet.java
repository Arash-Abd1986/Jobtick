package com.jobtick.android.fragments;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.jobtick.android.R;
import com.jobtick.android.activities.CategoryListActivity;
import com.jobtick.android.activities.TaskCreateActivity;
import com.jobtick.android.adapers.TaskCategoryAdapter;
import com.jobtick.android.models.TaskCategory;
import com.jobtick.android.models.TaskModel;
import com.jobtick.android.pagination.PaginationListener;
import com.jobtick.android.utils.Constant;
import com.jobtick.android.utils.ConstantKey;
import com.jobtick.android.utils.SessionManager;
import com.tapadoo.alerter.Alerter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

import static com.jobtick.android.pagination.PaginationListener.PAGE_START;

public class CategoryListBottomSheet extends BottomSheetDialogFragment implements TaskCategoryAdapter.OnItemClickListener {
    RecyclerView recyclerViewCategories;
    private TaskCategoryAdapter adapter;
    private SessionManager sessionManager;
    private FrameLayout close;

    private String query = "";

    public CategoryListBottomSheet(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_category_list, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerViewCategories = view.findViewById(R.id.recyclerView_categories);
        close = view.findViewById(R.id.close);
        close.setOnClickListener(v ->
                this.dismiss());
        setCategoryData();

    }

    private void setCategoryData() {
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
        recyclerViewCategories.setLayoutManager(layoutManager);
        recyclerViewCategories.setHasFixedSize(true);
        adapter = new TaskCategoryAdapter(getActivity(), new ArrayList<>());
        recyclerViewCategories.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
        getTaskCategoryData();
    }

    @Override
    public void onItemClick(View view, TaskCategory obj, int position) {
        Intent creating_task = new Intent(getActivity(), TaskCreateActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(ConstantKey.CATEGORY_ID, obj.getId());
        creating_task.putExtras(bundle);
        getActivity().startActivityForResult(creating_task, ConstantKey.RESULTCODE_CATEGORY);
    }

    public List<TaskCategory> getTaskCategoryData() {
        List<TaskCategory> items = new ArrayList<>();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.BASE_URL + Constant.TASK_CATEGORY_V2 + "?query=" + query,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.has("data") && !jsonObject.isNull("data")) {
                            JSONArray jsonArray_data = jsonObject.getJSONArray("data");
                            if (jsonArray_data.length() > 0)
                                for (int i = 0; jsonArray_data.length() > i; i++) {
                                    JSONObject jsonObject_taskModel_list = jsonArray_data.getJSONObject(i);
                                    TaskCategory taskModel = new TaskCategory().getJsonToModel(jsonObject_taskModel_list, getActivity());
                                    items.add(taskModel);
                                }
                            else {
                                showToast("some went to wrong", getActivity());
                                this.dismiss();
                            }
                        } else {
                            showToast("some went to wrong", getActivity());
                            this.dismiss();
                            return;
                        }

                        if (jsonObject.has("meta") && !jsonObject.isNull("meta")) {
                            JSONObject jsonObject_meta = jsonObject.getJSONObject("meta");
                            Constant.PAGE_SIZE = jsonObject_meta.getInt("per_page");
                        }

                        if (items.size() <= 0) {
                            recyclerViewCategories.setVisibility(View.GONE);
                        } else {
                            recyclerViewCategories.setVisibility(View.VISIBLE);
                        }
                        adapter.addItems(items);
                    } catch (JSONException e) {
                        // hideProgressDialog();
                        this.dismiss();
                        e.printStackTrace();
                    }
                },
                error -> Log.d("error", error.networkResponse.toString()) /*errorHandle1(error.networkResponse)*/) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                map1.put("Authorization", "Bearer " + sessionManager.getAccessToken());
                return map1;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
        return items;
    }


    public void showToast(String content, Context context) {
        Alerter.create(getActivity())
                .setTitle("")
                .setText(content)
                .setBackgroundResource(R.color.colorRedError)
                .show();
    }

}