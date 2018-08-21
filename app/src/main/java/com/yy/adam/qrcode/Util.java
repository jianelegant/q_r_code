package com.yy.adam.qrcode;

import android.text.TextUtils;
import android.widget.Toast;

public class Util {

    public static void toast(String str) {
        if(!TextUtils.isEmpty(str)) {
            Toast.makeText(MainApp.s_GlobalCtx, str, Toast.LENGTH_SHORT).show();
        }
    }
}
