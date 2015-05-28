package huddletable.tableapp.Listeners;

import android.graphics.PointF;
import android.graphics.RectF;

import huddletable.tableapp.TableActivity;
import huddletable.tableapp.util.TouchImageView;

/**
 * Created by josekalladanthyil on 28/05/15.
 */
public class TouchImageViewListener implements TouchImageView.OnTouchImageViewListener {
    private final TouchImageView mImage;
    private final TableActivity mTableActivity;

    public TouchImageViewListener(TouchImageView imageContentView, TableActivity tableActivity) {
        this.mImage = imageContentView;
        this.mTableActivity = tableActivity;
    }

    @Override
    public void onMove() {
        PointF point = mImage.getScrollPosition();
        RectF rect = mImage.getZoomedRect();
        float currentZoom = mImage.getCurrentZoom();
        boolean isZoomed = mImage.isZoomed();
        mTableActivity.updateZoom(currentZoom);
    }
}
