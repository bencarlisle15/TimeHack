package com.bencarlisle.timelibrary.main;

import com.google.api.services.calendar.model.Event;

public interface DataControllable {

    void addEvent(Event event);
    void addReturnable(Returnable returnable);
    void addTask(Task task);
}
