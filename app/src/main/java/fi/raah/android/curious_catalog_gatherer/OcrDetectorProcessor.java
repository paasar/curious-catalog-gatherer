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

import android.content.res.AssetManager;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;

import java.util.List;

import fi.raah.android.curious_catalog_gatherer.cards.CardService;
import fi.raah.android.curious_catalog_gatherer.ui.camera.GraphicOverlay;

/**
 * A very simple Processor which gets detected TextBlocks and adds them to the overlay
 * as OcrGraphics.
 */
public class OcrDetectorProcessor implements Detector.Processor<TextBlock> {

    private final ActivityCallback activityCallback;
    private final Settings settings;
    private GraphicOverlay<GraphicOverlay.Graphic> mGraphicOverlay;

    //TODO Dagger?
    private DetectionFilter detectionFilter;
    private CardService cardService;

    OcrDetectorProcessor(AssetManager assetManager,
                         GraphicOverlay<GraphicOverlay.Graphic> ocrGraphicOverlay,
                         ActivityCallback activityCallback,
                         Settings settings) {
        mGraphicOverlay = ocrGraphicOverlay;
        this.activityCallback = activityCallback;
        this.settings = settings;
        this.cardService = new CardService(assetManager);
        this.detectionFilter = new DetectionFilter(cardService);
    }

    @Override
    public void receiveDetections(Detector.Detections<TextBlock> detections) {
        mGraphicOverlay.clear();
        SparseArray<TextBlock> items = detections.getDetectedItems();
        Log.d("Processor", "Detections: " + items.size());

        processDetectedTextBlocks(items);
        Log.d("Processor", "----------------------------------------------");
    }

    private void processDetectedTextBlocks(SparseArray<TextBlock> items) {
        if (items.size() < 1) {
            return;
        }

        List<TextBlock> singleLineBlocks = detectionFilter.filterSingleLineBlocks(items);

        DetectionFilter.CardAndNonCard cardAndNonCard = detectionFilter.splitIntoCardsAndNonCards(singleLineBlocks);

        for (TextBlock block : cardAndNonCard.getNonCardBlocks()) {
            addGraphic(block, OcrGraphic.RED_COLOR);
        }

        for (TextBlock block : cardAndNonCard.getCardBlocks()) {
            addGraphic(block, OcrGraphic.GREEN_COLOR);
            if (settings.isSettingsOk()) {
                cardService.fetchAndUpdateOwnerData(activityCallback, block.getValue());
            }
        }

        if (!settings.isSettingsOk()) {
            activityCallback.makeToast("Settings need to be set for full functionality.");
        }
    }

    private void addGraphic(TextBlock block, int color) {
        OcrGraphic graphic = new OcrGraphic(mGraphicOverlay, block, color);
        mGraphicOverlay.add(graphic);
    }

    @Override
    public void release() {
        mGraphicOverlay.clear();
    }
}
