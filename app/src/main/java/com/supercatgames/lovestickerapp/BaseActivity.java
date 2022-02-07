/*
 * Copyright (c) WhatsApp Inc. and its affiliates.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.supercatgames.lovestickerapp;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;


public class BaseActivity extends AppCompatActivity {


    public void loadRewardedAd(){
        //todo load ads
    }

    public void ShowRewardedAd(){
        //todo show ads
    }




    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);
        PendingIntent pendingIntent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity
                    (this, 0, getIntent(), PendingIntent.FLAG_MUTABLE);
        }
        else
            pendingIntent = PendingIntent.getActivity
                    (this, 0, getIntent(), PendingIntent.FLAG_IMMUTABLE);
        //todo sanırım main screen ads
        /*
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        loadRewardedAd();
    */

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public static final class MessageDialogFragment extends DialogFragment {
        private static final String ARG_TITLE_ID = "title_id";
        private static final String ARG_MESSAGE = "message";

        static DialogFragment newInstance(@StringRes int titleId, String message) {
            DialogFragment fragment = new MessageDialogFragment();
            Bundle arguments = new Bundle();
            arguments.putInt(ARG_TITLE_ID, titleId);
            arguments.putString(ARG_MESSAGE, message);
            fragment.setArguments(arguments);
            return fragment;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            @StringRes final int title = getArguments().getInt(ARG_TITLE_ID);
            String message = getArguments().getString(ARG_MESSAGE);

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity())
                    .setMessage(message)
                    .setCancelable(true)
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> dismiss());

            if (title != 0) {
                dialogBuilder.setTitle(title);
            }
            return dialogBuilder.create();
        }
    }
}
