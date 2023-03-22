package com.qw.curtain.lib.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;

import com.qw.curtain.lib.HollowInfo;
import com.qw.curtain.lib.debug.CurtainDebug;

public class NoInterceptViewAlertDialog extends Dialog {

    public static final String TAG = "NoInterceptViewAlertDialog";

    private final SparseArray<HollowInfo> hollows;

    public NoInterceptViewAlertDialog(@NonNull Context context, int themeResId, SparseArray<HollowInfo> hollows) {
        super(context, themeResId);
        this.hollows = hollows;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        if (window != null) {
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        return super.onTouchEvent(event) || tryHandByHollows(event);
    }

    private boolean tryHandByHollows(MotionEvent event) {
        for (int i = 0, size = hollows.size(); i < size; i++) {
            HollowInfo current = hollows.valueAt(i);
            if (isEventInHollowField(event, current)) {
                return current.targetView.dispatchTouchEvent(event);
            }
        }
        return false;
    }

    private boolean isEventInHollowField(MotionEvent event, HollowInfo hollowInfo) {
        int[] hollowLocation = new int[2];
        hollowInfo.targetView.getLocationOnScreen(hollowLocation);
        int xRangeEnd = hollowLocation[0] + hollowInfo.targetView.getWidth();
        int yRangeEnd = hollowLocation[1] + hollowInfo.targetView.getHeight();
        boolean inXRange = (event.getRawX() > hollowLocation[0]) && (event.getRawX() < xRangeEnd);
        boolean inYRange = (event.getRawY() > hollowLocation[1]) && (event.getRawY() < yRangeEnd);
        CurtainDebug.d(TAG, " eventRawX " + event.getRawX() + " eventRawY " + event.getRawY());
        CurtainDebug.d(TAG, " inX " + inXRange + " inY " + inYRange);
        return inXRange && inYRange;
    }

}
