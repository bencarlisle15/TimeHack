package com.bencarlisle.timehack.day;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.widget.Toast;

import com.bencarlisle.timehack.main.Event;
import com.bencarlisle.timehack.main.Parser;

import java.util.ArrayList;

public class VoiceRecognitionActivity extends Activity {
    private int SPEECH_REQUEST_CODE = 1;
    private static WidgetCalendarControl lastCalendarControl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sendRecognizeIntent();
    }

    public static void setCalendarControl(WidgetCalendarControl calendarControl) {
        lastCalendarControl = calendarControl;
    }

    private void sendRecognizeIntent() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak!");
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 10);
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE) {
            if (resultCode == RESULT_OK && null != data) {

                ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if (results != null && results.size() > 0) {
                    String status = "Could not understand";
                    for (String result : results) {
                        Log.e("EVENT", result);
                        String tempStatus = checkAndParseResult(result, lastCalendarControl);
                        Log.e("EVENT", tempStatus == null ? "NULL" : tempStatus);
                        if (tempStatus != null) {
                            status = tempStatus;
                            break;
                        }
                    }
                    Toast.makeText(this, status, Toast.LENGTH_SHORT).show();
                    sendRecognizeIntent();
                    return;
                } else {
                    Log.e("FINISHED", "no speech");
                }
            }
            finish();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private String checkAndParseResult(String result, CalendarModel calendarModel) {
        Event event = Parser.parseResult(result);
        if (event == null) {
            return null;
        }
        Event eventResult = calendarModel.addEvent(event, false);
        return eventResult == null ? "Successfully Added" : ("Conflicting event: " + eventResult);
    }
}