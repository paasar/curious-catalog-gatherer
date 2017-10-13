package fi.raah.android.curious_catalog_gatherer.cards;

import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class CardService  extends AppCompatActivity {
    private HashMap<String, Set<String>> allCards = new HashMap<>();
    private HashMap<String, Set<String>> cardNameToBlocks = new HashMap<>();

    public CardService() {
        initializeBlockCards();
    }

    private void initializeBlockCards() {
        AssetManager assetManager = getAssets();
        try {
            String[] assets = assetManager.list("cards");
            Log.d("CCG", "Assets: " + assets[0]);

            for (String asset : assets) {
                allCards.put(asset, cardNames(assetManager, asset));
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
            //for (String cardName : cardNames) {
            //    Log.d("CCG", "CARD NAME: " + cardName);
            //}
        }

        //TODO mites nää hanskataan?
        in.close();
        inputStream.close();

        return cardNames;
    }
}
