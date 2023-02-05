package com.jobtick.android.adapers;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.jobtick.android.R;

import android.annotation.SuppressLint;

import com.jobtick.android.interfaces.PaymentOnClick;
import com.jobtick.android.models.payments.PaymentHistory;
import com.jobtick.android.utils.ImageUtil;
import com.jobtick.android.utils.StringUtils;
import com.jobtick.android.utils.TimeHelper;
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
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.koloft)
        View koloft;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.nazok)
        View nazok;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.bottomPart)
        View bottomPart;

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
        if (temp.getTask() != null)
            holder.descriptionTxt.setText(temp.getTask().getTitle());

        if (isPoster) {
            if (temp.getTask().getWorker() != null) {
                holder.nameTxt.setText(temp.getTask().getWorker().getName());
                if (temp.getTask().getWorker().getAvatar() != null) {
                    ImageUtil.displayImage(holder.ivAvatar, temp.getTask().getWorker().getAvatar().getUrl(), null);
                }
            }
        } else {
            if (temp.getTask().getPoster() != null) {
                holder.nameTxt.setText(temp.getTask().getPoster().getName());
                if (temp.getTask().getPoster().getAvatar() != null) {
                    ImageUtil.displayImage(holder.ivAvatar, temp.getTask().getPoster().getAvatar().getUrl(), null);
                }
            }
        }

        holder.priceTxt.setText(StringUtils.getPriceTxt(temp.getAmount()));
        if (temp.getType().equals("debit")) {
            holder.debitedTxt.setVisibility(View.VISIBLE);
        } else {
            holder.debitedTxt.setVisibility(View.GONE);
        }

        if (position == 0) {
            holder.dateTxt.setText(TimeHelper.convertToJustDateFormat(temp.getCreatedAt()));
            holder.dateTxt.setVisibility(View.VISIBLE);
            holder.koloft.setVisibility(View.GONE);
            holder.nazok.setVisibility(View.GONE);
        } else {
            if (!TimeHelper.convertToJustDateFormat(items.get(position - 1).getCreatedAt()).equals(
                    TimeHelper.convertToJustDateFormat(temp.getCreatedAt()))) {

                holder.dateTxt.setText(TimeHelper.convertToJustDateFormat(temp.getCreatedAt()));
                holder.dateTxt.setVisibility(View.VISIBLE);
                holder.koloft.setVisibility(View.VISIBLE);
                holder.nazok.setVisibility(View.GONE);
            } else {
                holder.koloft.setVisibility(View.GONE);
                holder.nazok.setVisibility(View.VISIBLE);
                holder.dateTxt.setVisibility(View.GONE);

            }
        }
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.WRAP_CONTENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT
//        );
//        try {
//
//
//            if (holder.nazok.getVisibility() == View.VISIBLE) {
//                params.setMargins(0, 12, 0, 12);
//                holder.parentKoloftNazok.setLayoutParams(params);
//            } else {
//                params.setMargins(0, 12, 0, 12);
//                holder.parentKoloftNazok.setLayoutParams(params);
//            }
//        }catch (Exception e){
//            Log.d("asdadadasdasd", e.toString());
//        }

        holder.itemView.setOnClickListener(view -> onclick.onClick(temp));

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

}