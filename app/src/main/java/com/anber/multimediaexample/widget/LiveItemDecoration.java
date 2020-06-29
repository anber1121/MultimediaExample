package com.anber.multimediaexample.widget;

import android.graphics.Rect;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

public class LiveItemDecoration extends RecyclerView.ItemDecoration {

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int child = parent.getChildAdapterPosition(view) + 1;
        if (child % 2 == 0) {
            outRect.set(0, 10, 0, 0);
        } else {
            outRect.set(0, 10, 10, 0);
        }
    }
}
