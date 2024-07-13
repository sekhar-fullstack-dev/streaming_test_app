package com.example.streaming_test_app.ui.streaming;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;


import com.bumptech.glide.Glide;
import com.example.streaming_test_app.BuildConfig;
import com.example.streaming_test_app.R;
import com.example.streaming_test_app.base.BaseActivity;
import com.example.streaming_test_app.databinding.ActivityLiveClassBinding;
import com.example.streaming_test_app.utils.LoadAndDrawImageTask;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.pedro.encoder.input.video.CameraHelper;
import com.pedro.library.rtmp.RtmpCamera1;
import com.pedro.rtmp.utils.ConnectCheckerRtmp;

import java.util.ArrayList;
import java.util.List;


public class LiveClassActivity extends BaseActivity {
    private static final int REQUEST_READ_STORAGE_PERMISSION = 200;
    SharedPreferences sharedPreferences;
    private ActivityLiveClassBinding binding;
    private LiveClassesViewModel model;
    boolean isAdmin = true;
    private RtmpCamera1 rtmpCamera1;
    private String streamKey = "10000";
    private String TAG = this.getClass().getName();
    private BottomSheetBehavior<View> bottomSheetBehavior;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_live_class);
        model = new ViewModelProvider(LiveClassActivity.this).get(LiveClassesViewModel.class);
        binding.setModel(model);
        binding.setLifecycleOwner(LiveClassActivity.this);
        initialise();
        observe();
        initializeBottomSheetBehavior();
        initialiseGridView();
    }
    private void initialiseGridView() {
        List<Uri> images = fetchImages(this);
        ImageAdapter adapter = new ImageAdapter(this, images);
        binding.gridViewImages.setAdapter(adapter);
    }
    public List<Uri> fetchImages(Context context) {
        List<Uri> list = new ArrayList<>();
        ContentResolver contentResolver = getContentResolver();
        Uri collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME
        };
        try (Cursor cursor = contentResolver.query(collection, projection, null, null, null)) {
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            while (cursor.moveToNext()) {
                long id = cursor.getLong(idColumn);
                Uri contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                list.add(contentUri);
            }
        }
        return list;
    }
    private class ImageAdapter extends BaseAdapter {
        private Context context;
        private List<Uri> imagePaths; // List to hold image URIs as strings

        public ImageAdapter(Context context, List<Uri> imagePaths) {
            this.context = context;
            this.imagePaths = imagePaths;
        }

        @Override
        public int getCount() {
            return imagePaths.size();
        }

        @Override
        public Object getItem(int position) {
            return imagePaths.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                imageView = new ImageView(context);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setLayoutParams(new GridView.LayoutParams(GridView.AUTO_FIT, 300)); // Set your dimension
            } else {
                imageView = (ImageView) convertView;
            }
            Glide.with(context).load(imagePaths.get(position)).into(imageView);
            imageView.setOnLongClickListener(view -> {
                ClipData.Item item = new ClipData.Item(imagePaths.get(position));
                ClipData clipData = new ClipData("BitmapData", new String[]{ClipDescription.MIMETYPE_TEXT_URILIST}, item);
                //ClipData clipData = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                view.startDragAndDrop(clipData, shadowBuilder, view, 0);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                return true;
            });
            return imageView;
        }
    }
    private void initializeBottomSheetBehavior() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet);

        // Optional: Set the initial state of the Bottom Sheet
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        // Optional: Set the peek height
        bottomSheetBehavior.setPeekHeight(300, true);

        // Optional: Set callback to observe state changes
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                // Called when the bottom sheet's state changes
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    // Bottom sheet is expanded
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    // Bottom sheet is collapsed
                }
                // Handle other states if needed
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // Called when the bottom sheet is being dragged
            }
        });
    }
    private void observe() {
        model.toggleComments.observe(this, aBoolean -> {
            /*if (aBoolean!=null){
                binding.upArrow.setVisibility(View.GONE);
                binding.upArrow.setRotation(aBoolean?180:0);
                if (aBoolean){
                    binding.comments.setVisibility(View.VISIBLE);
                }
                binding.comments.animate()
                        .translationY(!aBoolean?binding.comments.getHeight():0)
                        .setDuration(350)
                        .setInterpolator(new DecelerateInterpolator())
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                binding.comments.setVisibility(!aBoolean?View.GONE:View.VISIBLE);
                                binding.upArrow.setVisibility(View.VISIBLE);
                            }
                        }).start();
                binding.upArrow.animate()
                        .rotationBy(aBoolean ? -180f : 180f)
                        .setDuration(300)
                        .start();
            }*/
            //showBottomSheet();
        });
        model.isRemoveMode.observe(this, aBoolean -> {
            if (aBoolean!=null){
                binding.handWritingView.setIsEraserMode(aBoolean);
            }
        });
        model.paintColor.observe(this, integer -> {
            if (integer!=null){
                binding.handWritingView.setPaintColor(integer);
            }
        });
        model.getStreamKey().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s!=null){
                    askCameraAndMicPermission();
                }
            }
        });
    }
    private void initialise() {
        //binding.handWritingView.loadSavedScreenContent(sharedPreferences);
        binding.handWritingView.setIsAdmin(true);
        setUpDragListener();
    }
    private void setUpDragListener() {
        binding.handWritingView.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                switch (event.getAction()) {
                    case DragEvent.ACTION_DROP:
                        // Retrieve the ClipData from the event
                        ClipData clipData = event.getClipData();
                        // Get the image URI
                        if (clipData != null && clipData.getItemCount() > 0) {
                            Uri imageUri = clipData.getItemAt(0).getUri();
                            Log.d(TAG, "onDrag: "+imageUri);
                            loadAndDrawImage(imageUri,event.getX(),event.getY());
                        }
                        break;
                }
                return true;
            }
        });
    }
    private void loadAndDrawImage(Uri imageUri, float x, float y) {
        new LoadAndDrawImageTask(binding.handWritingView,this,500,x,y).execute(imageUri);
    }
    @Override
    protected void onCameraMicPermissionResult(boolean isPermission) {
        if (isPermission){
            initialiseLiveStreaming();
        }
    }
    private void initialiseLiveStreaming() {
        rtmpCamera1 = new RtmpCamera1(binding.surfaceView, new ConnectCheckerRtmp() {
            @Override
            public void onConnectionStartedRtmp(@NonNull String s) {

            }

            @Override
            public void onConnectionSuccessRtmp() {

            }

            @Override
            public void onConnectionFailedRtmp(@NonNull String s) {

            }

            @Override
            public void onNewBitrateRtmp(long l) {

            }

            @Override
            public void onDisconnectRtmp() {

            }

            @Override
            public void onAuthErrorRtmp() {

            }

            @Override
            public void onAuthSuccessRtmp() {

            }
        });
        int rotation = CameraHelper.getCameraOrientation(this);
        if (!rtmpCamera1.isStreaming()) {
            if (rtmpCamera1.prepareAudio() && rtmpCamera1.prepareVideo(640, 480, 30, 1228800, rotation)) {
                rtmpCamera1.startStream("rtmp://"+ BuildConfig.RTMP_STREAM_IP +"/live/"+model.getStreamKey().getValue());
                Log.d(getClass().getName(), "startStream: "+streamKey);
            } else {
                // Handle error, e.g., show a Toast
            }
        } else {
            rtmpCamera1.stopStream();
        }
    }

    @Override
    protected void onDestroy() {
        binding.handWritingView.saveScreenContent(sharedPreferences);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //rtmpCamera1.stopStream();
            }
        },100);
        super.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (rtmpCamera1!=null && !rtmpCamera1.isStreaming()) {
            rtmpCamera1.startPreview(CameraHelper.Facing.FRONT);
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (rtmpCamera1!=null && !rtmpCamera1.isStreaming()) {
            rtmpCamera1.stopPreview();
        }
    }
}
