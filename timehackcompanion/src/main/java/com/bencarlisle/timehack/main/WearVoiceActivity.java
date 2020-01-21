package com.bencarlisle.timehack.main;

import com.bencarlisle.timelibrary.main.Event;
import com.bencarlisle.timelibrary.main.Parser;
import com.bencarlisle.timelibrary.main.Returnable;
import com.bencarlisle.timelibrary.main.Task;
import com.bencarlisle.timelibrary.main.VoiceRecognitionActivity;

public class WearVoiceActivity extends VoiceRecognitionActivity {

    @Override
    protected String checkAndParseResult(String result) {
        Event event = Parser.parseEventResult(result);
        if (event != null) {
            new SharedDataControl(this).addEvent(event);
            return "Successfully added event";
        }
        Returnable returnable = Parser.parseReturnableResult(result);
        if (returnable != null) {
            new SharedDataControl(this).addReturnable(returnable);
            return "Successfully added returnable";
        }
        Task task = Parser.parseTaskResult(result);
        if (task != null) {
            new SharedDataControl(this).addTask(task);
            return "Successfully added task";
        }
        return null;
    }
}
