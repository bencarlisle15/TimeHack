package com.bencarlisle.timehack.main;

import com.bencarlisle.timelibrary.main.DataControllable;
import com.bencarlisle.timelibrary.main.VoiceRecognitionActivity;

public class AppVoiceRecognitionActivity extends VoiceRecognitionActivity {
    @Override
    protected DataControllable getDataControllable() {
        return new DataControl(this);
    }
}
