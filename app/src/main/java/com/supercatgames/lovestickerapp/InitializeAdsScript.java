package com.supercatgames.lovestickerapp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.unity3d.ads.IUnityAdsInitializationListener;
import com.unity3d.ads.IUnityAdsLoadListener;
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.UnityAdsShowOptions;

public class InitializeAdsScript extends AppCompatActivity implements IUnityAdsInitializationListener {

    private static final String unityGameID = "4571537";
    private static final Boolean testMode = true;
    private static final String adUnitId = "Rewarded_Android";

    public static void initializeAd(Context context){
        final IUnityAdsInitializationListener myAdListener = new IUnityAdsInitializationListener();
        UnityAds.initialize(context, unityGameID, testMode);
    }

    @Override
    public void onInitializationComplete() {

    }

    @Override
    public void onInitializationFailed(UnityAds.UnityAdsInitializationError unityAdsInitializationError, String s) {

    }

    private static class IUnityAdsInitializationListener implements com.unity3d.ads.IUnityAdsInitializationListener{

        @Override
        public void onInitializationComplete() {

        }

        @Override
        public void onInitializationFailed(UnityAds.UnityAdsInitializationError unityAdsInitializationError, String s) {

        }
    }




    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_entry);
        // Initialize the SDK:
        UnityAds.initialize(getApplicationContext(), unityGameID, testMode, this);
    }


    public static void showRewardedAd(Activity activity)
    {
        UnityAds.load(adUnitId);
        UnityAds.show(activity, adUnitId);
    }

}