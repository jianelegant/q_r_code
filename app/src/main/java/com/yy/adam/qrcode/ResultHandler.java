package com.yy.adam.qrcode;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;

public class ResultHandler {

    public static void dialPhone(Activity activity, String phoneNumber) {
        launchIntent(activity, new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber)));
    }

    public static void dialPhoneFromUri(Activity activity, String uri) {
        launchIntent(activity, new Intent(Intent.ACTION_DIAL, Uri.parse(uri)));
    }

    public static void openBrowser(Activity activity, String url) {
        if (url.startsWith("HTTP://")) {
            url = "http" + url.substring(4);
        } else if (url.startsWith("HTTPS://")) {
            url = "https" + url.substring(5);
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        launchIntent(activity, intent);
    }

    public static void sendEmail(Activity activity, String[] to,
                         String[] cc,
                         String[] bcc,
                         String subject,
                         String body) {
        Intent intent = new Intent(Intent.ACTION_SEND, Uri.parse("mailto:"));
        if (to != null && to.length != 0) {
            intent.putExtra(Intent.EXTRA_EMAIL, to);
        }
        if (cc != null && cc.length != 0) {
            intent.putExtra(Intent.EXTRA_CC, cc);
        }
        if (bcc != null && bcc.length != 0) {
            intent.putExtra(Intent.EXTRA_BCC, bcc);
        }
        putExtra(intent, Intent.EXTRA_SUBJECT, subject);
        putExtra(intent, Intent.EXTRA_TEXT, body);
        intent.setType("text/plain");
        launchIntent(activity, intent);
    }

    public static void sendSMS(Activity activity, String phoneNumber, String body) {
        sendSMSFromUri(activity, "smsto:" + phoneNumber, body);
    }

    private static void sendSMSFromUri(Activity activity, String uri, String body) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(uri));
        putExtra(intent, "sms_body", body);
        // Exit the app once the SMS is sent
        intent.putExtra("compose_mode", true);
        launchIntent(activity, intent);
    }

    private static void putExtra(Intent intent, String key, String value) {
        if (value != null && !value.isEmpty()) {
            intent.putExtra(key, value);
        }
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
