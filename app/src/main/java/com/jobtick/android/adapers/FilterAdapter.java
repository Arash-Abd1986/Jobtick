package com.jobtick.android.adapers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.jobtick.android.R;

import java.util.List;

public class FilterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_BUTTON = 0;
    private static final int VIEW_TYPE_CELL = 1;

    private final List<String> items;
    private Context context;
    private OnFilterDeleteListener mOnFilterDeleteListener;

    public FilterAdapter(List<String> items) {
        this.items = items;
    }

    public void setmOnFilterDeleteListener(OnFilterDeleteListener mOnFilterDeleteListener) {
        this.mOnFilterDeleteListener = mOnFilterDeleteListener;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {

        TextView txtData;
        CardView button;

        public OriginalViewHolder(View v) {
            super(v);
            txtData = v.findViewById(R.id.txt_data);
            button = v.findViewById(R.id.delete_filter_button);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        RecyclerView.ViewHolder vh;
        View v;
        if(viewType == VIEW_TYPE_CELL){
             v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_filter, parent, false);
        }else{
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_delete_filter, parent, false);
        }
        vh = new OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof OriginalViewHolder) {
            OriginalViewHolder view = (OriginalViewHolder) holder;
            String string = items.get(position);
            view.txtData.setText(string);

            if(position == items.size()) {
                view.button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(mOnFilterDeleteListener != null){
                            mOnFilterDeleteListener.onFilterDeleteButtonClick();
                        }
                    }
                });
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position == items.size()) ? VIEW_TYPE_BUTTON : VIEW_TYPE_CELL;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public interface OnFilterDeleteListener{
        void onFilterDeleteButtonClick();
    }

}