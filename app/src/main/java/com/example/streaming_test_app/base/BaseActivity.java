package com.example.streaming_test_app.base;

import static com.example.streaming_test_app.utils.Constants.CAMERA_MIC_REQUEST_PERMISSION_CODE;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.streaming_test_app.interfaces.OnCallbackListener;

import com.google.android.material.snackbar.Snackbar;


public class BaseActivity extends AppCompatActivity {
    private OnCallbackListener onCallbackListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public void askCameraAndMicPermission() {
        requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO},CAMERA_MIC_REQUEST_PERMISSION_CODE);
    }

    private final ActivityResultLauncher<String[]> mOpenDocument = registerForActivityResult(new ActivityResultContracts.OpenDocument(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri uri) {
            if (uri != null) {
                takePersistableUriPermission(uri);
                if (onCallbackListener != null) {
                    onCallbackListener.callBack(uri);
                    onCallbackListener = null;
                }
            }
        }
    });

    public void pickImageOrVideo(OnCallbackListener onCallbackListener) {
        this.onCallbackListener = onCallbackListener;
        mOpenDocument.launch(new String[]{"image/*", "video/*"}); // Use appropriate MIME types
    }

    private void takePersistableUriPermission(Uri uri) {
        final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
        // Check for the freshest data regarding permissions for the URI
        getContentResolver().takePersistableUriPermission(uri, takeFlags);
    }



    protected void onCameraMicPermissionResult(boolean isPermission){

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_MIC_REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0) {
                int count = 0;
                for (int i:grantResults){
                    if (i==0){
                        count++;
                    }
                }
                onCameraMicPermissionResult(count == 2);
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void Toast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void SnackBar(String msg){
        View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
        Snackbar.make(rootView,msg, Snackbar.LENGTH_LONG).show();
    }
}
