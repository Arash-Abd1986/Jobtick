package com.jobtick.android.pagination

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jobtick.android.utils.Constant
import timber.log.Timber

abstract class PaginationListener
/**
 * Supporting only LinearLayoutManager for now.
 */(private val layoutManager: LinearLayoutManager) : RecyclerView.OnScrollListener() {
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        val visibleItemCount = layoutManager.childCount
        val totalItemCount = layoutManager.itemCount
        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
        val check = totalItemCount - visibleItemCount + firstVisibleItemPosition
        Timber.tag("CHAT")
            .e("Current Visible = $visibleItemCount Total = $totalItemCount Scrolled Out Items = $firstVisibleItemPosition Check = $check")
        if (!isLoading && !isLastPage) {
            if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0 && totalItemCount >= Constant.PAGE_SIZE) {
                loadMoreItems()
            }
        }
    }

    protected abstract fun loadMoreItems()
    abstract val isLastPage: Boolean
    abstract val isLoading: Boolean

    companion object {
        const val PAGE_START = 1
    }
    /**
     * Set scrolling threshold here (for now i'm assuming 10 item in one page)
     */
}