package com.jobtick.android.widget;

import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

public abstract class EndlessRecyclerViewOnScrollListener extends RecyclerView.OnScrollListener {


    public static String TAG = "EndlessScrollListener";

    private int previousTotal = 0; // The total number of items in the dataset after the last load
    private boolean loading = true; // True if we are still waiting for the last set of data to load.
    private final int visibleThreshold = 1; // The minimum amount of items to have below your current scroll position before loading more.
    int firstVisibleItem, visibleItemCount, totalItemCount;

    private int currentPage = 1;
    private int firstVisibleInListview = -1;

    RecyclerViewPositionHelper mRecyclerViewHelper;

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        mRecyclerViewHelper = RecyclerViewPositionHelper.createHelper(recyclerView);
        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = mRecyclerViewHelper.getItemCount();
        firstVisibleItem = mRecyclerViewHelper.findFirstVisibleItemPosition();
        Log.i("RecyclerView scrolled: ", totalItemCount + ", " + getTotalItem());

        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
            }
        }
        if (!loading && (totalItemCount - visibleItemCount)
                <= (firstVisibleItem + visibleThreshold)) {
            if(totalItemCount != getTotalItem()) {
                currentPage++;
                System.out.println("myTasksFrag: loadmore");
                onLoadMore(currentPage);
                loading = true;
            }
        }

        firstVisibleInListview = firstVisibleItem;

    }

    //Start loading
    public abstract void onLoadMore(int currentPage);
    public abstract void onScrollDown();
    public abstract void onScrollUp();
    public abstract int getTotalItem();

    public void reset(){
        previousTotal = 0;
        loading = true;
        currentPage = 1;
    }
}
