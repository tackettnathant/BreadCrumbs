package com.n8sqrd.breadcrumbs.ui;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

/**
 * Created by ntackett on 1/9/2018.
 */

public class PathTouchHelper extends ItemTouchHelper.SimpleCallback {
    PathTouchHelperListener listener;

    public PathTouchHelper(int dragDirs,int swipeDirs,PathTouchHelperListener listener) {
        super(dragDirs,swipeDirs);
        this.listener=listener;
    }
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        listener.onSwiped(viewHolder,direction,viewHolder.getAdapterPosition());
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (viewHolder!=null) {
            final View foreground = ((PathAdapter.ViewHolder) viewHolder).foreground;
            getDefaultUIUtil().onSelected(foreground);
        }

    }

    @Override
    public void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        final View fg = ((PathAdapter.ViewHolder) viewHolder).foreground;
        getDefaultUIUtil().onDrawOver(c,recyclerView,fg,dX,dY,actionState,isCurrentlyActive);
    }
    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        final View foregroundView = ((PathAdapter.ViewHolder) viewHolder).foreground;
        getDefaultUIUtil().clearView(foregroundView);
    }
    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView,
                            RecyclerView.ViewHolder viewHolder, float dX, float dY,
                            int actionState, boolean isCurrentlyActive) {
        final View foregroundView = ((PathAdapter.ViewHolder) viewHolder).foreground;

        getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY,actionState, isCurrentlyActive);
    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    public interface PathTouchHelperListener {
        void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position);
    }
}
