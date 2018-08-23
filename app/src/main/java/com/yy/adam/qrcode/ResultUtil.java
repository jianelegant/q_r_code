package com.yy.adam.qrcode;

import android.widget.Toast;

import com.journeyapps.barcodescanner.BarcodeResult;

public class ResultUtil {

    public static void handleResult(BarcodeResult result) {
        if(null != result) {
            Toast.makeText(MainApp.s_GlobalCtx, result.getText(), Toast.LENGTH_SHORT).show();
        }
    }
}
