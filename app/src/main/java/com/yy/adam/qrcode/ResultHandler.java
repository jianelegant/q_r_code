package com.yy.adam.qrcode;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;

import java.util.ArrayList;
import java.util.Locale;

public class ResultHandler {

    private static final String[] EMAIL_TYPE_STRINGS = {"home", "work", "mobile"};
    private static final String[] PHONE_TYPE_STRINGS = {"home", "work", "mobile", "fax", "pager", "main"};
    private static final String[] ADDRESS_TYPE_STRINGS = {"home", "work"};
    private static final int[] EMAIL_TYPE_VALUES = {
            ContactsContract.CommonDataKinds.Email.TYPE_HOME,
            ContactsContract.CommonDataKinds.Email.TYPE_WORK,
            ContactsContract.CommonDataKinds.Email.TYPE_MOBILE,
    };
    private static final int[] PHONE_TYPE_VALUES = {
            ContactsContract.CommonDataKinds.Phone.TYPE_HOME,
            ContactsContract.CommonDataKinds.Phone.TYPE_WORK,
            ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,
            ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK,
            ContactsContract.CommonDataKinds.Phone.TYPE_PAGER,
            ContactsContract.CommonDataKinds.Phone.TYPE_MAIN,
    };
    private static final int[] ADDRESS_TYPE_VALUES = {
            ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME,
            ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK,
    };
    private static final int NO_TYPE = -1;

    /**
     * addContact
     */
    public static void addContact(Activity activity, String[] names,
                          String[] nicknames,
                          String pronunciation,
                          String[] phoneNumbers,
                          String[] phoneTypes,
                          String[] emails,
                          String[] emailTypes,
                          String note,
                          String instantMessenger,
                          String address,
                          String addressType,
                          String org,
                          String title,
                          String[] urls,
                          String birthday,
                          String[] geo) {

        // Only use the first name in the array, if present.
        Intent intent = new Intent(Intent.ACTION_INSERT_OR_EDIT, ContactsContract.Contacts.CONTENT_URI);
        intent.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);
        putExtra(intent, ContactsContract.Intents.Insert.NAME, names != null && names.length > 0 ? names[0] : null);

        putExtra(intent, ContactsContract.Intents.Insert.PHONETIC_NAME, pronunciation);

        if (phoneNumbers != null) {
            int phoneCount = Math.min(phoneNumbers.length, Contents.PHONE_KEYS.length);
            for (int x = 0; x < phoneCount; x++) {
                putExtra(intent, Contents.PHONE_KEYS[x], phoneNumbers[x]);
                if (phoneTypes != null && x < phoneTypes.length) {
                    int type = toPhoneContractType(phoneTypes[x]);
                    if (type >= 0) {
                        intent.putExtra(Contents.PHONE_TYPE_KEYS[x], type);
                    }
                }
            }
        }

        if (emails != null) {
            int emailCount = Math.min(emails.length, Contents.EMAIL_KEYS.length);
            for (int x = 0; x < emailCount; x++) {
                putExtra(intent, Contents.EMAIL_KEYS[x], emails[x]);
                if (emailTypes != null && x < emailTypes.length) {
                    int type = toEmailContractType(emailTypes[x]);
                    if (type >= 0) {
                        intent.putExtra(Contents.EMAIL_TYPE_KEYS[x], type);
                    }
                }
            }
        }

