package com.yy.adam.qrcode;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

public class PerUtil {

    public static boolean hasContactPermission() {
        return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(MainApp.s_GlobalCtx, Manifest.permission.WRITE_CONTACTS);
    }
}
