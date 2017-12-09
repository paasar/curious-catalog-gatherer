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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import cz.msebera.android.httpclient.Header;
import fi.raah.android.curious_catalog_gatherer.ActivityCallback;
import fi.raah.android.curious_catalog_gatherer.Settings;
import fi.raah.android.curious_catalog_gatherer.http.AsyncJsonHttpResponseHandler;
import fi.raah.android.curious_catalog_gatherer.http.CatalogClient;
import fi.raah.android.curious_catalog_gatherer.model.BlockCodeToAmount;
import fi.raah.android.curious_catalog_gatherer.model.CardOwners;
import fi.raah.android.curious_catalog_gatherer.model.EditableCard;
import fi.raah.android.curious_catalog_gatherer.model.Ownage;

public class CardService {
    //0123__NNN__Blaa -> Set<Mountain,Swamp,...>
    private Set<String> cardNames = new HashSet<>();

    //NNN_Mountain -> 123456
    private HashMap<String, String> blockAndCardNameToMultiverseId = new HashMap<>();

    //Mountain -> Set<NNN,MMM,...>
    private HashMap<String, List<String>> cardNameToBlockCodes = new HashMap<>();

    private final Settings settings;
    private final CatalogClient catalogClient;

    private final static ConcurrentHashMap<String, CardOwners> CARD_NAME_TO_OWNAGE_CACHE = new ConcurrentHashMap<>();

    public CardService(AssetManager assetManager, Settings settings, CatalogClient catalogClient) {
        initializeBlockCards(assetManager);
        this.settings = settings;
        this.catalogClient = catalogClient;
    }

    private void initializeBlockCards(AssetManager assetManager) {
        try {
            String[] assets = assetManager.list("cards");
            for (String asset : assets) {
                Set<Card> cards = cards(assetManager, asset);
                cardNames.addAll(cardsToNames(cards));

                String blockCode = parseBlockCode(asset);
                addToMultiverseIdMap(blockCode, cards);
                addToCardNameToBlockCodesMap(blockCode, cards);
            }
        } catch (IOException e) {
            //TODO
            e.printStackTrace();
        }
    }

    private void addToCardNameToBlockCodesMap(String blockCode, Set<Card> cards) {
        for (Card card : cards) {
            List<String> blockCodes = getBlockCodesForCardName(card.getName());
            blockCodes.add(0, blockCode);//Add to front since we want to have newest first
            cardNameToBlockCodes.put(card.getName(), blockCodes);
        }
    }

    private List<String> getBlockCodesForCardName(String cardName) {
        if (cardNameToBlockCodes.containsKey(cardName)) {
            return cardNameToBlockCodes.get(cardName);
        } else {
            return new ArrayList<String>();
        }
    }

    private void addToMultiverseIdMap(String blockCode, Set<Card> cards) {
        for (Card card : cards) {
            blockAndCardNameToMultiverseId.put(
                    blockCodeAndCardNameKey(blockCode, card.getName()), card.getMultiverseId());
        }
    }

    private String blockCodeAndCardNameKey(String blockCode, String cardName) {
        return blockCode + "_" + cardName;
    }

    private String parseBlockCode(String setFileName) {
        String[] fileNameParts = setFileName.split("__");
        return fileNameParts[1];
    }

    private Set<Card> cards(AssetManager assetManager, String setFileName) throws IOException {
        InputStream inputStream = assetManager.open("cards/" + setFileName);
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        HashSet<Card> cards = new HashSet<>();
        while((line = in.readLine()) != null) {
            String[] cardNameAndMultiverseId = line.split(";");
            cards.add(new Card(cardNameAndMultiverseId[0], cardNameAndMultiverseId[1]));
        }

        //TODO What should we do here?
        in.close();
        inputStream.close();

        return cards;
    }

    private Set<String> cardsToNames(Set<Card> cards) {
        Set<String> cardNames = new HashSet<>();
        for (Card card : cards) {
            cardNames.add(card.getName());
        }
        return cardNames;
    }

    public boolean isCardName(String text) {
        return text != null && text.length() > 2 && isInAllCards(text);
    }

    private boolean isInAllCards(String text) {
        return cardNames.contains(text);
    }

