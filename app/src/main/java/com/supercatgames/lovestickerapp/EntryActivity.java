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
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
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


    public InterstitialAd mInterstitialAd;
    private static final String TAG = "EntryActivity AdMob";

    public void ShowInterstitialAd(){
        if (mInterstitialAd != null) {
            mInterstitialAd.show(EntryActivity.this);
            Log.d(TAG, "The Interstitial ad has displayed.");
            loadInterstitialAd();
        } else {
            Log.d(TAG, "The interstitial ad wasn't ready yet.");
        }
    }

    public void loadInterstitialAd(){
        AdRequest adRequest = new AdRequest.Builder().build();


        InterstitialAd.load(this,"ca-app-pub-3940256099942544/5224354917", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.d(TAG, "onInterstitialAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.d(TAG, loadAdError.getMessage());
                        mInterstitialAd = null;
                    }
                });
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        //setContentView(R.layout.activity_entry);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });
        loadInterstitialAd();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(mInterstitialAd != null)
                    ShowInterstitialAd();
                else{
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            ShowInterstitialAd();
                        }
                    }, 5000);
                }
            }
        }, 4000);

        overridePendingTransition(0, 0);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        progressBar = findViewById(R.id.entry_activity_progress);
        loadListAsyncTask = new LoadListAsyncTask(this);
        loadListAsyncTask.execute();

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
