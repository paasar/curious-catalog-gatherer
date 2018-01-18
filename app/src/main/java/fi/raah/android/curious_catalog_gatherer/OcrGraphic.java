/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fi.raah.android.curious_catalog_gatherer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;

import java.util.List;

import fi.raah.android.curious_catalog_gatherer.ui.camera.GraphicOverlay;

/**
 * Graphic instance for rendering TextBlock position, size, and ID within an associated graphic
 * overlay view.
 */
public class OcrGraphic extends GraphicOverlay.Graphic {

    private int mId;

    static final int RED_COLOR = Color.RED;
    static final int GREEN_COLOR = Color.GREEN;

    private Paint sRectPaint;
    private Paint sTextPaint;
    private final TextBlock mTextBlock;
    private final String overridingText;

    OcrGraphic(GraphicOverlay overlay, TextBlock textBlock, String overridingText, int color) {
        super(overlay);

        mTextBlock = textBlock;
        this.overridingText = overridingText;

        if (sRectPaint == null) {
            sRectPaint = new Paint();
            sRectPaint.setColor(color);
            sRectPaint.setStyle(Paint.Style.STROKE);
            sRectPaint.setStrokeWidth(4.0f);
        }

        if (sTextPaint == null) {
            sTextPaint = new Paint();
            sTextPaint.setColor(color);

            float textHeight = (float) textBlock.getBoundingBox().height() * 2;
            sTextPaint.setTextSize(textHeight);
        }
        // Redraw the overlay, as this graphic has been added.
        postInvalidate();
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public TextBlock getTextBlock() {
        return mTextBlock;
    }

    /**
     * Checks whether a point is within the bounding box of this graphic.
     * The provided point should be relative to this graphic's containing overlay.
     * @param x An x parameter in the relative context of the canvas.
     * @param y A y parameter in the relative context of the canvas.
     * @return True if the provided point is contained within this graphic's bounding box.
     */
    public boolean contains(float x, float y) {
        // TODO: Check if this graphic's overridingText contains this point.
        if (mTextBlock == null) {
            return false;
        }
        RectF rect = new RectF(mTextBlock.getBoundingBox());
        rect.left = translateX(rect.left);
        rect.top = translateY(rect.top);
        rect.right = translateX(rect.right);
        rect.bottom = translateY(rect.bottom);
        return (rect.left < x && rect.right > x && rect.top < y && rect.bottom > y);
    }

    /**
     * Draws the text block annotations for position, size, and raw value on the supplied canvas.
     */
    @Override
    public void draw(Canvas canvas) {
        // TODO: Draw the overridingText onto the canvas.
        if (mTextBlock == null) {
            return;
        }

        // Draws the bounding box around the TextBlock.
        RectF rect = new RectF(mTextBlock.getBoundingBox());
        rect.left = translateX(rect.left);
        rect.top = translateY(rect.top);
        rect.right = translateX(rect.right);
        rect.bottom = translateY(rect.bottom);
        canvas.drawRect(rect, sRectPaint);

        // Break the text into multiple lines and draw each one according to its own bounding box.
        List<? extends Text> textComponents = mTextBlock.getComponents();
        for(Text currentText : textComponents) {
            float left = translateX(currentText.getBoundingBox().left);
            float bottom = translateY(currentText.getBoundingBox().bottom);
            if (overridingText == null) {
                canvas.drawText(currentText.getValue(), left, bottom, sTextPaint);
            } else {
                canvas.drawText(overridingText, left, bottom, sTextPaint);
            }
        }
    }
}
