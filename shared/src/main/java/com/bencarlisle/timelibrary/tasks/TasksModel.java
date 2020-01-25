package com.bencarlisle.timelibrary.tasks;

import android.util.Log;

import com.bencarlisle.timelibrary.main.DataControllable;
import com.bencarlisle.timelibrary.main.Merger;
import com.bencarlisle.timelibrary.main.Pollable;
import com.bencarlisle.timelibrary.main.Task;

import java.util.ArrayList;
import java.util.Calendar;

public abstract class TasksModel implements Pollable {

    protected final ArrayList<Task> tasks = new ArrayList<>();
    protected DataControllable dataControl;
    private Merger merger;

    protected abstract void addTaskView(Task task);
    protected abstract void deleteTask(int id);

    protected TasksModel(DataControllable dataControl) {
        this.dataControl = dataControl;
    }

    public void startPolling(int secondsToWait) {
        merger = new Merger(this, secondsToWait);
        merger.start();
    }

    public void stopPolling() {
        merger.interrupt();
        merger = null;
    }

    public void poll() {
        synchronized (tasks) {
            ArrayList<Task> newTasks= dataControl.getTasks();
            if (newTasks == null) {
                return;
            }
            for (Task task: newTasks) {
                if (!tasks.contains(task)) {
                    Log.e("TASK FOUND", task.toString());
                    tasks.add(task);
                    addTaskView(task);
                } else if (tasks.get(tasks.indexOf(task)).getHoursCompleted() != task.getHoursCompleted()) {
                    deleteTask(task.getId());
                    addTask(task);
                }
            }
            for (int i = 0; i < tasks.size(); i++) {
                if (!newTasks.contains(tasks.get(i))) {
                    Log.e("REMOVING TASK ", String.valueOf(tasks.get(i).getId()));
                    deleteTask(tasks.get(i).getId());
                }
            }
        }
    }

    void createTempEvent() {
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Calendar dueDate = Calendar.getInstance();
            dueDate.add(Calendar.DATE, 1);
            addTask(new Task(dueDate,  "HELLO FRIEND", 5, 10, 0));
        }).start();
    }

    private void addTask(Task task) {
        dataControl.addTask(task);
        tasks.add(task);
        addTaskView(task);
    }
}