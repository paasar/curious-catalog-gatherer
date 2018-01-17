package fi.raah.android.curious_catalog_gatherer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import fi.raah.android.curious_catalog_gatherer.ui.camera.GraphicOverlay;

public class TextGraphic extends GraphicOverlay.Graphic {

    private static final int TRANSPARENT_BLACK_COLOR = Color.argb(60, 0, 0,0);
    private static final int WHITE_COLOR = Color.WHITE;
    private static final int MARGIN = 100;
    private static int TOP = MARGIN * 3;
    private Paint sRectPaint;
    private Paint sTextPaint;
    private String text;

    TextGraphic(GraphicOverlay overlay, String text) {
        super(overlay);

        this.text = text;

        if (sRectPaint == null) {
            sRectPaint = new Paint();
            sRectPaint.setColor(TRANSPARENT_BLACK_COLOR);
            sRectPaint.setStyle(Paint.Style.FILL);
        }

        if (sTextPaint == null) {
            sTextPaint = new Paint();
            sTextPaint.setColor(WHITE_COLOR);
            sTextPaint.setTextSize(80f);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        RectF rect = new RectF();

        rect.set(MARGIN, TOP, MARGIN + 300, TOP + 100);

        canvas.drawRect(rect, sRectPaint);
        canvas.drawText(text, MARGIN + 10, TOP + 75, sTextPaint);
    }

    @Override
    public boolean contains(float x, float y) {
        return false;
    }
}
