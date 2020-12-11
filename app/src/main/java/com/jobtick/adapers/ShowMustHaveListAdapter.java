package com.jobtick.adapers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.jobtick.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShowMustHaveListAdapter extends RecyclerView.Adapter<ShowMustHaveListAdapter.OriginalViewHolder> {

    private final List<String> items;

    private Context ctx;


    public ShowMustHaveListAdapter(List<String> items) {
        this.items = items;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
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
    public ShowMustHaveListAdapter.OriginalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ctx = parent.getContext();
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_must_have, parent, false);
        return new OriginalViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ShowMustHaveListAdapter.OriginalViewHolder holder, int position) {
        String string = items.get(position);
        holder.txtBtnAddMustHave.setText(string);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}