    public void fetchAndUpdateOwnerData(final ActivityCallback activityCallback, final String cardName) {
        fetchAndUpdateOwnerData(activityCallback, cardName, false);
    }

    private void fetchAndUpdateOwnerData(final ActivityCallback activityCallback, final String cardName, boolean refresh) {
        if (refresh) {
            CARD_NAME_TO_OWNAGE_CACHE.remove(cardName);
        }

        if (CARD_NAME_TO_OWNAGE_CACHE.containsKey(cardName)) {
            CardOwners cardOwners = CARD_NAME_TO_OWNAGE_CACHE.get(cardName);
            activityCallback.cardDataUpdate(cardOwners, createEditableCard(cardOwners), refresh);
        } else {
            fetchFromCatalog(activityCallback, cardName, refresh);
        }
    }

    private String getMultiverseId(String blockCode, String cardName) {
        String multiverseid = blockAndCardNameToMultiverseId.get(blockCodeAndCardNameKey(blockCode, cardName));
        if (multiverseid != null) {
            return multiverseid;
        } else {
            throw new IllegalStateException("Could not get multiverseid for blockCode " +
                    blockCode + " and card name " + cardName);
        }
    }

    private void fetchFromCatalog(final ActivityCallback activityCallback, final String cardName, final boolean refresh) {
            catalogClient.getCardOwners(cardName, null, new AsyncJsonHttpResponseHandler() {
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
                                ownageList.add(new Ownage(owner.getString("username"),
                                                          owner.getInt("ownedCount"),
                                                          owner.getString("blockName"),
                                                          owner.getString("blockCode")));
                            }
                        } catch (JSONException e) {
                            Log.e("CCG", "JSON parse error: " + e.getMessage());
                        }
                    }

                    CardOwners cardOwners = new CardOwners(cardNameResult, ownageList);
                    CARD_NAME_TO_OWNAGE_CACHE.put(cardName, cardOwners);
                    activityCallback.cardDataUpdate(cardOwners, createEditableCard(cardOwners), refresh);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.e("CCG", "Failed to get card info " + cardName + " status: " + statusCode + " response: " + responseString);
                    super.onFailure(statusCode, headers, responseString, throwable);
                    activityCallback.makeToast("Failed to fetch card info " + cardName);
                }
            });
    }

    private EditableCard createEditableCard(CardOwners cardOwners) {
        return new EditableCard(cardOwners.getCardName(),
                createBlockCodeToAmountList(getBlockCodesForCardName(cardOwners.getCardName()), cardOwners));
    }

    private List<BlockCodeToAmount> createBlockCodeToAmountList(List<String> blockCodesForCardName, CardOwners cardOwners) {
        List<BlockCodeToAmount> result = new ArrayList<>();
        List<Ownage> ownageList = cardOwners.getOwnageList();

        for (String blockCode : blockCodesForCardName) {
            result.add(findOwnedAmount(blockCode, ownageList));
        }

        return result;
    }

    private BlockCodeToAmount findOwnedAmount(String blockCode, List<Ownage> ownageList) {
        for (Ownage ownage : ownageList) {
            if (ownage.getOwner().equals(settings.getUsername()) &&
                    blockCode.equals(ownage.getBlockCode())) {
                return new BlockCodeToAmount(blockCode, ownage.getAmount());
            }
        }

        return new BlockCodeToAmount(blockCode, 0);
    }

    public void sendUpdatedOwnagesToCatalog(final ActivityCallback activityCallback, List<EditableCard> editableCards) {
        for (final EditableCard editableCard : editableCards) {
            BlockCodeToAmount blockAndAmount = editableCard.getSelectedBlockAndAmount();
            catalogClient.updateCard(getMultiverseId(blockAndAmount.getBlockCode(),
                                                     editableCard.getName()),
                                     blockAndAmount.getAmount() + editableCard.getDifference(),
                    new AsyncJsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            fetchAndUpdateOwnerData(activityCallback, editableCard.getName(), true);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            Log.e("CCG", "Failed to update. Response: " + responseString);
                            activityCallback.makeToast("Failed to update card info " + editableCard.getName());
                        }
                    });
        }
    }
}
