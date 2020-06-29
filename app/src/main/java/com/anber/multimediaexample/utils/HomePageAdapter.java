package com.anber.multimediaexample.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anber.multimediaexample.R;

import java.util.List;

/**
 * @author LJ
 * @version 1.0
 * @date 2019-4-2
 * @fileName com.yjsm.host.cloudvideo.moudle.host
 */
public class HomePageAdapter extends RecyclerView.Adapter<HomePageAdapter.HomeHolder> implements View.OnClickListener {

    private List<String> types;

    private List<Integer> imgs;

    private Context context;

    private ItemClickListener itemClickListener;

    public HomePageAdapter(Context context, List<String> types, List<Integer> imgs) {
        this.context = context;
        this.types = types;
        this.imgs = imgs;
    }

    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public HomeHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_home_page, viewGroup, false);
        return new HomeHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeHolder viewHolder, int i) {
        viewHolder.imageView.setImageResource(imgs.get(i));
        viewHolder.tvShort.setText(types.get(i));
        viewHolder.itemView.setTag(i);
        viewHolder.itemView.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        return types.size();
    }

    @Override
    public void onClick(View v) {
        if (itemClickListener != null) {
            itemClickListener.onClick((Integer) v.getTag());
        }
    }

    public interface ItemClickListener {
        void onClick(int position);
    }

    class HomeHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        private TextView tvShort;

        public HomeHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_short_video);
            tvShort = itemView.findViewById(R.id.tv_video);
        }
    }
}
