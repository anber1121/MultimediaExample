package com.anber.multimediaexample.ItemDecoration;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

public class GridItemDecoration extends RecyclerView.ItemDecoration {

    private Paint mPaint;
    private int paintColor;
    private int width;
    private int height;

    public GridItemDecoration() {
    }

    public GridItemDecoration(int paintColor, int width, int height) {
        this.paintColor = paintColor;
        this.width = width;
        this.height = height;
        initPaint();
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        drawVertical(c, parent);
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setColor(paintColor);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(width);
    }

    //绘制纵向分割线
    private void drawVertical(Canvas c, RecyclerView parent) {
        int childSize = parent.getChildCount();
        for (int i = 0; i < childSize; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
            int left = child.getRight() + layoutParams.rightMargin;
            int top = child.getTop();
            int right = left + width;
            int bottom = child.getBottom() + 10;

            if (mPaint != null && i % 3 != 2) {
                c.drawRect(left, top, right, bottom, mPaint);
            }
        }
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int index = parent.getChildAdapterPosition(view) + 1;
        int left = 0;
        int right = 0;
        int top = 10;
        int bottom = 0;
        switch (index % 3) {
            case 0:
                right = 10;
                break;
            case 1:
                right = 10;
                break;
            case 2:
                right = 0;
                break;
            default:
                break;
        }
        outRect.set(left, top, right, bottom);
    }
}
