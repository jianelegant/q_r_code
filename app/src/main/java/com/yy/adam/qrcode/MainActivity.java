package com.yy.adam.qrcode;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import com.google.zxing.client.android.Intents;
import com.google.zxing.client.android.ViewfinderView;
import com.google.zxing.client.android.camera.CameraManager;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback{

    private static final String TAG = MainActivity.class.getSimpleName();
    private ViewfinderView mViewfinderView;
    private CameraManager mCameraManager;
    private boolean hasSurface;

    private CaptureActivityHandler mHandler;

    private Collection<BarcodeFormat> decodeFormats;
    private Map<DecodeHintType,?> decodeHints;
    private String characterSet;

    private Result mSavedResultToShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hasSurface = false;
    }

    @Override
    protected void onResume() {
        super.onResume();

        mCameraManager = new CameraManager(getApplication());
        mViewfinderView = findViewById(R.id.viewfinder_view);
        mViewfinderView.setCameraManager(mCameraManager);

        decodeFormats = null;
        characterSet = null;
        Intent intent = getIntent();
        if(null != intent) {
            characterSet = intent.getStringExtra(Intents.Scan.CHARACTER_SET);
        }

        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            // The activity was paused but not stopped, so the surface still exists. Therefore
            // surfaceCreated() won't be called, so init the camera here.
            initCamera(surfaceHolder);
        } else {
            // Install the callback and wait for surfaceCreated() to init the camera.
            surfaceHolder.addCallback(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mHandler != null) {
            mHandler.quitSynchronously();
            mHandler = null;
        }
        mCameraManager.closeDriver();
        //historyManager = null; // Keep for onActivityResult
        if (!hasSurface) {
            SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
            SurfaceHolder surfaceHolder = surfaceView.getHolder();
            surfaceHolder.removeCallback(this);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (holder == null) {
            Log.e(TAG, "*** WARNING *** surfaceCreated() gave us a null surface!");
        }
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (mCameraManager.isOpen()) {
            Log.w(TAG, "initCamera() while already open -- late SurfaceView callback?");
            return;
        }
        try {
            mCameraManager.openDriver(surfaceHolder);
            // Creating the handler starts the preview, which can also throw a RuntimeException.
            if (mHandler == null) {
                mHandler = new CaptureActivityHandler(this, decodeFormats, decodeHints, characterSet, mCameraManager);
            }
            decodeOrStoreSavedBitmap(null, null);
        } catch (IOException ioe) {
            Log.w(TAG, ioe);
//            displayFrameworkBugMessageAndExit();
        } catch (RuntimeException e) {
            // Barcode Scanner has seen crashes in the wild of this variety:
            // java.?lang.?RuntimeException: Fail to connect to camera service
            Log.w(TAG, "Unexpected error initializing camera", e);
//            displayFrameworkBugMessageAndExit();
        }
    }

    private void decodeOrStoreSavedBitmap(Bitmap bitmap, Result result) {
        // Bitmap isn't used yet -- will be used soon
        if (mHandler == null) {
            mSavedResultToShow = result;
        } else {
            if (result != null) {
                mSavedResultToShow = result;
            }
            if (mSavedResultToShow != null) {
                Message message = Message.obtain(mHandler, R.id.decode_succeeded, mSavedResultToShow);
                mHandler.sendMessage(message);
            }
            mSavedResultToShow = null;
        }
    }

    public ViewfinderView getViewfinderView() {
        return mViewfinderView;
    }

    public CameraManager getCameraManager() {
        return mCameraManager;
    }

    public Handler getHandler() {
        return mHandler;
    }

    public void handleDecode(Result result, Bitmap barcode, float scaleFactor) {
        String str = result.getText();
        Log.d(TAG, str);
        Util.toast(str);
    }

    public void drawViewfinder() {
        mViewfinderView.drawViewfinder();
    }
}
