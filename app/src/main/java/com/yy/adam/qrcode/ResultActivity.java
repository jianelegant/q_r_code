package com.yy.adam.qrcode;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.zxing.client.result.AddressBookParsedResult;
import com.google.zxing.client.result.EmailAddressParsedResult;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.SMSParsedResult;
import com.google.zxing.client.result.TelParsedResult;
import com.google.zxing.client.result.WifiParsedResult;
import com.yy.adam.qrcode.wifi.WifiConfigManager;

public class ResultActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;

    TextView mType;
    TextView mDisplayResult;
    TextView mCopy;

    public static void callStart() {
        Intent intent = new Intent(MainApp.s_GlobalCtx, ResultActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MainApp.s_GlobalCtx.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_result);
        loadAd();
        initViews();
    }

    private void loadAd() {
        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    private void initViews() {
        mType = findViewById(R.id.id_type);
        mDisplayResult = findViewById(R.id.id_display_result);
        mCopy = findViewById(R.id.id_copy);
        mCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyToFlipboard();
            }
        });
    }

    private void copyToFlipboard() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(getString(R.string.app_name), ResultUtil.s_ParsedResult.getDisplayResult());
        clipboard.setPrimaryClip(clip);
        Util.toast("Copy to Flipboard succeed");
    }

    @Override
    protected void onResume() {
        super.onResume();
        handleResult();
    }

    private void handleResult() {
        mCopy.setVisibility(View.GONE);
        ParsedResult parsedResult = ResultUtil.s_ParsedResult;
        if(null != parsedResult) {
            switch (parsedResult.getType()) {
                case URI:
                    ResultHandler.openBrowser(this, parsedResult.getDisplayResult());
                    break;
                case TEL:
                    TelParsedResult telResult = (TelParsedResult) parsedResult;
                    if(!TextUtils.isEmpty(telResult.getTelURI())) {
                        ResultHandler.dialPhoneFromUri(this, telResult.getTelURI());
                    } else {
                        ResultHandler.dialPhone(this, telResult.getNumber());
                    }
                    break;
                case EMAIL_ADDRESS:
                    EmailAddressParsedResult emailResult = (EmailAddressParsedResult) parsedResult;
                    ResultHandler.sendEmail(this, emailResult.getTos(), emailResult.getCCs(), emailResult.getBCCs(), emailResult.getSubject(), emailResult.getBody());
                    break;
                case SMS:
                    SMSParsedResult smsResult = (SMSParsedResult) parsedResult;
                    ResultHandler.sendSMS(this, smsResult.getNumbers()[0], smsResult.getBody());
                    break;
                case WIFI:
                    WifiParsedResult wifiResult = (WifiParsedResult) parsedResult;
                    WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    if (wifiManager == null) {
                        return;
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Util.toast("Requesting connection to network...");
                        }
                    });
                    new WifiConfigManager(wifiManager).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, wifiResult);
                    finish();
                    break;
                case ADDRESSBOOK:
                    if(PerUtil.hasContactPermission()) {
                        AddressBookParsedResult addressResult = (AddressBookParsedResult) parsedResult;
                        String[] addresses = addressResult.getAddresses();
                        String address1 = addresses == null || addresses.length < 1 ? null : addresses[0];
                        String[] addressTypes = addressResult.getAddressTypes();
                        String address1Type = addressTypes == null || addressTypes.length < 1 ? null : addressTypes[0];
                        ResultHandler.addContact(this,
                                addressResult.getNames(),
                                addressResult.getNicknames(),
                                addressResult.getPronunciation(),
                                addressResult.getPhoneNumbers(),
                                addressResult.getPhoneTypes(),
                                addressResult.getEmails(),
                                addressResult.getEmailTypes(),
                                addressResult.getNote(),
                                addressResult.getInstantMessenger(),
                                address1,
                                address1Type,
                                addressResult.getOrg(),
                                addressResult.getTitle(),
                                addressResult.getURLs(),
                                addressResult.getBirthday(),
                                addressResult.getGeo());
                    } else {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CONTACTS}, PERMISSION_REQUEST_CODE);
                    }
                    break;
                case TEXT:
                case GEO:
                case PRODUCT:
                case CALENDAR:
                case ISBN:
                case VIN:
                default:
                    mCopy.setVisibility(View.VISIBLE);
                    mType.setText("Type : " + parsedResult.getType().name());
                    mDisplayResult.setText(ResultUtil.s_ParsedResult.getDisplayResult());
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(PERMISSION_REQUEST_CODE == requestCode) {
            if(null != permissions && permissions.length > 0) {
                if(PackageManager.PERMISSION_GRANTED == grantResults[0]) {
                }
            }
        }
    }
}
