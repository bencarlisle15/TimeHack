package com.bencarlisle.timehack;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private final int REQ_CODE_SPEECH_INPUT = 100;
    private CalendarControl calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        calendar = new CalendarControl(this);
    }

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
            Toast.makeText(getApplicationContext(), notSupported, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                    Toast.makeText(getApplicationContext(), status, Toast.LENGTH_SHORT).show();
                    start(null);
                } else {
                    Log.e("FINISHED", "no speech");
                }
            }
        }
    }

    private String checkAndParseResult(String result) {
        Event event = Parser.parseResult(result);
        if (event == null) {
            return null;
        }
        Event eventResult = calendar.addEvent(event, false);
        return eventResult  == null ? "Successfully Added" : ("Conflicting event: " + eventResult );
    }
}
