package com.jobtick.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.jobtick.BuildConfig;
import com.jobtick.EditText.EditTextMedium;
import com.jobtick.EditText.EditTextRegular;
import com.jobtick.EditText.EditTextSemiBold;
import com.jobtick.R;
import com.jobtick.TextView.TextViewMedium;
import com.jobtick.TextView.TextViewRegular;
import com.jobtick.activities.AddTagActivity;

import com.jobtick.activities.TaskCreateActivity;
import com.jobtick.adapers.AddTagAdapter;
import com.jobtick.adapers.AttachmentAdapter;
import com.jobtick.models.AttachmentModel;
import com.jobtick.models.GeocodeObject;
import com.jobtick.models.PositionModel;
import com.jobtick.models.TaskModel;
import com.jobtick.retrofit.ApiClient;
import com.jobtick.utils.ConstantKey;
import com.jobtick.utils.Helper;
import com.jobtick.utils.ImageUtil;
import com.jobtick.utils.KeyboardUtil;
import com.jobtick.utils.SessionManager;
import com.jobtick.utils.Tools;
import com.jobtick.widget.SpacingItemDecoration;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.mapbox.mapboxsdk.plugins.places.picker.PlacePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

/**
 * A simple {@link Fragment} subclass.
 */
public class TaskDetailFragment extends Fragment implements AddTagAdapter.OnItemClickListener, AttachmentAdapter.OnItemClickListener {
    @BindView(R.id.lyt_btn_details)
    LinearLayout lytBtnDetails;
    @BindView(R.id.lyt_bnt_date_time)
    LinearLayout lytBntDateTime;
    @BindView(R.id.lyt_btn_budget)
    LinearLayout lytBtnBudget;
    @BindView(R.id.edt_title_counter)
    EditTextSemiBold edtTitleCounter;
    @BindView(R.id.edt_title)
    EditTextRegular edtTitle;
    @BindView(R.id.edt_description_counter)
    EditTextSemiBold edtDescriptionCounter;
    @BindView(R.id.edt_description)
    EditTextRegular edtDescription;
    @BindView(R.id.recycler_add_must_have)
    RecyclerView recyclerAddMustHave;
    @BindView(R.id.img_add_white)
    ImageView imgAddWhite;
    @BindView(R.id.rlt_add_must_have)
    RelativeLayout rltAddMustHave;
    @BindView(R.id.checkbox_online)
    CheckBox checkboxOnline;
    @BindView(R.id.txt_suburb)
    TextViewRegular txtSuburb;
    @BindView(R.id.img_location_pin)
    ImageView imgLocationPin;
    @BindView(R.id.card_location)
    LinearLayout cardLocation;
    @BindView(R.id.lyt_btn_back)
    LinearLayout lytBtnBack;
    @BindView(R.id.card_button)
    CardView cardButton;
    @BindView(R.id.lyt_btn_next)
    LinearLayout lytBtnNext;
    @BindView(R.id.lyt_button)
    LinearLayout lytButton;
    @BindView(R.id.bottom_sheet)
    FrameLayout bottomSheet;


    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    private String TAG = TaskDetailFragment.class.getName();
    private int PLACE_SELECTION_REQUEST_CODE = 21;


    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;


    public static final String VIDEO_FORMAT = ".mp4";
    public static final String VIDEO_SIGN = "VID_";
    private static final int VIDEO_CAPTURE = 101;
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    public static final long MAX_VIDEO_DURATION = 30;
    public static final long MAX_VIDEO_SIZE = 20 * 1024 * 1024;
    public static final String MEDIA_DIRECTORY_NAME = "Jobtick";
    public static final String VIDEO_NAME = "video_name";
    public static final String AD_THUMB = "thumb_name";
    public static final String TIME_STAMP_FORMAT = "yyyyMMdd_HHmmss";
    public static final int MEDIA_TYPE_VIDEO = 2;
    private static final int PICK_VIDEO = 107;

