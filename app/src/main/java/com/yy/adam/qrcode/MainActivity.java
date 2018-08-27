package com.yy.adam.qrcode;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

public class MainActivity extends AppCompatActivity {

    private CaptureManager mCaptureManager;
    private DecoratedBarcodeView mDecoratedBarcodeView;

    ImageView mFlashBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews(savedInstanceState);
    }

    private void initViews(Bundle savedInstanceState) {
        mDecoratedBarcodeView = findViewById(R.id.zxing_barcode_scanner);
        mDecoratedBarcodeView.setTorchListener(new DecoratedBarcodeView.TorchListener() {
            @Override
            public void onTorchOn() {
                mFlashBtn.setImageResource(R.drawable.flashlight_on);
            }

            @Override
            public void onTorchOff() {
                mFlashBtn.setImageResource(R.drawable.flashlight_off);
            }
        });
        mFlashBtn = findViewById(R.id.id_flashlight);
        mFlashBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mDecoratedBarcodeView.isTorchOn()) {
                    mDecoratedBarcodeView.setTorchOff();
                } else {
                    mDecoratedBarcodeView.setTorchOn();
                }
            }
        });

        mCaptureManager = new CaptureManager(this, mDecoratedBarcodeView);
        mCaptureManager.initializeFromIntent(getIntent(), savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCaptureManager.decode(new CaptureManager.IResultCallback() {
            @Override
            public void onResult(BarcodeResult result) {
                ResultUtil.handleResult(result);
            }
        });
        mCaptureManager.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCaptureManager.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCaptureManager.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mCaptureManager.onSaveInstanceState(outState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return mDecoratedBarcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }
}
