package com.jobtick.android.widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.recyclerview.widget.RecyclerView;

public class EndLessRecyclerView extends RecyclerView {

    private final EndlessRecyclerViewOnScrollListener endlessScroller;

    private OnLoadMore onLoadMore;

    public EndLessRecyclerView(Context context) {
        this(context, null);
    }

    public EndLessRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EndLessRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        endlessScroller = new EndlessRecyclerViewOnScrollListener() {
            @Override
            public void onLoadMore(int currentPage) {
                onLoadMore.loadMore();
            }

            @Override
            public void onScrollDown() {

            }

            @Override
            public void onScrollUp() {

            }

            @Override
            public int getTotalItem() {
                return 0;
            }
        };

        this.addOnScrollListener(endlessScroller);
    }

    public OnLoadMore getOnLoadMore() {
        return onLoadMore;
    }

    public void setOnLoadMore(OnLoadMore onLoadMore) {
        this.onLoadMore = onLoadMore;
    }

    public interface OnLoadMore{

        void loadMore();
    }
}
