package com.anber.multimediaexample.adapter;

import android.content.Context;
import android.graphics.Rect;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.anber.multimediaexample.R;
import com.anber.multimediaexample.R;


public class AudienceIconDecoration extends RecyclerView.ItemDecoration {

    private Context context;

    public AudienceIconDecoration(Context context) {
        this.context = context;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int child = parent.getChildAdapterPosition(view);
        int px = context.getResources().getDimensionPixelOffset(R.dimen.icon_5);
        if (child == 0) {
            outRect.set(0, 0, 0, 0);
        } else {
            outRect.set(0, 0, px, 0);
        }
    }
}