        ArrayList<ContentValues> data = new ArrayList<>();
        if (urls != null) {
            for (String url : urls) {
                if (url != null && !url.isEmpty()) {
                    ContentValues row = new ContentValues(2);
                    row.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE);
                    row.put(ContactsContract.CommonDataKinds.Website.URL, url);
                    data.add(row);
                    break;
                }
            }
        }

        if (birthday != null) {
            ContentValues row = new ContentValues(3);
            row.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE);
            row.put(ContactsContract.CommonDataKinds.Event.TYPE, ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY);
            row.put(ContactsContract.CommonDataKinds.Event.START_DATE, birthday);
            data.add(row);
        }

        if (nicknames != null) {
            for (String nickname : nicknames) {
                if (nickname != null && !nickname.isEmpty()) {
                    ContentValues row = new ContentValues(3);
                    row.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE);
                    row.put(ContactsContract.CommonDataKinds.Nickname.TYPE,
                            ContactsContract.CommonDataKinds.Nickname.TYPE_DEFAULT);
                    row.put(ContactsContract.CommonDataKinds.Nickname.NAME, nickname);
                    data.add(row);
                    break;
                }
            }
        }

        if (!data.isEmpty()) {
            intent.putParcelableArrayListExtra(ContactsContract.Intents.Insert.DATA, data);
        }

        StringBuilder aggregatedNotes = new StringBuilder();
        if (note != null) {
            aggregatedNotes.append('\n').append(note);
        }
        if (geo != null && geo.length >= 2) {
            aggregatedNotes.append('\n').append(geo[0]).append(',').append(geo[1]);
        }

        if (aggregatedNotes.length() > 0) {
            // Remove extra leading '\n'
            putExtra(intent, ContactsContract.Intents.Insert.NOTES, aggregatedNotes.substring(1));
        }

        if (instantMessenger != null && instantMessenger.startsWith("xmpp:")) {
            intent.putExtra(ContactsContract.Intents.Insert.IM_PROTOCOL, ContactsContract.CommonDataKinds.Im.PROTOCOL_JABBER);
            intent.putExtra(ContactsContract.Intents.Insert.IM_HANDLE, instantMessenger.substring(5));
        } else {
            putExtra(intent, ContactsContract.Intents.Insert.IM_HANDLE, instantMessenger);
        }

        putExtra(intent, ContactsContract.Intents.Insert.POSTAL, address);
        if (addressType != null) {
            int type = toAddressContractType(addressType);
            if (type >= 0) {
                intent.putExtra(ContactsContract.Intents.Insert.POSTAL_TYPE, type);
            }
        }
        putExtra(intent, ContactsContract.Intents.Insert.COMPANY, org);
        putExtra(intent, ContactsContract.Intents.Insert.JOB_TITLE, title);
        launchIntent(activity, intent);
    }

    /**
     * dialPhone
     */
    public static void dialPhone(Activity activity, String phoneNumber) {
        launchIntent(activity, new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber)));
    }

    /**
     * dialPhoneFromUri
     */
    public static void dialPhoneFromUri(Activity activity, String uri) {
        launchIntent(activity, new Intent(Intent.ACTION_DIAL, Uri.parse(uri)));
    }

    /**
     * openBrowser
     */
    public static void openBrowser(Activity activity, String url) {
        if (url.startsWith("HTTP://")) {
            url = "http" + url.substring(4);
        } else if (url.startsWith("HTTPS://")) {
            url = "https" + url.substring(5);
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        launchIntent(activity, intent);
    }

    /**
     * sendEmail
     */
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

    /**
     * sendSMS
     */
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

    private static int toEmailContractType(String typeString) {
        return doToContractType(typeString, EMAIL_TYPE_STRINGS, EMAIL_TYPE_VALUES);
    }

    private static int toPhoneContractType(String typeString) {
        return doToContractType(typeString, PHONE_TYPE_STRINGS, PHONE_TYPE_VALUES);
    }

    private static int toAddressContractType(String typeString) {
        return doToContractType(typeString, ADDRESS_TYPE_STRINGS, ADDRESS_TYPE_VALUES);
    }

    private static int doToContractType(String typeString, String[] types, int[] values) {
        if (typeString == null) {
            return NO_TYPE;
        }
        for (int i = 0; i < types.length; i++) {
            String type = types[i];
            if (typeString.startsWith(type) || typeString.startsWith(type.toUpperCase(Locale.ENGLISH))) {
                return values[i];
            }
        }
        return NO_TYPE;
    }
}