    private Uri fileUri;
    private AttachmentAdapter attachmentAdapter;
  //  private BottomSheetBehavior mBehavior;
    private BottomSheetDialog mBottomSheetDialog;
    private TaskCreateActivity taskCreateActivity;
    private AddTagAdapter tagAdapter;
    private ArrayList<String> addTagList;
    private GeocodeObject geoCodeObject;
    private LatLng locationObject;
    private OperationsListener operationsListener;
    private TaskModel task;
    private SessionManager sessionManager;

    private String imagePath;
    private Uri filePath;
    private Bitmap bitmap;

    private ArrayList<AttachmentModel> attachmentArrayList;

    public static TaskDetailFragment newInstance(String title, String description, ArrayList<String> musthave, String task_type, String location, PositionModel positionModel, OperationsListener operationsListener) {
        TaskDetailFragment fragment = new TaskDetailFragment();
        fragment.operationsListener = operationsListener;
        Bundle args = new Bundle();
        args.putString("TITLE", title);
        args.putString("DESCRIPTION", description);
        args.putStringArrayList("MUSTHAVE", musthave);
        args.putString("TASK_TYPE", task_type);
        args.putString("LOCATION", location);
        args.putParcelable("POSITION", positionModel);

        fragment.setArguments(args);
        return fragment;
    }

    public TaskDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_task_detail, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        taskCreateActivity = (TaskCreateActivity) getActivity();
        sessionManager = new SessionManager(taskCreateActivity);

        task = new TaskModel();
        attachmentArrayList = new ArrayList<>();
        attachmentArrayList.add(new AttachmentModel());
       // mBehavior = BottomSheetBehavior.from(bottomSheet);
        addTagList = new ArrayList<>();
        task.setTitle(getArguments().getString("TITLE"));
        task.setDescription(getArguments().getString("DESCRIPTION"));
        task.setMusthave(getArguments().getStringArrayList("MUSTHAVE"));
        task.setTaskType(getArguments().getString("TASK_TYPE"));
        task.setLocation(getArguments().getString("LOCATION"));
        task.setPosition(getArguments().getParcelable("POSITION"));

        taskCreateActivity.setActionDraftTaskDetails(taskModel -> {
            if (taskModel.getTitle() != null && taskModel.getDescription() != null) {
                operationsListener.draftTaskDetails(taskModel, true);
            } else {
                if (!TextUtils.isEmpty(edtTitle.getText().toString().trim()) || edtTitle.getText().toString().trim().length() >= 10) {
                    taskModel.setTitle(edtTitle.getText().toString().trim());
                } else if (!TextUtils.isEmpty(edtDescription.getText().toString().trim()) || edtDescription.getText().toString().trim().length() >= 25) {
                    taskModel.setDescription(edtDescription.getText().toString().trim());
                } else if (!TextUtils.isEmpty(txtSuburb.getText().toString().trim())) {
                    taskModel.setLocation(txtSuburb.getText().toString().trim());
                    taskModel.setPosition(task.getPosition());
                }
                if (addTagList != null && addTagList.size() != 0) {
                    taskModel.setMusthave(addTagList);
                }
                taskModel.setTaskType(checkboxOnline.isChecked() ? "remote" : "physical");
                operationsListener.draftTaskDetails(taskModel, false);
            }
        });


        setComponent();
        txtSuburb.setOnClickListener(v -> {
            Intent intent = new PlaceAutocomplete.IntentBuilder()
                    .accessToken(Mapbox.getAccessToken())
                    .placeOptions(PlaceOptions.builder()
                            // .backgroundColor(Helper.getAttrColor(taskCreateActivity, R.attr.colorBackground))
                            .backgroundColor(taskCreateActivity.getResources().getColor(R.color.background))
                            .limit(10)
                            .country("AU")

                            /*.addInjectedFeature(home)
                            .addInjectedFeature(work)*/
                            .build(PlaceOptions.MODE_FULLSCREEN))
                    .build(getActivity());
            startActivityForResult(intent, PLACE_SELECTION_REQUEST_CODE);
        });
        init();

