package com.bencarlisle.timehack.main;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bencarlisle.timehack.day.DayActivity;
import com.bencarlisle.timehack.tasks.TasksActivity;

import java.util.ArrayList;
import java.util.Locale;

public abstract class GeneralActivity extends Activity {

    private final int REQ_CODE_SPEECH_INPUT = 100;

    protected abstract String checkAndParseResult(String result);

    public void dayCalendar(View view) {
        if (!(this instanceof DayActivity)) {
            startActivity(new Intent(this, DayActivity.class));
        }
    }

    public void tasks(View view) {
        if (!(this instanceof TasksActivity)) {
            startActivity(new Intent(this, TasksActivity.class));
        }
    }


    @SuppressWarnings("WeakerAccess")
    public void start(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        String speechPrompt = "SPEAK!";
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, speechPrompt);
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            String notSupported = "Not supported";
            Toast.makeText(this, notSupported, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && null != data) {

                ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if (results != null && results.size() > 0) {
                    String status = "Could not understand";
                    for (String result: results) {
                        Log.e("EVENT", result);
                        String tempStatus = checkAndParseResult(result);
                        Log.e("EVENT", tempStatus == null ? "NULL" : tempStatus);
                        if (tempStatus != null) {
                            status = tempStatus;
                            break;
                        }
                    }
                    Toast.makeText(this, status, Toast.LENGTH_SHORT).show();
                    start(null);
                } else {
                    Log.e("FINISHED", "no speech");
                }
            }
        }
    }
}
