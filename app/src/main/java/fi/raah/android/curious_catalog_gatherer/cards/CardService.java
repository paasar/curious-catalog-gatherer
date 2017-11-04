package fi.raah.android.curious_catalog_gatherer.cards;

import android.content.res.AssetManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import cz.msebera.android.httpclient.Header;
import fi.raah.android.curious_catalog_gatherer.ActivityCallback;
import fi.raah.android.curious_catalog_gatherer.http.AsyncJsonHttpResponseHandler;
import fi.raah.android.curious_catalog_gatherer.http.CatalogClient;
import fi.raah.android.curious_catalog_gatherer.model.CardOwners;
import fi.raah.android.curious_catalog_gatherer.model.Ownage;

public class CardService {
    private HashMap<String, Set<String>> blockToCardsMap = new HashMap<>();

    private final static ConcurrentHashMap<String, CardOwners> CARD_NAME_TO_OWNAGE_CACHE = new ConcurrentHashMap<>();
    private final CatalogClient catalogClient;

    public CardService(AssetManager assetManager, CatalogClient catalogClient) {
        initializeBlockCards(assetManager);
        this.catalogClient = catalogClient;
    }

    private void initializeBlockCards(AssetManager assetManager) {
        try {
            String[] assets = assetManager.list("cards");
            for (String asset : assets) {
                blockToCardsMap.put(asset, cardNames(assetManager, asset));
            }
        } catch (IOException e) {
            //TODO
            e.printStackTrace();
        }
    }

    private Set<String> cardNames(AssetManager assetManager, String fileName) throws IOException {
        InputStream inputStream = assetManager.open("cards/" + fileName);
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
        String line = null;
        HashSet<String> cardNames = new HashSet<>();
        while((line = in.readLine()) != null) {
            cardNames.add(line);
        }

        //TODO mites nää hanskataan?
        in.close();
        inputStream.close();

        return cardNames;
    }

    public boolean isCardName(String text) {
        return text != null && text.length() > 2 && isInAllCards(text);
    }

    private boolean isInAllCards(String text) {
        Collection<Set<String>> cardNameSets = blockToCardsMap.values();
        for (Set<String> cardNames : cardNameSets) {
            if (cardNames.contains(text)) {
                return true;
            }
        }
        return false;
    }

    public void fetchAndUpdateOwnerData(final ActivityCallback activityCallback, final String cardName) {
        if (CARD_NAME_TO_OWNAGE_CACHE.containsKey(cardName)) {
            activityCallback.updateOwners(CARD_NAME_TO_OWNAGE_CACHE.get(cardName));
        } else {
            fetchFromCatalog(activityCallback, cardName);
        }
    }

    private void fetchFromCatalog(final ActivityCallback activityCallback, final String cardName) {
            catalogClient.getCardOwners(cardName, null, new AsyncJsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    // If the response is JSONObject instead of expected JSONArray
                    Log.d("CCG", "It was an object! " + response);
                    throw new UnsupportedOperationException("Method not implemented.");
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray array) {
                    String cardNameResult = cardName;
                    List<Ownage> ownageList = new ArrayList<>();

                    for(int i = 0, size = array.length(); i < size; i++) {
                        try {
                            JSONObject cardInfo = array.getJSONObject(i);

                            cardNameResult = cardInfo.getString("cardName");

                            JSONArray owners = (JSONArray) cardInfo.get("owners");
                            for (int j = 0, ownersSize = owners.length(); j < ownersSize; j++) {
                                JSONObject owner = (JSONObject) owners.get(j);
                                ownageList.add(new Ownage(owner.getString("username"), owner.getInt("ownedCount"), owner.getString("blockName")));
                            }
                        } catch (JSONException e) {
                            Log.e("CCG", "ERROR: " + e.getMessage());
                        }
                    }

                    CardOwners cardOwners = new CardOwners(cardNameResult, ownageList);
                    CARD_NAME_TO_OWNAGE_CACHE.put(cardName, cardOwners);
                    activityCallback.updateOwners(cardOwners);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.e("CCG", "Failed to get card info " + cardName + " status: " + statusCode + " response: " + responseString);
                    super.onFailure(statusCode, headers, responseString, throwable);
                    activityCallback.makeToast("Failed to fetch card info " + cardName);
                }
            });
    }
}
