package com.yy.adam.qrcode;

import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ResultParser;
import com.journeyapps.barcodescanner.BarcodeResult;

public class ResultUtil {

    public static ParsedResult s_ParsedResult;
    public static void handleResult(BarcodeResult barcodeResult) {
        if(null != barcodeResult && null != barcodeResult.getResult()) {
            s_ParsedResult = ResultParser.parseResult(barcodeResult.getResult());
        }
        launchResultActivity();
    }

    private static void launchResultActivity() {
        ResultActivity.callStart();
    }
}
