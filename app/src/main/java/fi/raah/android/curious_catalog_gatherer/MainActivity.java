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

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;
import java.util.ArrayList;

import fi.raah.android.curious_catalog_gatherer.cards.CardService;
import fi.raah.android.curious_catalog_gatherer.http.CatalogClient;
import fi.raah.android.curious_catalog_gatherer.model.CardInfoAdapter;
import fi.raah.android.curious_catalog_gatherer.model.CardManagerAdapter;
import fi.raah.android.curious_catalog_gatherer.model.CardOwners;
import fi.raah.android.curious_catalog_gatherer.model.CardOwnersHistoryQueue;
import fi.raah.android.curious_catalog_gatherer.model.DomainAndToken;
import fi.raah.android.curious_catalog_gatherer.model.EditableCard;
import fi.raah.android.curious_catalog_gatherer.model.EditableCardCounts;
import fi.raah.android.curious_catalog_gatherer.model.HistoryListAdapter;
import fi.raah.android.curious_catalog_gatherer.model.Ownage;
import fi.raah.android.curious_catalog_gatherer.ui.CardInfoFragment;
import fi.raah.android.curious_catalog_gatherer.ui.CardManagerFragment;
import fi.raah.android.curious_catalog_gatherer.ui.HistoryFragment;
import fi.raah.android.curious_catalog_gatherer.ui.Icons;
import fi.raah.android.curious_catalog_gatherer.ui.SettingsFragment;
import fi.raah.android.curious_catalog_gatherer.ui.camera.CameraSource;
import fi.raah.android.curious_catalog_gatherer.ui.camera.CameraSourcePreview;
import fi.raah.android.curious_catalog_gatherer.ui.camera.GraphicOverlay;

/**
 * Activity for the Magic: The Gathering card detecting app.
 * This app detects MtG cards by their name. Card related ownage
 * data is retrieved from a catalog service. In manage view (CardManagerFragment)
 * user's own card ownages can be changed.
 *
 * During detection overlay graphics are drawn to indicate the position,
 * size, and contents of each recognized text block.
 */
public final class MainActivity extends AppCompatActivity implements ActivityCallback {
    private static final String TAG = "MainActivity";

    // Intent request code to handle updating play services if needed.
    private static final int RC_HANDLE_GMS = 9001;

    // Permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;

    // Constants used to pass extra data in the intent
    public static final String AutoFocus = "AutoFocus";
    public static final String UseFlash = "UseFlash";
    public static final String TextBlockObject = "String";

    private CameraSource mCameraSource;
    private CameraSourcePreview mPreview;
    private GraphicOverlay<GraphicOverlay.Graphic> mGraphicOverlay;

    // Helper objects for detecting taps and pinches.
    private GestureDetector gestureDetector;

    //TODO Dagger
    private Settings settings;
    private CatalogClient catalogClient;
    private CardService cardService;

    private CardInfoFragment cardInfoFragment;
    private CardInfoAdapter cardInfoAdapter;

    private HistoryFragment historyFragment;
    private HistoryListAdapter historyListAdapter;

    private SettingsFragment settingsFragment;

    private CardManagerFragment cardManagerFragment;
    private CardManagerAdapter cardManagerAdapter;

    private MenuItem cardInfoItem;
    private MenuItem historyItem;
    private MenuItem manageCardsItem;
    private MenuItem settingsItem;
    private Icons icons = new Icons();

    private DomainAndToken domainAndToken;
    // This value tries to prevent an unnecessary "Configuration required" popup,
    // but isn't doing a good job since the orientation changes when going to the scanner
    // and back causes creation of new MainActivities.
    private boolean returnFromQRScan = false;

