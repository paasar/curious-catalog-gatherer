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

import fi.raah.android.curious_catalog_gatherer.http.AsyncJsonHttpResponseHandler;
import fi.raah.android.curious_catalog_gatherer.http.CatalogClient;
import fi.raah.android.curious_catalog_gatherer.model.Ownage;
import fi.raah.android.curious_catalog_gatherer.ui.camera.GraphicOverlay;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import org.json.*;

import cz.msebera.android.httpclient.Header;

/**
 * A very simple Processor which gets detected TextBlocks and adds them to the overlay
 * as OcrGraphics.
 */
public class OcrDetectorProcessor implements Detector.Processor<TextBlock> {

    private final OwnersListener ownersListener;
    private GraphicOverlay<GraphicOverlay.Graphic> mGraphicOverlay;

    OcrDetectorProcessor(GraphicOverlay<GraphicOverlay.Graphic> ocrGraphicOverlay, OwnersListener ownersListener) {
        mGraphicOverlay = ocrGraphicOverlay;
        this.ownersListener = ownersListener;
    }

    private void getOwnerData(String item) throws JSONException {
//        CatalogClient.get("/api/v1/cards-advanced?criteria=" + item, null, new JsonHttpResponseHandler() {
        try {
            CatalogClient.get("/ext/api/card-owners?cardName=" + URLEncoder.encode(item, "UTF-8"), null, new AsyncJsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    // If the response is JSONObject instead of expected JSONArray
                    Log.d("CCG", "It was an object! " + response);


                    List<Ownage> ownageList = new ArrayList<>();
//                    ArrayList<String> ownageList = new ArrayList<String>();
                    Iterator<String> keys = response.keys();
                    while(keys.hasNext()) {
                        String key = keys.next();
                        Log.d("CCG", "key " + key);
//                        ownageList.add(key);//card name
                        try {
                            JSONArray owners = (JSONArray)response.get(key);
                            for (int i = 0; i < owners.length(); i++) {
                                JSONObject owner = (JSONObject)owners.get(i);
                                ownageList.add(new Ownage(owner.getString("username"), owner.getInt("ownedCount"), owner.getString("blockName")));
                            }
                        } catch (JSONException e) {
                            Log.e("CCG", "ERROR: " + e.getMessage());
                        }
                    }


                    ownersListener.updateOwners(ownageList);
//                    TextGraphic textGraphic = new TextGraphic(mGraphicOverlay, ownageList);
//                    mGraphicOverlay.add(textGraphic);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray cards) {
                    try {
                        JSONObject first = (JSONObject) cards.get(0);
                        Log.d("CCG", "FIRST " + first);
                    } catch (JSONException e) {
                        Log.d("CCG", e.getMessage());
                    }
                }
            });
        } catch (UnsupportedEncodingException e) {
            Log.e("CCG", "ERROR " + e.getMessage());
        }
    }
    @Override
    public void receiveDetections(Detector.Detections<TextBlock> detections) {
        mGraphicOverlay.clear();
        SparseArray<TextBlock> items = detections.getDetectedItems();
        Log.d("Processor", "Detections: " + items.size());

        drawSingleTextWithRelatedData(items);
        Log.d("Processor", "----------------------------------------------");
    }

    private void drawSingleTextWithRelatedData(SparseArray<TextBlock> items) {
        if (items.size() < 1) {
            return;
        }
        TextBlock item = items.valueAt(0);
        // TODO Get blocks with single line
        // TODO Get first single line block -> name?
        // TODO Get second single line block -> type?
        if (item != null && item.getValue() != null && item.getValue().length() > 2) {
            Log.d("Processor", "Text detected! " + item.getValue());
            try {
                getOwnerData(item.getValue());
            } catch (JSONException e) {
                Log.e("CCG", "ERROR: " + e.getMessage());
            }
        }
        OcrGraphic graphic = new OcrGraphic(mGraphicOverlay, item);
        mGraphicOverlay.add(graphic);
    }

    private void drawFoundTexts(SparseArray<TextBlock> items) {
        for (int i = 0; i < items.size(); ++i) {
            TextBlock item = items.valueAt(i);
            if (item != null && item.getValue() != null) {
                Log.d("Processor", "Text detected! " + item.getValue());
            }
            OcrGraphic graphic = new OcrGraphic(mGraphicOverlay, item);
            mGraphicOverlay.add(graphic);
        }
    }

    @Override
    public void release() {
        mGraphicOverlay.clear();
    }
}
