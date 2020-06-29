package com.anber.multimediaexample.adapter;

import androidx.annotation.Nullable;

import com.anber.multimediaexample.R;
import com.anber.multimediaexample.widget.CircleImageView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.List;

public class AudienceIconListAdapter extends BaseQuickAdapter<Integer, BaseViewHolder> {


    public AudienceIconListAdapter(int layoutResId, @Nullable List<Integer> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, Integer o) {
        CircleImageView imageView = baseViewHolder.getView(R.id.civ_icon);
        imageView.setImageResource(R.color.black_60);
    }

}
