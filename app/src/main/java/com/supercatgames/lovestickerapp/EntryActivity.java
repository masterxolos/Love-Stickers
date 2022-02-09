package com.supercatgames.lovestickerapp;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.adcolony.sdk.AdColony;
import com.adcolony.sdk.AdColonyAdSize;
import com.adcolony.sdk.AdColonyAdView;
import com.adcolony.sdk.AdColonyAdViewListener;
import com.adcolony.sdk.AdColonyAppOptions;
import com.adcolony.sdk.AdColonyInterstitial;
import com.adcolony.sdk.AdColonyInterstitialListener;
import com.facebook.appevents.AppEventsConstants;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class EntryActivity extends BaseActivity {
    private View progressBar;
    private LoadListAsyncTask loadListAsyncTask;

    private String ZoneID = "vz5f4d9386b26d46c493";
    private String BannerZoneID = "vz39fe89d101e241a090";
    private String AppID = "appc7961e952b744d8cbd";

    private FirebaseAnalytics mFirebaseAnalytics;

    public void ShowInterstitialAd(){
       //todo show ads
    }

    public void loadInterstitialAd(){
        AdColony.configure(this, AppID, ZoneID);
        AdColonyAppOptions appOptions = new AdColonyAppOptions()
                .setKeepScreenOn(true);
        AdColonyInterstitialListener listener = new AdColonyInterstitialListener() {
            @Override
            public void onRequestFilled(AdColonyInterstitial ad) {
                ad.show();
                /** Store and use this ad object to show your ad when appropriate */
            }
        };

        AdColony.requestInterstitial(ZoneID, listener);
    }

    public void loadBannerAd(){
        AdColonyAdViewListener listener = new AdColonyAdViewListener() {
            @Override
            public void onRequestFilled(AdColonyAdView ad) {
                /** Add this ad object to whatever layout you have set up for this placement */
            }
        };

        AdColony.requestAdView(BannerZoneID, listener, AdColonyAdSize.BANNER);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        //setContentView(R.layout.activity_entry);

        overridePendingTransition(0, 0);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        progressBar = findViewById(R.id.entry_activity_progress);
        loadListAsyncTask = new LoadListAsyncTask(this);
        loadListAsyncTask.execute();
        loadInterstitialAd();
        loadBannerAd();

        AppEventsLogger logger = AppEventsLogger.newLogger(this);
        Bundle params = new Bundle();
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, "opened");
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, "opened");
        logger.logEvent(AppEventsConstants.EVENT_NAME_VIEWED_CONTENT, 34.99, params);
    }

    // Implement a function to load a rewarded ad. The ad will start to show once the ad has been loaded.


    private void showStickerPack(ArrayList<StickerPack> stickerPackList) {
        progressBar.setVisibility(View.GONE);
        if (stickerPackList.size() > 1) {
            final Intent intent = new Intent(this, StickerPackListActivity.class);
            intent.putParcelableArrayListExtra(StickerPackListActivity.EXTRA_STICKER_PACK_LIST_DATA, stickerPackList);
            startActivity(intent);
            finish();
            overridePendingTransition(0, 0);
        } else {
            final Intent intent = new Intent(this, StickerPackDetailsActivity.class);
            intent.putExtra(StickerPackDetailsActivity.EXTRA_SHOW_UP_BUTTON, false);
            intent.putExtra(StickerPackDetailsActivity.EXTRA_STICKER_PACK_DATA, stickerPackList.get(0));
            startActivity(intent);
            finish();
            overridePendingTransition(0, 0);
        }
    }

    private void showErrorMessage(String errorMessage) {
        progressBar.setVisibility(View.GONE);
        Log.e("EntryActivity", "error fetching sticker packs, " + errorMessage);
        final TextView errorMessageTV = findViewById(R.id.error_message);
        errorMessageTV.setText(getString(R.string.error_message, errorMessage));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loadListAsyncTask != null && !loadListAsyncTask.isCancelled()) {
            loadListAsyncTask.cancel(true);
        }
    }

    class LoadListAsyncTask extends AsyncTask<Void, Void, Pair<String, ArrayList<StickerPack>>> {
        private final WeakReference<EntryActivity> contextWeakReference;

        LoadListAsyncTask(EntryActivity activity) {
            this.contextWeakReference = new WeakReference<>(activity);
        }

        @Override
        protected Pair<String, ArrayList<StickerPack>> doInBackground(Void... voids) {
            ArrayList<StickerPack> stickerPackList;
            try {
                final Context context = contextWeakReference.get();
                if (context != null) {
                    stickerPackList = StickerPackLoader.fetchStickerPacks(context);
                    if (stickerPackList.size() == 0) {
                        return new Pair<>("could not find any packs", null);
                    }
                    for (StickerPack stickerPack : stickerPackList) {
                        StickerPackValidator.verifyStickerPackValidity(context, stickerPack);
                    }
                    return new Pair<>(null, stickerPackList);
                } else {
                    return new Pair<>("could not fetch sticker packs", null);
                }
            } catch (Exception e) {
                Log.e("EntryActivity", "error fetching sticker packs", e);
                return new Pair<>(e.getMessage(), null);
            }
        }

        @Override
        protected void onPostExecute(Pair<String, ArrayList<StickerPack>> stringListPair) {

            final EntryActivity entryActivity = contextWeakReference.get();
            if (entryActivity != null) {
                if (stringListPair.first != null) {
                    entryActivity.showErrorMessage(stringListPair.first);
                } else {
                    entryActivity.showStickerPack(stringListPair.second);
                }
            }
        }
    }
}
