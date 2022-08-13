package com.jobtick.android.utils;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class SpacesItemDecorationV2 extends RecyclerView.ItemDecoration {
    private int space;

    public SpacesItemDecorationV2(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        outRect.bottom = 0;
        outRect.top = 0;
        outRect.left = space;
        outRect.right = space;
    }
}