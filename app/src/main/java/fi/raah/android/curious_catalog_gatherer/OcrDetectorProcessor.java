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

import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;

import java.util.ArrayList;
import java.util.List;

import fi.raah.android.curious_catalog_gatherer.cards.CardService;
import fi.raah.android.curious_catalog_gatherer.model.EditableCardCounts;
import fi.raah.android.curious_catalog_gatherer.ui.camera.GraphicOverlay;

/**
 * A OCR Processor which gets detected TextBlocks for card recognition.
 */
public class OcrDetectorProcessor implements Detector.Processor<TextBlock> {

    private final ActivityCallback activityCallback;
    private final Settings settings;
    private GraphicOverlay<GraphicOverlay.Graphic> mGraphicOverlay;

    private int noDetectionsCounter = 0;
    private boolean cardsDetected = false;

    //TODO Dagger?
    private DetectionFilter detectionFilter;
    private CardService cardService;

    OcrDetectorProcessor(GraphicOverlay<GraphicOverlay.Graphic> ocrGraphicOverlay,
                         ActivityCallback activityCallback,
                         Settings settings,
                         CardService cardService) {
        mGraphicOverlay = ocrGraphicOverlay;
        this.activityCallback = activityCallback;
        this.settings = settings;
        this.cardService = cardService;
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
            noDetectionsCounter++;
            if (noDetectionsCounter == 5) {
                moveToNextPhase();
            }
        } else {
            noDetectionsCounter = 0;

            List<TextBlock> singleLineBlocks = detectionFilter.filterSingleLineBlocks(items);

            DetectionFilter.CardAndNonCard cardAndNonCard = detectionFilter.splitIntoCardsAndNonCards(singleLineBlocks);

            annotateNonCardsInRed(cardAndNonCard.getNonCardBlocks());

            annotateAndHandleCards(cardAndNonCard.getCardBlocks());

            if (!settings.isSettingsOk()) {
                activityCallback.makeToast("Settings need to be set for full functionality.");
            }
        }

        annotateEditableCardCounts();
    }

    private void annotateEditableCardCounts() {
        EditableCardCounts cardCounts = activityCallback.getEditableCardCounts();
        mGraphicOverlay.add(new TextGraphic(mGraphicOverlay, cardCounts.getTotal() + "|" + cardCounts.getUnique()));
    }

    private void moveToNextPhase() {
        cardService.nextPhase();
        noDetectionsCounter = 0;

        if (cardsDetected) {
            mGraphicOverlay.add(new RectangleGraphic(mGraphicOverlay));
        }
        cardsDetected = false;
    }

    private void annotateAndHandleCards(List<TextBlock> cardBlocks) {
        cardsDetected = true;

        List<String> cardNames = new ArrayList<>();
        for (TextBlock block : cardBlocks) {
            addGraphic(block, OcrGraphic.GREEN_COLOR);
            cardNames.add(block.getValue());
        }

        if (settings.isSettingsOk()) {
            cardService.fetchAndUpdateData(activityCallback, cardNames);
        }
    }

    private void annotateNonCardsInRed(List<TextBlock> nonCardBlocks) {
        for (TextBlock block : nonCardBlocks) {
            addGraphic(block, OcrGraphic.RED_COLOR);
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
