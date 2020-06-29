package com.anber.multimediaexample.adapter;

import com.anber.multimediaexample.R;
import com.anber.multimediaexample.utils.KitLiveRoomQueryLiveListResponse;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;


/**
 * @author LJ
 * @version 1.0
 * @date 2019-4-9
 * @fileName com.yjsm.host.cloudvideo.moudle.liveroom.adapter.LiveRoomListAdapter
 */
public class LiveRoomListAdapter extends BaseQuickAdapter<KitLiveRoomQueryLiveListResponse.LiveStreamInfo, BaseViewHolder> {


    public LiveRoomListAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, KitLiveRoomQueryLiveListResponse.LiveStreamInfo o) {
        baseViewHolder.setText(R.id.tv_live_room_name, o.getUserName());
        baseViewHolder.setText(R.id.tv_watch_number_1, String.valueOf(o.getOnLineCount()));
    }
}
