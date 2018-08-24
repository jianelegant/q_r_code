package com.yy.adam.qrcode;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;

public class ResultHandler {

    public static void openBrowser(Activity activity, String url) {
        if (url.startsWith("HTTP://")) {
            url = "http" + url.substring(4);
        } else if (url.startsWith("HTTPS://")) {
            url = "https" + url.substring(5);
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        launchIntent(activity, intent);
    }

    private static void launchIntent(final Activity activity, Intent intent) {
        try {
            rawLaunchIntent(intent);
            activity.finish();
        } catch (ActivityNotFoundException ignored) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(R.string.app_name);
            builder.setMessage(R.string.msg_intent_failed);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if(null != activity) {
                        activity.finish();
                    }
                }
            });
            builder.setPositiveButton(R.string.button_ok, null);
            builder.show();
        }
    }

    private static void rawLaunchIntent(Intent intent) {
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            MainApp.s_GlobalCtx.startActivity(intent);
        }
    }
}
