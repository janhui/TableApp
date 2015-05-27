package huddletable.tableapp.Listeners;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Handler;
import android.view.View;

import huddletable.tableapp.TableActivity;
import huddletable.tableapp.util.SystemUiHider;

/**
 * Created by josekalladanthyil on 27/05/15.
 */
public class MyOnVisibilityChangeListener implements SystemUiHider.OnVisibilityChangeListener {
    private View controlsView;
    private int shortAnimTime;
    private SystemUiHider mSystemUiHider;
    // Cached values.
    int mControlsHeight;
    int mShortAnimTime;

    public MyOnVisibilityChangeListener(View controlsView, int shortAnimTime, SystemUiHider mSystemUiHider) {
        this.controlsView = controlsView;
        this.shortAnimTime = shortAnimTime;
        this.mSystemUiHider = mSystemUiHider;
    }

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void onVisibilityChange(boolean visible) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            // If the ViewPropertyAnimator API is available
            // (Honeycomb MR2 and later), use it to animate the
            // in-layout UI controls at the bottom of the
            // screen.
            if (mControlsHeight == 0) {
                mControlsHeight = controlsView.getHeight();
            }
            if (mShortAnimTime == 0) {
                mShortAnimTime = shortAnimTime;
            }
            controlsView.animate()
                    .translationY(visible ? 0 : mControlsHeight)
                    .setDuration(mShortAnimTime);
        } else {
            // If the ViewPropertyAnimator APIs aren't
            // available, simply show or hide the in-layout UI
            // controls.
            controlsView.setVisibility(visible ? View.VISIBLE : View.GONE);
        }

        if (visible && TableActivity.AUTO_HIDE) {
            // Schedule a hide().
            delayedHide(TableActivity.AUTO_HIDE_DELAY_MILLIS);
        }
    }
    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    public void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            mSystemUiHider.hide();
        }
    };
}
