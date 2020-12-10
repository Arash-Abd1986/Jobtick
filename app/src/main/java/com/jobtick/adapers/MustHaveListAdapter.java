package com.jobtick.adapers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.jobtick.R;
import com.jobtick.models.MustHaveModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MustHaveListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<MustHaveModel> items = new ArrayList<>();
    private OnLoadMoreListener onLoadMoreListener;

    private Context ctx;
    private OnItemClickListener mOnItemClickListener;
    private onCheckedAllItem onCheckedAllItem;


    public interface onCheckedAllItem {
        void onCheckedAllItem();
    }

    public void onCheckedAllItems(final onCheckedAllItem onChecked) {
        this.onCheckedAllItem = onChecked;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, MustHaveModel obj, int position, String action);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public MustHaveListAdapter(Context context, List<MustHaveModel> items) {
        this.items = items;
        ctx = context;

    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.cb_receive_alerts)
        CheckBox cbReceiveAlerts;
        @BindView(R.id.txt_btn_add_must_have)
        TextView txtBtnAddMustHave;

        @BindView(R.id.lytMain)
        LinearLayout lytMain;

        public OriginalViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_check_must_have, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof OriginalViewHolder) {
            OriginalViewHolder view = (OriginalViewHolder) holder;
            String string = items.get(position).getMustHaveTitle();

            view.txtBtnAddMustHave.setText(string);

           /* view.imgBtnRemove.setOnClickListener(v -> {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(v, items.get(view.getAdapterPosition()), view.getAdapterPosition(), "action");
                }
            });*/

//            view.cbReceiveAlerts.setOnCheckedChangeListener((buttonView, isChecked) -> items.get(position).setChecked(true));
            view.cbReceiveAlerts.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        items.get(position).setChecked(true);
                        view.lytMain.setBackgroundResource(R.drawable.bg_blue_border_requirement);
                    } else {
                        items.get(position).setChecked(false);
                        view.lytMain.setBackgroundResource(R.drawable.bg_grey_border_requirement);
                    }
                    if (onCheckedAllItem != null) {
                        onCheckedAllItem.onCheckedAllItem();
                    }
                }
            });

            view.lytMain.setOnClickListener(v -> {
                if (view.cbReceiveAlerts.isChecked()) {
                    view.cbReceiveAlerts.setChecked(false);
                } else {
                    view.cbReceiveAlerts.setChecked(true);
                }
            });

            view.txtBtnAddMustHave.setOnClickListener(v -> {
                if (view.cbReceiveAlerts.isChecked()) {
                    view.cbReceiveAlerts.setChecked(false);
                } else {
                    view.cbReceiveAlerts.setChecked(true);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public boolean isAllSelected() {

        for (int i = 0; i < items.size(); i++) {
            if (!items.get(i).isChecked()) {
                return false;
            }
        }
        return true;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public interface OnLoadMoreListener {
        void onLoadMore(int current_page);
    }

}