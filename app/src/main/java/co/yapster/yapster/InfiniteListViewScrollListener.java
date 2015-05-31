package co.yapster.yapster;

import android.widget.AbsListView;

/**
 * Created by gurkarangulati on 2/8/15.
 */
public abstract class InfiniteListViewScrollListener implements AbsListView.OnScrollListener {

    private int bufferItemCount = 5;
    private int currentPage = 1;
    private int itemCount;
    private int firstVisibleItem;
    private int visibleItemCount;
    private int totalItemCount = 0;
    private int scrollState = 0;
    private boolean isLoading = false;
    public boolean hasLoadedAll = false;

    public InfiniteListViewScrollListener(int bufferItemCount){
        this.bufferItemCount = bufferItemCount;
    }

    public abstract void loadMore(int page, int totalItemsCount);


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        this.scrollState = scrollState;
        if (scrollState == SCROLL_STATE_IDLE){
            if (hasLoadedAll == false) {
                this.isScrollCompleted();
            }
        }else if (scrollState == SCROLL_STATE_FLING){
        }else if (scrollState == SCROLL_STATE_TOUCH_SCROLL){
        }

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            this.firstVisibleItem = firstVisibleItem;
            this.visibleItemCount = visibleItemCount;
            this.itemCount = totalItemCount;
    }

    private void isScrollCompleted() {
        if (!isLoading && this.visibleItemCount > 0 && (totalItemCount - visibleItemCount)<=(firstVisibleItem + bufferItemCount)){
                isLoading = true;
                currentPage++;
                loadMore(currentPage, itemCount);
                isLoading = false;
        }
    }
}
