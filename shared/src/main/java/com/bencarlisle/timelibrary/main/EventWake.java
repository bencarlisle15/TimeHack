package com.bencarlisle.timelibrary.main;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.bencarlisle.timelibrary.R;

public class EventWake extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_wake);
        byte[] eventBytes = getIntent().getByteArrayExtra("event");
        int startsIn = getIntent().getIntExtra("startsIn", 0);
        Event event = new Event(eventBytes, false);
        TextView startsInView = findViewById(R.id.starts_in);
        String startsInString = "Event starts in " + startsIn + " minutes";
        startsInView.setText(startsInString);
        TextView descriptionView = findViewById(R.id.description);
        descriptionView.setText(event.getDescription());
        String timeString = Helper.convertTimeToString(event.getStartTime()) + " - " + Helper.convertTimeToString(event.getEndTime());
        TextView timeView = findViewById(R.id.times);
        timeView.setText(timeString);
    }
}
