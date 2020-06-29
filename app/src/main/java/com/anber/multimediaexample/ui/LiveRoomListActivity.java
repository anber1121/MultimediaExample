package com.anber.multimediaexample.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.anber.multimediaexample.R;
import com.anber.multimediaexample.adapter.LiveRoomListAdapter;
import com.anber.multimediaexample.base.BaseActivity;
import com.anber.multimediaexample.http.BaseSubscriber;
import com.anber.multimediaexample.http.TipView;
import com.anber.multimediaexample.http.retrofit.RetrofitServiceManager;
import com.anber.multimediaexample.utils.KitLiveRoomQueryLiveListResponse;
import com.anber.multimediaexample.widget.CustomLoadMoreView;
import com.anber.multimediaexample.widget.LiveItemDecoration;
import com.anber.multimediaexample.widget.PullToRefreshLayout;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.goldze.base.http.HttpHelper;
import com.goldze.base.utils.LogUtils;
import com.huawei.base.util.NewTitleBar;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LJ
 * @version 1.0
 * @date 2019-4-9
 * @fileName
 */

public class LiveRoomListActivity extends BaseActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private NewTitleBar titleBar;

    private RecyclerView rvLiveRoom;

    private LiveRoomListAdapter adapter;

    private Button btnCreateLiveRoom;

    private PullToRefreshLayout refreshLayout;

    private int limit = 20;

    private int page = 0;

    /**
     * 刷新直播间列表
     */
    private static final int REFRESH_LIVE_LIST = 100;

    private List<KitLiveRoomQueryLiveListResponse.LiveStreamInfo> streamInfoList = new ArrayList<>();
    private CustomLoadMoreView loadingView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_room);
        initView();
        initData();
        initListener();
        queryLiveList(true);
    }

    private void queryLiveList(boolean isRefresh) {
        if (isRefresh) {
            LogUtils.d("isRefresh");
            streamInfoList.clear();
            page = 0;
        } else {
            LogUtils.d("page: "+page);
            page++;
        }
        LogUtils.d("page: "+page);
        RetrofitServiceManager.get().getApi().queryKitLiveList(limit, page).compose(HttpHelper.applySchedulers()).subscribe(new BaseSubscriber<KitLiveRoomQueryLiveListResponse>(refreshLayout, TipView.LIVE_ROOM_LIST_NO_DATA) {
            @Override
            protected void onDoNext(KitLiveRoomQueryLiveListResponse queryLiveListResponseHttpResponse) {
                if (queryLiveListResponseHttpResponse != null && queryLiveListResponseHttpResponse.getLiveStreamInfos() != null && !queryLiveListResponseHttpResponse.getLiveStreamInfos().isEmpty()) {
                    LogUtils.d("refreshLayout.setRefreshing(false)");
                    refreshLayout.setRefreshing(false);
                    streamInfoList.addAll(queryLiveListResponseHttpResponse.getLiveStreamInfos());
                    if (isRefresh) {
                        LogUtils.d("adapter.setNewDat");
                        adapter.setNewData(queryLiveListResponseHttpResponse.getLiveStreamInfos());
                    } else {
                        adapter.addData(queryLiveListResponseHttpResponse.getLiveStreamInfos());
                        if (queryLiveListResponseHttpResponse.getLiveStreamInfos().size() < limit) {
                            LogUtils.d("size() < limit loadMoreEnd(false)");
//                            adapter.loadMoreEnd(false);
                        } else {
                            LogUtils.d("size() >= limit");
//                            adapter.loadMoreComplete();
                        }
                    }
                    if (queryLiveListResponseHttpResponse.getLiveStreamInfos().size() < limit*(page+1)) {
                        LogUtils.d("size() < limit loadMoreEnd(true)");
//                        adapter.loadMoreEnd(true);
                    }
                } else {
                    LogUtils.d("refreshLayout.setRefreshing(false)");
                    if (page == 0){
                        refreshLayout.setRefreshing(false);
                        adapter.setNewData(new ArrayList<>());
                        showTipView();
                    }else {
//                        adapter.loadMoreEnd(true);
                    }
                }
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
            }
        });
    }

    private void initListener() {
        titleBar.getIvLeft().setOnClickListener(v -> onBackPressed());
        btnCreateLiveRoom.setOnClickListener(this);
    }

    private void initView() {
        refreshLayout = findViewById(R.id.pull_refresh);
        titleBar = findViewById(R.id.live_room_title);
        rvLiveRoom = findViewById(R.id.rv_live_room);
        btnCreateLiveRoom = findViewById(R.id.btn_create_live_room);
        adapter = new LiveRoomListAdapter(R.layout.item_live_room_list);
        rvLiveRoom.setLayoutManager(new GridLayoutManager(this, 2));
        rvLiveRoom.addItemDecoration(new LiveItemDecoration());
        rvLiveRoom.setAdapter(adapter);
    }

    private void initData() {
        refreshLayout.setOnRefreshListener(this);
        loadingView = new CustomLoadMoreView();
//        adapter.setLoadMoreView(loadingView);
//        adapter.setEnableLoadMore(true);
//        adapter.setOnLoadMoreListener(this, rvLiveRoom);
        adapter.setOnItemClickListener((baseQuickAdapter, view, i) -> {
//            Intent intent = new Intent(LiveRoomListActivity.this, LiveRoomActivity.class);
//            intent.putExtra("userId", streamInfoList.get(i).getUserId());
//            intent.putExtra("userName", streamInfoList.get(i).getUserName());
//            intent.putExtra("playUrl", (Serializable) streamInfoList.get(i).getUrl());
////            intent.putExtra("playUrl", streamInfoList.get(i).getUrl().get(0));
//            intent.putExtra("roomId", streamInfoList.get(i).getRoomId());
//            intent.putExtra("onLineCount", streamInfoList.get(i).getOnLineCount());
//            if (streamInfoList.get(i).getLianMaiMap() != null) {
//                intent.putExtra("audience", streamInfoList.get(i).getLianMaiMap().size());
//            }
//            startActivity(intent);
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_create_live_room:
//                Intent intent = new Intent(this, CreateLiveRoomActivity.class);
//                startActivityForResult(intent, REFRESH_LIVE_LIST);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REFRESH_LIVE_LIST:
                if (resultCode == RESULT_OK) {
                    queryLiveList(true);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onRefresh() {
        LogUtils.d("onRefresh()");
        queryLiveList(true);
    }
}
