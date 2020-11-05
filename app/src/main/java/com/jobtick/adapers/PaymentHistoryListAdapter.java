package com.jobtick.adapers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.jobtick.R;
import com.jobtick.interfaces.PaymentOnClick;
import com.jobtick.models.payments.PaymentHistory;
import com.jobtick.utils.ImageUtil;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PaymentHistoryListAdapter extends RecyclerView.Adapter<PaymentHistoryListAdapter.ViewHolder> {

    private List<PaymentHistory> items = new ArrayList<>();
    private boolean isPoster;
    private PaymentOnClick onclick;

    public PaymentHistoryListAdapter(List<PaymentHistory> items, boolean isPoster, PaymentOnClick onclick) {
        this.items = items;
        this.isPoster = isPoster;
        this.onclick = onclick;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivAvatar)
        CircularImageView ivAvatar;
        @BindView(R.id.dateTxt)
        TextView dateTxt;
        @BindView(R.id.descriptionTxt)
        TextView descriptionTxt;
        @BindView(R.id.nameTxt)
        TextView nameTxt;
        @BindView(R.id.priceTxt)
        TextView priceTxt;
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
                ImageUtil.displayImage(holder.ivAvatar, temp.getTask().getWorker().getAvatar(), null);
            }
        } else {
            holder.nameTxt.setText(temp.getTask().getPoster().getName());

            if (temp.getTask().getPoster().getAvatar() != null) {
                ImageUtil.displayImage(holder.ivAvatar, temp.getTask().getPoster().getAvatar(), null);
            }
        }

        holder.priceTxt.setText("$ " + temp.getAmount());
        if (temp.getType().equals("debit")) {
            holder.debitedTxt.setVisibility(View.VISIBLE);
        } else {
            holder.debitedTxt.setVisibility(View.GONE);
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