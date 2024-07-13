package com.example.streaming_test_app.ui.streaming;

import android.app.Application;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;



public class LiveClassesViewModel extends AndroidViewModel {
    public MutableLiveData<Boolean> toggleComments = new MutableLiveData<>(true);

    public void toggleComments(){
        toggleComments.setValue(Boolean.FALSE.equals(toggleComments.getValue()));
    }

    public MutableLiveData<Boolean> isRemoveMode = new MutableLiveData<>(null);
    public void toggleIsRemoveMode(){
        isRemoveMode.setValue(Boolean.FALSE.equals(isRemoveMode.getValue()));
    }

    public MutableLiveData<Integer> paintColor = new MutableLiveData<>(Color.BLACK);
    public void setPaintColor(int color){
        paintColor.setValue(color);
    }

    private MutableLiveData<String> streamKey = new MutableLiveData<>("10000");
    public MutableLiveData<String> getStreamKey() {
        return streamKey;
    }

    private MutableLiveData<String> streamToken = new MutableLiveData<>();
    public MutableLiveData<String> getStreamToken() {
        return streamToken;
    }

    //LiveStreamRepo liveStreamRepo;
    private MutableLiveData<String> error = new MutableLiveData<>();

    public LiveClassesViewModel(@NonNull Application application) {
        super(application);
        //liveStreamRepo.getStreamCredentials(streamKey, streamToken, error);
    }


}
