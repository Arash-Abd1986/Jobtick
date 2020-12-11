package com.jobtick.pagination;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jobtick.utils.Constant;

import timber.log.Timber;


public abstract class PaginationListener extends RecyclerView.OnScrollListener {
    public static final int PAGE_START = 1;
    @NonNull
    private final LinearLayoutManager layoutManager;
    /**
     * Set scrolling threshold here (for now i'm assuming 10 item in one page)
     */

    /**
     * Supporting only LinearLayoutManager for now.
     */
    public PaginationListener(@NonNull LinearLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }
    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        int visibleItemCount = layoutManager.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
        int check = totalItemCount - visibleItemCount+firstVisibleItemPosition;
        Timber.tag("CHAT").e("Current Visible = " + visibleItemCount + " Total = " + totalItemCount + " Scrolled Out Items = " + firstVisibleItemPosition + " Check = " + check);

        if (!isLoading() && !isLastPage()) {
            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                    && firstVisibleItemPosition >= 0
                    && totalItemCount >= Constant.PAGE_SIZE) {
                loadMoreItems();
            }
        }
    }
    protected abstract void loadMoreItems();
    public abstract boolean isLastPage();
    public abstract boolean isLoading();

}
