package com.yy.adam.qrcode;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;

import com.google.zxing.client.result.EmailAddressParsedResult;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.SMSParsedResult;
import com.google.zxing.client.result.TelParsedResult;

public class ResultActivity extends AppCompatActivity {

    TextView mType;
    TextView mDisplayResult;
    Button mCopy;

    public static void callStart() {
        Intent intent = new Intent(MainApp.s_GlobalCtx, ResultActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MainApp.s_GlobalCtx.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        initViews();
    }

    private void initViews() {
        mType = findViewById(R.id.id_type);
        mDisplayResult = findViewById(R.id.id_display_result);
        mCopy = findViewById(R.id.id_copy);
    }

    @Override
    protected void onResume() {
        super.onResume();
        handleResult();
    }

    private void handleResult() {
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
                case TEXT:
                case ADDRESSBOOK:
                case GEO:
                case PRODUCT:
                case CALENDAR:
                case WIFI:
                case ISBN:
                case VIN:
                default:
                    mType.setText("Type : " + parsedResult.getType().name());
                    mDisplayResult.setText(ResultUtil.s_ParsedResult.getDisplayResult());
                    break;
            }
        }
    }
}
