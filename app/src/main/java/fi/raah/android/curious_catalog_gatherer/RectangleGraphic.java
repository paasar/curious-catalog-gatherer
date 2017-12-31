package fi.raah.android.curious_catalog_gatherer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import fi.raah.android.curious_catalog_gatherer.ui.camera.GraphicOverlay;

public class RectangleGraphic extends GraphicOverlay.Graphic {

    private static final int WHITE_COLOR = Color.WHITE;
    private static final int MARGIN = 100;
    private Paint sRectPaint;

    RectangleGraphic(GraphicOverlay overlay) {
        super(overlay);

        if (sRectPaint == null) {
            sRectPaint = new Paint();
            sRectPaint.setColor(WHITE_COLOR);
            sRectPaint.setStyle(Paint.Style.STROKE);
            sRectPaint.setStrokeWidth(10.0f);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        RectF rect = new RectF();
        rect.set(MARGIN, MARGIN, canvas.getWidth() - MARGIN, canvas.getHeight() - MARGIN);

        canvas.drawRect(rect, sRectPaint);
    }

    @Override
    public boolean contains(float x, float y) {
        return false;
    }
}
