package com.thunder.nytsearch.adapters;

import android.widget.AbsListView;

/**
 * Created by anlinsquall on 19/3/17.
 */

public abstract class EndlessScrollListener implements AbsListView.OnScrollListener {
    private int visibleThreshold = 5;
    private int currentPage = 0;
    private int previousTotalItemCount = 0;
    private boolean loading = true;
    private  int startingPageIndex = 0;

    public EndlessScrollListener(){

    }

    public EndlessScrollListener(int visibleThreshold){
        this.visibleThreshold = visibleThreshold;
    }

    public EndlessScrollListener(int visibleThreshold, int startingPage){
        this.visibleThreshold = visibleThreshold;
        this.startingPageIndex = startingPage;
        this.currentPage = startingPage;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                         int totalItemCount) {
        // If the total item count is zero and previous isn't, assume
        // list is invalidated and should be reset back to initial stage
        if(totalItemCount < previousTotalItemCount){
            this.currentPage = this.startingPageIndex;
            this.previousTotalItemCount = totalItemCount;
            if (totalItemCount ==0){
                this.loading = true;
            }
        }
        // If it is still loading, we check to see if the data set count has changed
        // if so we conclude it has finished loading and update the current page
        // number and total item count
        if (loading && (totalItemCount > previousTotalItemCount)){
            loading = false;
            previousTotalItemCount = totalItemCount;
            currentPage ++;
        }

        // If it isn't currently loading we check to see if we have breached
        // the visibleThreshold and need to reload more data.
        // If we do not need to reload some more data, we execute onLoad more to fetch
        if (!loading && (firstVisibleItem + visibleItemCount + visibleThreshold >= totalItemCount))
            loading = onLoadMore(currentPage + 1, totalItemCount);
    }

    protected abstract boolean onLoadMore(int i, int totalItemCount);

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // Don't take any action on changed.
    }
}
