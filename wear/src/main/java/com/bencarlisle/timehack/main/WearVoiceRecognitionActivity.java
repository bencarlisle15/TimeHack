package com.bencarlisle.timehack.main;

import com.bencarlisle.timelibrary.main.DataControllable;
import com.bencarlisle.timelibrary.main.VoiceRecognitionActivity;

public class WearVoiceRecognitionActivity extends VoiceRecognitionActivity {

    @Override
    protected DataControllable getDataControllable() {
        return new SharedDataControl(this);
    }
}