    /**
     * Initializes the UI and creates the detector pipeline.
     */
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.main_activity);

        mPreview = (CameraSourcePreview) findViewById(R.id.preview);
        mGraphicOverlay = (GraphicOverlay<GraphicOverlay.Graphic>) findViewById(R.id.graphicOverlay);

        // Set good defaults for capturing text.
        boolean autoFocus = true;
        boolean useFlash = false;

        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource(autoFocus, useFlash);
        } else {
            requestCameraPermission();
        }

        gestureDetector = new GestureDetector(this, new CaptureGestureListener());

        Snackbar.make(mGraphicOverlay, "Tap to refocus.",
                Snackbar.LENGTH_LONG)
                .show();

        storeIntentDataIfExists();
    }

    private void ensureSettings() {
        if (!settings.isSettingsOk() && !isSettingsFragmentVisible()) {
            requestConfiguration();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        cardInfoItem = menu.findItem(R.id.action_card_info);
        historyItem = menu.findItem(R.id.action_card_history);
        manageCardsItem = menu.findItem(R.id.action_manage_cards);
        settingsItem = menu.findItem(R.id.action_manage_settings);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // This is called here to ensure menu has been created for the icon change to work.
        openSettingsIfIntentData();
        return super.onPrepareOptionsMenu(menu);
    }

    private void openSettingsIfIntentData() {
        if (domainAndToken != null) {
            showFragment(settingsFragment, settingsItem);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_card_info) {
            toggleFragment(cardInfoFragment, item);
        }

        if (id == R.id.action_card_history) {
            toggleFragment(historyFragment, item);
        }

        if (id == R.id.action_manage_settings) {
            toggleFragment(settingsFragment, item);
        }

        if (id == R.id.action_manage_cards) {
            toggleFragment(cardManagerFragment, item);
        }

        return super.onOptionsItemSelected(item);
    }

    protected void toggleFragment(Fragment fragment, MenuItem item) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        if (fragment.isAdded()) {
            if (fragment.isVisible()) {
                hideFragment(fragment, item, ft);
            } else {
                ft.show(fragment);
                item.setIcon(icons.on(item.getItemId()));
            }
        } else {
            ft.replace(R.id.fragment_container, fragment);
            ft.show(fragment);
            item.setIcon(icons.on(item.getItemId()));
        }

        hideOtherFragments(ft, fragment);

        ft.addToBackStack(null);//TODO this does not change the icons
        ft.commit();
    }

    protected void showFragment(Fragment fragment, MenuItem item) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        if (!fragment.isAdded()) {
            ft.replace(R.id.fragment_container, fragment);
        }
        ft.show(fragment);
        item.setIcon(icons.on(item.getItemId()));

        hideOtherFragments(ft, fragment);

        ft.commit();
    }

    private void hideFragment(Fragment fragment, MenuItem item, FragmentTransaction ft) {
        ft.hide(fragment);
        if (item != null) {
            item.setIcon(icons.off(item.getItemId()));
        }
    }

    private void hideOtherFragments(FragmentTransaction ft, Fragment fragment) {
        if (fragment != cardInfoFragment) {
            hideFragment(cardInfoFragment, cardInfoItem, ft);
        }
        if (fragment != historyFragment) {
            hideFragment(historyFragment, historyItem, ft);
        }
        if (fragment != cardManagerFragment) {
            hideFragment(cardManagerFragment, manageCardsItem, ft);
        }
        if (fragment != settingsFragment) {
            hideFragment(settingsFragment, settingsItem, ft);
        }
    }

    /**
     * Handles the requesting of the camera permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */
    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

        Snackbar.make(mGraphicOverlay, R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show();
    }

    private void requestConfiguration() {
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showFragment(settingsFragment, settingsItem);
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.settings_primer)
               .setTitle(R.string.settings_dialog_title)
               .setPositiveButton(R.string.take_me_there, listener)
               .create()
               .show();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        boolean c = gestureDetector.onTouchEvent(e);

        return c || super.onTouchEvent(e);
    }

    /**
     * Creates and starts the camera.  Note that this uses a higher resolution in comparison
     * to other detection examples to enable the ocr detector to detect small text samples
     * at long distances.
     *
     * Suppressing InlinedApi since there is a check that the minimum version is met before using
     * the constant.
     */
    @SuppressLint("InlinedApi")
    private void createCameraSource(boolean autoFocus, boolean useFlash) {
        Context context = getApplicationContext();

        // Create the TextRecognizer
        TextRecognizer textRecognizer = new TextRecognizer.Builder(context).build();
        // Set the TextRecognizer's Processor.
        textRecognizer.setProcessor(new OcrDetectorProcessor(mGraphicOverlay, this, getSettings(), getCardService()));
        // Check if the TextRecognizer is operational.
        if (!textRecognizer.isOperational()) {
            Log.w(TAG, "Detector dependencies are not yet available.");

            // Check for low storage.  If there is low storage, the native library will not be
            // downloaded, so detection will not become operational.
            IntentFilter lowStorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowStorageFilter) != null;

            if (hasLowStorage) {
                Toast.makeText(this, R.string.low_storage_error, Toast.LENGTH_LONG).show();
                Log.w(TAG, getString(R.string.low_storage_error));
            }
        }

        // Create the mCameraSource using the TextRecognizer.
        mCameraSource =
                new CameraSource.Builder(getApplicationContext(), textRecognizer)
                        .setFacing(CameraSource.CAMERA_FACING_BACK)
                        .setRequestedPreviewSize(1280, 1024)
                        .setRequestedFps(15.0f)
                        .setFlashMode(useFlash ? Camera.Parameters.FLASH_MODE_TORCH : null)
                        .setFocusMode(autoFocus ? Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO : null)
                        .build();
    }

    @Override
    protected void onStart() {
        settings = getSettings();
        catalogClient = getCatalogClient();
        cardService = getCardService();

        cardInfoFragment = new CardInfoFragment();
        cardInfoAdapter = new CardInfoAdapter(this, new ArrayList<Ownage>());
        cardInfoFragment.setListAdapter(cardInfoAdapter);

        historyFragment = new HistoryFragment();
        historyListAdapter = new HistoryListAdapter(this, new CardOwnersHistoryQueue(50));
        historyFragment.setAdapter(historyListAdapter);

        settingsFragment = new SettingsFragment();

        cardManagerFragment = new CardManagerFragment();
        cardManagerAdapter = new CardManagerAdapter(this, getCardService(), new ArrayList<EditableCard>());
        cardManagerFragment.setAdapter(cardManagerAdapter);

        super.onStart();
    }

    private void storeIntentDataIfExists() {
        Uri data = getIntent().getData();
        if (domainAndToken == null && (data != null && data.getHost() != null & data.getPath() != null)) {
            domainAndToken = new DomainAndToken(data.getHost(), data.getPath().substring(1));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startCameraSource();

        if (!returnFromQRScan && domainAndToken == null) {
            ensureSettings();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mPreview != null) {
            mPreview.stop();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        cardInfoFragment = null;
        historyFragment = null;
        settingsFragment = null;
        cardManagerFragment = null;
    }

    /**
     * Releases the resources associated with the camera source, the associated detectors, and the
     * rest of the processing pipeline.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPreview != null) {
            mPreview.release();
        }
    }

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on {@link #requestPermissions(String[], int)}.
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     * </p>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            // We have permission, so create the camerasource
            boolean autoFocus = getIntent().getBooleanExtra(AutoFocus,false);
            boolean useFlash = getIntent().getBooleanExtra(UseFlash, false);
            createCameraSource(autoFocus, useFlash);
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("CCG Camera Permission")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, listener)
                .show();
    }

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() throws SecurityException {
        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    private boolean onTap(float rawX, float rawY) {
        mCameraSource.autoFocus(null);
        return false;
    }

    public Settings getSettings() {
        if (settings == null) {
            settings = new Settings(getPreferences(Context.MODE_PRIVATE));
        }
        return settings;
    }

    public CatalogClient getCatalogClient() {
        if (catalogClient == null) {
            catalogClient = new CatalogClient(this, getSettings());
        }
        return catalogClient;
    }

    public CardService getCardService() {
        if (cardService == null) {
            cardService = new CardService(getAssets(), getSettings(), getCatalogClient());
        }
        return cardService;
    }

    public DomainAndToken getDomainAndToken() {
        return domainAndToken;
    }

    public void resetReturnFromQRScan() {
        returnFromQRScan = false;
    }

    private boolean isSettingsFragmentVisible() {
        FragmentManager fm = getSupportFragmentManager();
        for (Fragment fragment : fm.getFragments()) {
            if (fragment instanceof SettingsFragment) {
                return fragment.isVisible();
            }
        }

        return false;
    }

    private class CaptureGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return onTap(e.getRawX(), e.getRawY()) || super.onSingleTapConfirmed(e);
        }
    }

    @Override
    public void cardDataUpdate(final CardOwners cardOwners, final EditableCard editableCard, final boolean refresh) {
        runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    cardInfoAdapter.updateOwnageList(cardOwners);
                    cardInfoFragment.setCardName(cardOwners.getCardName());

                    historyListAdapter.push(cardOwners);

                    if (refresh) {
                        cardManagerAdapter.removeByName(editableCard.getName());
                    }
                    cardManagerAdapter.add(editableCard);
            }
        });
    }

    @Override
    public void makeShortToast(final String message) {
        makeToast(message, Toast.LENGTH_SHORT);
    }

    @Override
    public void makeLongToast(final String message) {
        makeToast(message, Toast.LENGTH_LONG);
    }

    private void makeToast(final String message, final int toastLength) {
        final Activity activity = this;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, message, toastLength).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        returnFromQRScan = true;
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public EditableCardCounts getEditableCardCounts() {
        return cardManagerAdapter.getEditableCardCounts();
    }
}
