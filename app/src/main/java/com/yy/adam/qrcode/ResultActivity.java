package com.yy.adam.qrcode;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

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
        updateDisplay();
    }

    private void updateDisplay() {
        mType.setText(ResultUtil.s_ParsedResult.getType().name());
        mDisplayResult.setText(ResultUtil.s_ParsedResult.getDisplayResult());
    }
}
