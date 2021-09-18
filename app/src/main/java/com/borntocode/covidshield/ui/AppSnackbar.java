package com.borntocode.covidshield.ui;

import android.content.Context;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.borntocode.covidshield.R;
import com.google.android.material.snackbar.Snackbar;

public class AppSnackbar {
    public static void SnackbarOK(View view, Context context, String message) {
        Snackbar snackbar = Snackbar
                .make(view, message, Snackbar.LENGTH_SHORT).setActionTextColor(ContextCompat.getColor(context, R.color.colorPrimaryVariant))
                .setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
        snackbar.show();
    }

    public static void SnackbarINFO(View view, Context context, String message) {
        Snackbar snackbar = Snackbar
                .make(view, message, Snackbar.LENGTH_SHORT).setActionTextColor(ContextCompat.getColor(context, R.color.colorPrimaryVariant));
        snackbar.show();
    }
}
