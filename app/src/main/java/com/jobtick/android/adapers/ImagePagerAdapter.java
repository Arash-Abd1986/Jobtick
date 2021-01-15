package com.jobtick.android.adapers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;

import com.jobtick.android.R;
import com.jobtick.android.models.AttachmentModel;
import com.jobtick.android.utils.TouchImageView;

import java.util.ArrayList;

public class ImagePagerAdapter extends PagerAdapter
{
    private final LayoutInflater mLayoutInflater;
    private final ArrayList<AttachmentModel> arrayList;
    private final Context context;

    public ImagePagerAdapter(Context context,
                             ArrayList<AttachmentModel> arrayList) {
        this.arrayList=arrayList;
        this.context=context;
        mLayoutInflater = (LayoutInflater) context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position)
    {
        View itemView = mLayoutInflater.inflate(R.layout.zoom_img_item,
                container, false);

        TouchImageView ivZoom = itemView.findViewById(R.id.ivZoom);

        Glide.with(context)
                .load(arrayList.get(position).getModalUrl())
                .into(ivZoom);

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position,
                            @NonNull Object object) {
        container.removeView((LinearLayout) object);
    }
}
