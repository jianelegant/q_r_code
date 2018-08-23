package com.yy.adam.qrcode;

import android.text.TextUtils;
import android.widget.Toast;

public class Util {

    public static void toast(String msg) {
        if(TextUtils.isEmpty(msg)) {
            return;
        }
        Toast.makeText(MainApp.s_GlobalCtx, msg, Toast.LENGTH_SHORT).show();
    }
}