        if (!edtTitle.getText().toString().equalsIgnoreCase("")) {
            int length = edtTitle.getText().toString().trim().length();
            if (length <= 9) {
                edtTitleCounter.setText(edtTitle.getText().toString().trim().length() + "/10+");
                edtTitleCounter.setTextColor(taskCreateActivity.getResources().getColor(R.color.red_600));
            } else {
                edtTitleCounter.setText(edtTitle.getText().toString().trim().length() + "/100");
                edtTitleCounter.setTextColor(taskCreateActivity.getResources().getColor(R.color.green));
            }
        } else {
            edtTitleCounter.setText("0/10+");
            edtTitleCounter.setTextColor(taskCreateActivity.getResources().getColor(R.color.red_600));
        }

        edtTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equalsIgnoreCase("")) {
                    int length = s.length();
                    if (length <= 9) {
                        edtTitleCounter.setText(s.length() + "/10+");
                        edtTitleCounter.setTextColor(taskCreateActivity.getResources().getColor(R.color.red_600));
                    } else {
                        edtTitleCounter.setText(s.length() + "/100");
                        edtTitleCounter.setTextColor(taskCreateActivity.getResources().getColor(R.color.green));
                    }
                } else {
                    edtTitleCounter.setText("0/10+");
                    edtTitleCounter.setTextColor(taskCreateActivity.getResources().getColor(R.color.red_600));
                }
            }
        });


        if (!edtDescription.getText().toString().equalsIgnoreCase("")) {
            int length = edtTitle.getText().toString().trim().length();
            if (length <= 24) {
                edtDescriptionCounter.setText(edtTitle.getText().toString().trim().length() + "/25+");
                edtDescriptionCounter.setTextColor(taskCreateActivity.getResources().getColor(R.color.red_600));
            } else {
                edtDescriptionCounter.setText(edtTitle.getText().toString().trim().length() + "/1000");
                edtDescriptionCounter.setTextColor(taskCreateActivity.getResources().getColor(R.color.green));
            }
        } else {
            edtDescriptionCounter.setText("0/25+");
            edtDescriptionCounter.setTextColor(taskCreateActivity.getResources().getColor(R.color.red_600));
        }


        edtDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equalsIgnoreCase("")) {
                    int length = s.length();
                    if (length <= 24) {
                        edtDescriptionCounter.setText(s.length() + "/25+");
                        edtDescriptionCounter.setTextColor(taskCreateActivity.getResources().getColor(R.color.red_600));
                    } else {
                        edtDescriptionCounter.setText(s.length() + "/1000");
                        edtDescriptionCounter.setTextColor(taskCreateActivity.getResources().getColor(R.color.green));
                    }
                } else {
                    edtDescriptionCounter.setText("0/25+");
                    edtDescriptionCounter.setTextColor(taskCreateActivity.getResources().getColor(R.color.red_600));
                }
            }
        });
    }

    private void setComponent() {
        edtTitle.setText(task.getTitle());
        edtDescription.setText(task.getDescription());
        if (task.getTaskType() != null) {
            if (task.getTaskType().equalsIgnoreCase("remote")) {
                checkboxOnline.setChecked(true);
                cardLocation.setVisibility(View.GONE);
            } else {
                checkboxOnline.setChecked(false);
                cardLocation.setVisibility(View.VISIBLE);
            }
        } else {
            checkboxOnline.setChecked(false);
            cardLocation.setVisibility(View.VISIBLE);
        }
        if (task.getMusthave() != null && task.getMusthave().size() != 0) {
            addTagList.addAll(task.getMusthave());
        }
        txtSuburb.setText(task.getLocation());
    }

    private void init() {
        recyclerAddMustHave.setLayoutManager(new GridLayoutManager(taskCreateActivity, 1));
        recyclerAddMustHave.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(taskCreateActivity, 5), true));
        recyclerAddMustHave.setHasFixedSize(true);


        //set data and list adapter
        tagAdapter = new AddTagAdapter(taskCreateActivity, addTagList);
        recyclerAddMustHave.setAdapter(tagAdapter);
        tagAdapter.setOnItemClickListener(this);


        recyclerView.setLayoutManager(new GridLayoutManager(taskCreateActivity, 4));
        recyclerView.addItemDecoration(new SpacingItemDecoration(4, Tools.dpToPx(taskCreateActivity, 5), true));
        recyclerView.setHasFixedSize(true);
        //set data and list adapter
        attachmentAdapter = new AttachmentAdapter(taskCreateActivity, attachmentArrayList, true);
        recyclerView.setAdapter(attachmentAdapter);
        attachmentAdapter.setOnItemClickListener(this);


    }


    @OnClick({R.id.rlt_add_must_have, R.id.lyt_btn_details, R.id.lyt_bnt_date_time, R.id.lyt_btn_budget, R.id.checkbox_online, R.id.lyt_btn_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rlt_add_must_have:
                /*Intent intent = new Intent(taskCreateActivity, AddTagActivity.class);
                Bundle bundle = new Bundle();
                bundle.putStringArrayList(ConstantKey.TAG, addTagList);
                bundle.putString(ConstantKey.ACTIONBAR_TITLE, "Must-haves");
                bundle.putString(ConstantKey.TITLE, "Add up to 3 things the Tasker needs to have or do to make an offer -e.g.\n• Must be available on Saturday morning\n• Must have own van or truck");
                bundle.putInt(ConstantKey.TAG_SIZE, 3);
                intent.putExtras(bundle);
                startActivityForResult(intent, 25);*/

                showBottomSheetAddMustHave();
                //  showBottomSheetDialog();
                break;
            case R.id.lyt_btn_details:
                break;
            case R.id.lyt_bnt_date_time:
                break;
            case R.id.lyt_btn_budget:
                break;
            case R.id.checkbox_online:
                if (checkboxOnline.isChecked()) {
                    cardLocation.setVisibility(View.GONE);
                } else {
                    cardLocation.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.lyt_btn_next:
                switch (getValidationCode()) {
                    case 0:
                        //success
                        operationsListener.onNextClick(
                                edtTitle.getText().toString().trim(),
                                edtDescription.getText().toString().trim(),
                                addTagList,
                                checkboxOnline.isChecked() ? "remote" : "physical",
                                txtSuburb.getText().toString().trim(),
                                task.getPosition()
                        );
                        operationsListener.onValidDataFilled();
                        break;
                    case 1:
                        edtTitle.setError("Please enter the title");
                        break;
                    case 2:
                        edtDescription.setError("Please enter the description");
                        break;
                    case 3:
                        txtSuburb.setError("Please select your location");
                        break;
                }

                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 25) {
            addTagList.clear();
            addTagList.addAll(data.getStringArrayListExtra("TAG"));
            if (addTagList.size() != 0) {
                if (recyclerAddMustHave.getVisibility() != View.VISIBLE) {
                    recyclerAddMustHave.setVisibility(View.VISIBLE);
                }
            } else {
                if (recyclerAddMustHave.getVisibility() == View.VISIBLE) {
                    recyclerAddMustHave.setVisibility(View.GONE);
                }
            }
            tagAdapter.notifyDataSetChanged();
        }

        if (requestCode == PLACE_SELECTION_REQUEST_CODE && resultCode == getActivity().RESULT_OK) {

            // Retrieve the information from the selected location's CarmenFeature

            CarmenFeature carmenFeature = PlacePicker.getPlace(data);
            Helper.Logger(TAG, "CarmenFeature = " + carmenFeature.toJson());
            GeocodeObject geocodeObject = Helper.getGeoCodeObject(getActivity(), carmenFeature.center().latitude()
                    , carmenFeature.center().longitude());
            txtSuburb.setText(carmenFeature.placeName());


            //txtSuburb.setText(geocodeObject.getAddress());
            // editArea.setText(geocodeObject.getKnownName());
            PositionModel positionModel = new PositionModel();
            positionModel.setLatitude(carmenFeature.center().latitude());
            positionModel.setLongitude(carmenFeature.center().longitude());
            task.setPosition(positionModel);
            locationObject = new LatLng(carmenFeature.center().latitude(), carmenFeature.center().longitude());
        } else if (requestCode == 1 && resultCode == getActivity().RESULT_OK) {
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(taskCreateActivity.getContentResolver(), filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            imagePath = getPath(data.getData());
            //imageView.setImageBitmap(BitmapFactory.decodeFile(imagePath));
            //  uploadImageWithAmount();
            File file = new File(imagePath);
            uploadDataInTempApi(file);


        } else if (requestCode == CAMERA_REQUEST && resultCode == getActivity().RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            //bitmap to convert into file
            File pictureFile = new File(taskCreateActivity.getExternalCacheDir(), "jobtick");
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(pictureFile);
                if (bitmap != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                }
                fos.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            uploadDataInTempApi(pictureFile);


        }
    }

    @Override
    public void onItemClick(View view, String obj, int position, String action) {
        addTagList.remove(position);
        tagAdapter.notifyItemRemoved(position);
        tagAdapter.notifyItemRangeRemoved(position, addTagList.size());
        recyclerAddMustHave.swapAdapter(tagAdapter, true);
        if (addTagList.size() == 0) {
            recyclerAddMustHave.setVisibility(View.GONE);
        }
    }


    private int getValidationCode() {
        if (TextUtils.isEmpty(edtTitle.getText().toString().trim()) || edtTitle.getText().toString().trim().length() < 10) {
            return 1;
        } else if (TextUtils.isEmpty(edtDescription.getText().toString().trim()) || edtDescription.getText().toString().trim().length() < 25) {
            return 2;
        } else if (!checkboxOnline.isChecked()) {
            if (TextUtils.isEmpty(txtSuburb.getText().toString().trim())) {
                return 3;
            }
        }
        return 0;
    }

    @Override
    public void onItemClick(View view, AttachmentModel obj, int position, String action) {
        if (action.equalsIgnoreCase("add")) {
            showBottomSheetDialog();
        } else if (action.equalsIgnoreCase("delete")) {
            recyclerView.removeViewAt(position);
            attachmentArrayList.remove(position);
            attachmentAdapter.notifyItemRemoved(position);
            attachmentAdapter.notifyItemRangeRemoved(position, attachmentArrayList.size());
            taskCreateActivity.showToast("Deleted that attachment", taskCreateActivity);
        } else if (action.equalsIgnoreCase("show")) {
            showBottomSheetDialogViewFullImage(obj.getModalUrl(), position);
        }
    }


    public interface OperationsListener {
        void onNextClick(String title, String description, ArrayList<String> musthave, String task_type, String location, PositionModel positionModel);

        void onValidDataFilled();

        void draftTaskDetails(TaskModel taskModel, boolean moveForword);
    }


    private void showBottomSheetDialogViewFullImage(String url, int currentPosition) {
        /*if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
*/
        final View view = getLayoutInflater().inflate(R.layout.sheet_full_image, null);

        mBottomSheetDialog = new BottomSheetDialog(taskCreateActivity);
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        ImageView iv_attachment = view.findViewById(R.id.iv_attachment);
        if (url != null) {
            ImageUtil.displayImage(iv_attachment, url, null);
        }


        LinearLayout lyt_btn_delete = view.findViewById(R.id.lyt_btn_delete);
        lyt_btn_delete.setOnClickListener(v -> {
            recyclerView.removeViewAt(currentPosition);
            attachmentArrayList.remove(currentPosition);
            attachmentAdapter.notifyItemRemoved(currentPosition);
            attachmentAdapter.notifyItemRangeRemoved(currentPosition, attachmentArrayList.size());
            taskCreateActivity.showToast("Deleted that attachment", taskCreateActivity);
            mBottomSheetDialog.dismiss();

        });


        LinearLayout lyt_btn_back = view.findViewById(R.id.lyt_btn_back);
        lyt_btn_back.setOnClickListener(v -> {
            mBottomSheetDialog.dismiss();
        });

        // set background transparent
        ((View) view.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));

        mBottomSheetDialog.show();
        mBottomSheetDialog.setOnDismissListener(dialog -> mBottomSheetDialog = null);

    }


    private void showBottomSheetAddMustHave() {
      /*  if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }*/

        final View view = getLayoutInflater().inflate(R.layout.sheet_add_must_have, null);

        //  new KeyboardUtil(getActivity(), view);

        mBottomSheetDialog = new BottomSheetDialog(taskCreateActivity);
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

/*
        mBottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        mBottomSheetDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
*/


        TextViewMedium txtCount = view.findViewById(R.id.txt_count);
        TextViewRegular txtTotalCount = view.findViewById(R.id.txt_total_count);

        LinearLayout lytBtnNext = view.findViewById(R.id.lyt_btn_next);
        EditTextRegular edtAddTag = view.findViewById(R.id.edtAddTag);

        txtCount.setText(addTagList.size() + "");
        lytBtnNext.setOnClickListener(v -> {
            if (TextUtils.isEmpty(edtAddTag.getText().toString().trim())) {
                edtAddTag.setError("Text is empty");
                return;
            }


            if (3 > addTagList.size()) {

                txtCount.setText(addTagList.size() + 1 + "");

                if (recyclerView.getVisibility() != View.VISIBLE) {
                    recyclerView.setVisibility(View.VISIBLE);
                }
                addTagList.add(edtAddTag.getText().toString().trim());
                tagAdapter.notifyItemInserted(tagAdapter.getItemCount());
                edtAddTag.setText(null);
                if (addTagList.size() != 0) {
                    if (recyclerAddMustHave.getVisibility() != View.VISIBLE) {
                        recyclerAddMustHave.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (recyclerAddMustHave.getVisibility() == View.VISIBLE) {
                        recyclerAddMustHave.setVisibility(View.GONE);
                    }
                }
            } else {
                taskCreateActivity.showToast("Max. 3 Tag you can add", taskCreateActivity);
            }


        });


        // set background transparent
        ((View) view.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));

        mBottomSheetDialog.show();
        mBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mBottomSheetDialog = null;
            }
        });

       /* AddTagSheetFragment addPhotoBottomDialogFragment =
                AddTagSheetFragment.newInstance();
        addPhotoBottomDialogFragment.show(taskCreateActivity.getSupportFragmentManager(),
                "add_photo_dialog_fragment");
*/

    }


    private void showBottomSheetDialog() {
     /*   if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }*/

        final View view = getLayoutInflater().inflate(R.layout.sheet_attachment, null);
        LinearLayout lytBtnCamera = view.findViewById(R.id.lyt_btn_camera);
        LinearLayout lytBtnImage = view.findViewById(R.id.lyt_btn_image);
        LinearLayout lytBtnVideo = view.findViewById(R.id.lyt_btn_video);
        LinearLayout lytBtnDoc = view.findViewById(R.id.lyt_btn_doc);
        LinearLayout lyrBtnVideoCamera = view.findViewById(R.id.lyt_btn_video_camera);

        lytBtnCamera.setOnClickListener(view1 -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (getContext().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                } else {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            }
            mBottomSheetDialog.hide();
        });
        lytBtnVideo.setOnClickListener(view12 -> {
            getGalleryVideo();
            mBottomSheetDialog.hide();
        });
        lyrBtnVideoCamera.setOnClickListener(view13 -> {
            recordVideo();
            mBottomSheetDialog.hide();
        });
        lytBtnDoc.setOnClickListener(view14 -> {
            Intent intent = new Intent();
            intent.setType("application/pdf");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select PDF File"), 1001);
            mBottomSheetDialog.hide();
        });

        lytBtnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermissionREAD_EXTERNAL_STORAGE(taskCreateActivity)) {
                    Intent opengallary = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(Intent.createChooser(opengallary, "Open Gallary"), 1);
                }
                mBottomSheetDialog.hide();
            }
        });

        mBottomSheetDialog = new BottomSheetDialog(taskCreateActivity);
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);


        // set background transparent
        ((View) view.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));

        mBottomSheetDialog.show();
        mBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mBottomSheetDialog = null;
            }
        });

    }


    private void getGalleryVideo() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        intent.setType("video/*");
        startActivityForResult(intent, PICK_VIDEO);

    }


    private void recordVideo() {

        try {
            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
            intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, MAX_VIDEO_DURATION);
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
            intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, MAX_VIDEO_SIZE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file
            // start the video capture Intent
            startActivityForResult(intent, VIDEO_CAPTURE);

        } catch (Exception e) {

            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

            fileUri = getOutputMediaFileUri1(MEDIA_TYPE_VIDEO);

            intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, MAX_VIDEO_DURATION);
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
            intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, MAX_VIDEO_SIZE);

            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file

            // start the video capture Intent
            startActivityForResult(intent, VIDEO_CAPTURE);
        }
    }


    public boolean checkPermissionREAD_EXTERNAL_STORAGE(
            final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context,
                        READ_EXTERNAL_STORAGE)) {
                    showDialog("External storage", context,
                            READ_EXTERNAL_STORAGE);
                } else {
                    ActivityCompat
                            .requestPermissions(
                                    (Activity) context,
                                    new String[]{READ_EXTERNAL_STORAGE},
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public void showDialog(final String msg, final Context context,
                           final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                (dialog, which) -> ActivityCompat.requestPermissions((Activity) context,
                        new String[]{permission},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE));
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    private Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private Uri getOutputMediaFileUri1(int type) {
        return FileProvider.getUriForFile(taskCreateActivity, BuildConfig.APPLICATION_ID + ".provider", getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type) {

        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                MEDIA_DIRECTORY_NAME);

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }


        File mediaFile;
        if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + VIDEO_SIGN + getTimeStamp() + VIDEO_FORMAT);
        } else {
            return null;
        }


        Log.e("MEDIAFILE", String.valueOf(mediaFile));

        return mediaFile;
    }

    private static String getTimeStamp() {
        return new SimpleDateFormat(TIME_STAMP_FORMAT,
                Locale.getDefault()).format(new Date());
    }


    private void uploadDataInTempApi(File pictureFile) {
        taskCreateActivity.showpDialog();

        Call<String> call;

        //    File file = new File(imagePath);
        //    Log.e("AttachmentApi: ", file.getName());
        //   Log.e("uploadProfile++:11 ", imagePath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), pictureFile);
        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part imageFile = MultipartBody.Part.createFormData("media", pictureFile.getName(), requestFile);
        call = ApiClient.getClient().getTaskTempAttachmentMediaData(/*"application/x-www-form-urlencoded",*/ "XMLHttpRequest", sessionManager.getTokenType() + " " + sessionManager.getAccessToken(), imageFile);


        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                taskCreateActivity.hidepDialog();
                Log.e("Response", response.toString());
                if (response.code() == 422) {
                    taskCreateActivity.showToast(response.message(), taskCreateActivity);
                    return;
                }

                try {
                    String strResponse = response.body();
                    Log.e("body", strResponse);
                    JSONObject jsonObject = new JSONObject(strResponse);
                    Log.e("json", jsonObject.toString());
                    if (jsonObject.has("data")) {
                        JSONObject jsonObject_data = jsonObject.getJSONObject("data");
                        AttachmentModel attachment = new AttachmentModel().getJsonToModel(jsonObject_data);
                        if (attachmentArrayList.size() != 0) {
                            attachmentArrayList.add(attachmentArrayList.size() - 1, attachment);
                        }
                    }

                    //attachmentAdapter.addItems(attachmentArrayList);
                    attachmentAdapter.notifyItemInserted(0);
                    //  adapter.notifyItemRangeInserted(0,attachmentArrayList.size());
                    taskCreateActivity.showToast("attachment added", taskCreateActivity);
                } catch (JSONException e) {
                    taskCreateActivity.showToast("Something went wrong", taskCreateActivity);

                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                taskCreateActivity.hidepDialog();
                Log.e("Response", call.toString());
            }
        });

    }

    public String getPath(Uri uri) {
        Cursor cursor = taskCreateActivity.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = taskCreateActivity.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();


        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));

        Log.e("path", path);
        cursor.close();

        return path;
    }


}
