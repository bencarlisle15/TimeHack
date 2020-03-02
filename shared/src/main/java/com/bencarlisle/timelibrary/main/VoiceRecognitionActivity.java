package com.bencarlisle.timelibrary.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.widget.Toast;

import com.google.api.services.calendar.model.Event;

import java.util.ArrayList;

public abstract class VoiceRecognitionActivity extends Activity {
    protected int SPEECH_REQUEST_CODE = 1;

    protected abstract DataControllable getDataControllable();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sendRecognizeIntent();
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
                new Thread(()->analyzeText(data)).start();
            }
            finish();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    protected void analyzeText(Intent data) {
        Looper.prepare();
        ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
        if (results != null && results.size() > 0) {
            String status = "Could not understand";
            for (String result : results) {
                Log.e("EVENT", result);
                String tempStatus = checkAndParseResult(result);
                Log.e("EVENT", tempStatus == null ? "NULL" : tempStatus);
                if (tempStatus != null) {
                    status = tempStatus;
                    break;
                }
            }
            Helper.makeToast(this, status);
            sendRecognizeIntent();
        } else {
            Log.e("FINISHED", "no speech");
        }

    }

    private String checkAndParseResult(String result) {
        DataControllable dataControllable = getDataControllable();
        Event event = Parser.parseEventResult(result);
        if (event != null) {
            dataControllable.addEvent(event);
            return "Successfully added event";
        }
        Returnable returnable = Parser.parseReturnableResult(result);
        if (returnable != null) {
            dataControllable.addReturnable(returnable);
            return "Successfully added returnable";
        }
        Task task = Parser.parseTaskResult(result);
        if (task != null) {
            dataControllable.addTask(task);
            return "Successfully added task";
        }
        return null;
    }
}