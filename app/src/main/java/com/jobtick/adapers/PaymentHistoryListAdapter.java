package com.jobtick.adapers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.jobtick.R;
import android.annotation.SuppressLint;

import com.jobtick.interfaces.PaymentOnClick;
import com.jobtick.models.payments.PaymentHistory;
import com.jobtick.utils.ImageUtil;
import com.jobtick.utils.StringUtils;
import com.jobtick.utils.TimeHelper;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PaymentHistoryListAdapter extends RecyclerView.Adapter<PaymentHistoryListAdapter.ViewHolder> {

    private List<PaymentHistory> items = new ArrayList<>();
    private final boolean isPoster;
    private final PaymentOnClick onclick;

    public PaymentHistoryListAdapter(List<PaymentHistory> items, boolean isPoster, PaymentOnClick onclick) {
        this.items = items;
        this.isPoster = isPoster;
        this.onclick = onclick;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.ivAvatar)
        CircularImageView ivAvatar;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.dateTxt)
        TextView dateTxt;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.descriptionTxt)
        TextView descriptionTxt;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.nameTxt)
        TextView nameTxt;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.priceTxt)
        TextView priceTxt;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.debitedTxt)
        TextView debitedTxt;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_payment_history, parent, false);
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PaymentHistory temp = items.get(position);
        holder.descriptionTxt.setText(temp.getDescription());
        if (isPoster) {
            holder.nameTxt.setText(temp.getTask().getWorker().getName());

            if (temp.getTask().getWorker().getAvatar() != null) {
                ImageUtil.displayImage(holder.ivAvatar, temp.getTask().getWorker().getAvatar().getUrl(), null);
            }
        } else {
            holder.nameTxt.setText(temp.getTask().getPoster().getName());

            if (temp.getTask().getPoster().getAvatar() != null) {
                ImageUtil.displayImage(holder.ivAvatar, temp.getTask().getPoster().getAvatar().getUrl(), null);
            }
        }

        holder.priceTxt.setText(StringUtils.getPriceTxt(temp.getAmount()));
        if (temp.getType().equals("debit")) {
            holder.debitedTxt.setVisibility(View.VISIBLE);
        } else {
            holder.debitedTxt.setVisibility(View.GONE);
        }

        if(position == 0) {
            holder.dateTxt.setText(TimeHelper.convertToJustDateFormat(temp.getCreatedAt()));
            holder.dateTxt.setVisibility(View.VISIBLE);
        }
        else{
            if(!TimeHelper.convertToJustDateFormat(items.get(position - 1).getCreatedAt()).equals(
                    TimeHelper.convertToJustDateFormat(temp.getCreatedAt()))){
                
                holder.dateTxt.setText(TimeHelper.convertToJustDateFormat(temp.getCreatedAt()));
                holder.dateTxt.setVisibility(View.VISIBLE);
            }
            else
                holder.dateTxt.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onclick.onClick(temp);
            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

}