package com.bencarlisle.timelibrary.main;

import java.util.ArrayList;

public interface DataControllable {

    void addEvent(Event event);
    void addReturnable(Returnable returnable);
    void addTask(Task task);

    ArrayList<Event> getEvents();
    ArrayList<Returnable> getReturnables();
    ArrayList<Task> getTasks();

    void removeEvent(int id);
    void removeReturnable(int id);
    void removeTask(int id);
}
