package fi.raah.android.curious_catalog_gatherer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.google.android.gms.vision.text.Text;

import java.util.List;

import fi.raah.android.curious_catalog_gatherer.ui.camera.GraphicOverlay;

public class TextGraphic extends GraphicOverlay.Graphic {

    private static final int TEXT_COLOR = Color.RED;

    private final List<String> text;
    private static Paint sRectPaint;
    private static Paint sTextPaint;

    public TextGraphic(GraphicOverlay overlay, List<String> text) {
        super(overlay);
        this.text = text;

        if (sRectPaint == null) {
            sRectPaint = new Paint();
            sRectPaint.setColor(TEXT_COLOR);
            sRectPaint.setStyle(Paint.Style.STROKE);
            sRectPaint.setStrokeWidth(4.0f);
        }

        if (sTextPaint == null) {
            sTextPaint = new Paint();
            sTextPaint.setColor(TEXT_COLOR);
            sTextPaint.setTextSize(54.0f);
        }
        // Redraw the overlay, as this graphic has been added.
        postInvalidate();
    }

    @Override
    public void draw(Canvas canvas) {
        if (text == null) {
            return;
        }

        // Draws the bounding box around the TextBlock.
        RectF rect = new RectF(10, 10, 100, 100);// TODO hmm?
        rect.left = translateX(rect.left);
        rect.top = translateY(rect.top);
        rect.right = translateX(rect.right);
        rect.bottom = translateY(rect.bottom);
        canvas.drawRect(rect, sRectPaint);

//       // Render the text at the bottom of the box.
//        canvas.drawText(text, rect.left, rect.bottom, sTextPaint);
        int y = 0;
        for (String row: text) {
            canvas.drawText(row, 50, y, sTextPaint);
            y += 40;
        }
//        // Break the text into multiple lines and draw each one according to its own bounding box.
//        List<? extends Text> textComponents = mText.getComponents();
//        for(Text currentText : textComponents) {
//            float left = translateX(currentText.getBoundingBox().left);
//            float bottom = translateY(currentText.getBoundingBox().bottom);
//            canvas.drawText(currentText.getValue(), left, bottom, sTextPaint);
//        }
    }

    @Override
    public boolean contains(float x, float y) {
        if (text == null) {
            return false;
        }
        RectF rect = new RectF(10, 10, 100, 100);//TODO hmm?
        rect.left = translateX(rect.left);
        rect.top = translateY(rect.top);
        rect.right = translateX(rect.right);
        rect.bottom = translateY(rect.bottom);
        return (rect.left < x && rect.right > x && rect.top < y && rect.bottom > y);
    }
}
