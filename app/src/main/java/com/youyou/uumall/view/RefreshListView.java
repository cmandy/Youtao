package com.youyou.uumall.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.ListView;

import com.youyou.uumall.R;
import com.youyou.uumall.utils.MyLogger;

import org.androidannotations.annotations.EView;
import org.androidannotations.annotations.UiThread;

import pl.droidsonroids.gif.GifImageView;

/**
 * Created by Administrator on 2016/6/5 0005.
 *
 * @author cmandy
 */
@EView
public class RefreshListView extends ListView implements AbsListView.OnScrollListener, Animator.AnimatorListener {

    private final Context context;
    private int headerHeight;
    private static final int PULLDOWN_STATE = 0;// 下拉刷新状态
    private static final int RELEASE_STATE = 1;// 松开刷新状态
    private static final int REFRESHING_STATE = 2;// 正在刷新状态
    private int current_state = PULLDOWN_STATE;
    private View headerView;
    private OnRefreshListener onRefreshListener;
    private OnLoadMoreListener onOnLoadMoreListener;
    private GifImageView lv_gv;
    private MyLogger log;
    private View footerView;
    private int footerHeight;
    private boolean isLoadMome;
    private int diffY;
    private boolean isAutoRefresh = true;

    public RefreshListView(Context context) {
        this(context, null);
    }

    public RefreshListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RefreshListView, defStyleAttr, 0);
        if (typedArray != null && typedArray.length() != 0) {
            int attr = typedArray.getIndex(0);
            if (attr == R.styleable.RefreshListView_isLoadMore) {
                isLoadMome = typedArray.getBoolean(0, false);
            }
            typedArray.recycle();
        }
        log = MyLogger.getLogger("refresh");
        addHeader();
        if (isLoadMome) {
            addFooter();
        }
    }

    private void addFooter() {
        footerView = View.inflate(getContext(), R.layout.item_home_refresh_foot, null);
        // 隐藏脚布局
        footerView.measure(0, 0);
        footerHeight = footerView.getMeasuredHeight();
        footerView.setPadding(0, -footerHeight, 0, 0);
        addFooterView(footerView);
        // 监听Listview滚动状态
        setOnScrollListener(this);
    }

    //初始化头布局操作
    private void addHeader() {
        headerView = View.inflate(getContext(), R.layout.item_home_refresh_head, null);
        lv_gv = (GifImageView) headerView.findViewById(R.id.lv_gv);
        lv_gv.setBackgroundResource(R.drawable.xiala_jia_zai);
//        lv_gv.setScaleType(ImageView.ScaleType.CENTER);
        headerView.measure(0, 0);
        headerHeight = headerView.getMeasuredHeight();
//        log.e("控件高度为:"+headerHeight);
        headerView.setPadding(0, -headerHeight, 0, 0);
        addHeaderView(headerView);
    }

    //初始点Y
    private int downY = -1;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (downY == -1) {
                    downY = (int) ev.getY();
                }
                if (getFirstVisiblePosition() != 0) {
                    break;
                }
                if (current_state == REFRESHING_STATE) {
                    break;
                }
                int moveY = (int) ev.getY();
                diffY = (moveY - downY) / 2;
                if (diffY > 0) {
                    int topPadding = diffY - headerHeight;
                    if (topPadding < 0 && current_state != PULLDOWN_STATE) {
                        current_state = PULLDOWN_STATE;
                    } else if (topPadding > 0 && current_state != RELEASE_STATE) {
                        current_state = RELEASE_STATE;
                    }
                    headerView.setPadding(0, topPadding, 0, 0);
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                downY = -1;
                if (current_state == PULLDOWN_STATE) {//没拉到位状态,这个时候应该写动画,让这个toppadding从当前状态变成-headerHeight
                    ValueAnimator animator = ValueAnimator.ofInt(diffY - headerHeight, -headerHeight);
                    animator.setDuration(300);
                    animator.setInterpolator(new AccelerateDecelerateInterpolator());
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            Integer animatedValue = (Integer) animation.getAnimatedValue();
                            headerView.setPadding(0, animatedValue, 0, 0);

                        }
                    });
                    animator.start();
                } else if (current_state == RELEASE_STATE) {
                    current_state = REFRESHING_STATE;//正在刷新
                    isAutoRefresh = false;//刷新为手动刷新
                    ValueAnimator animator = ValueAnimator.ofInt(diffY - headerHeight, 0);
                    animator.setDuration(300);
                    animator.setInterpolator(new AccelerateDecelerateInterpolator());
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            Integer animatedValue = (Integer) animation.getAnimatedValue();
                            headerView.setPadding(0, animatedValue, 0, 0);

                        }
                    });
                    animator.start();
                    animator.addListener(this);
                }

                break;

        }
        return super.onTouchEvent(ev);
    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {
        if (onRefreshListener != null) {
            onRefreshListener.onRefreshing(isAutoRefresh);
        }
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }

    public void autoRefresh() {//主页如果是首次进入,加载动画
        ValueAnimator animator = ValueAnimator.ofInt(-headerHeight, 0);
        animator.setDuration(600);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer animatedValue = (Integer) animation.getAnimatedValue();
                headerView.setPadding(0, animatedValue, 0, 0);
            }
        });
        animator.start();
        animator.addListener(this);
    }

    @UiThread
    public void onRefreshComplete() {
        current_state = PULLDOWN_STATE;
        ValueAnimator animator = ValueAnimator.ofInt(0, -headerHeight);
        animator.setDuration(300);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer animatedValue = (Integer) animation.getAnimatedValue();
                headerView.setPadding(0, animatedValue, 0, 0);

            }
        });
        animator.start();
    }

    private boolean isLoadMore = false;

    public void onLoadMoreComplete() {
        footerView.setPadding(0, -footerHeight, 0, 0);
        isLoadMore = false;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (OnScrollListener.SCROLL_STATE_IDLE == scrollState) {
            int position = getLastVisiblePosition();
            boolean load = false;
            int count = getCount() - 1;
            if (position == count || position == count - 1 || getLastVisiblePosition() == count - 2 && getLastVisiblePosition() == count - 3) {
                load = true;
            }
            int measuredHeight = getChildAt(0).getMeasuredHeight();//子条目高度
//            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
//            int widthPixels = displayMetrics.widthPixels;
            int widthPixels = getMeasuredHeight();
            if (measuredHeight * getChildCount()<widthPixels) {//避免在条目没有占满一屏幕的情况下调用到上拉加载的方法
                load = false;
            }
            if (load && !isLoadMore) {
                isLoadMore = true;
//                footerView.setPadding(0, 0, 0, 0);
                // 让加载更多脚布局自动显示
//                setSelection(getCount());
                // 调用外界加载更多的业务
                if (onOnLoadMoreListener != null) {
                    onOnLoadMoreListener.onLoadingMore();
                }
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }


    public interface OnRefreshListener {
        void onRefreshing(boolean isAuto);
    }

    public interface OnLoadMoreListener {
        void onLoadingMore();
    }

    public void setOnRefreshListener(OnRefreshListener listener) {
        this.onRefreshListener = listener;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        this.onOnLoadMoreListener = listener;
    }
}
