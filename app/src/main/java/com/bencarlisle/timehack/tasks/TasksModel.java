package com.bencarlisle.timehack.tasks;

import android.content.Context;
import android.util.Log;

import com.bencarlisle.timehack.main.DataControl;
import com.bencarlisle.timehack.main.Merger;
import com.bencarlisle.timehack.main.Pollable;
import com.bencarlisle.timehack.main.Task;

import java.util.ArrayList;
import java.util.Calendar;

public abstract class TasksModel implements Pollable {

    final ArrayList<Task> tasks = new ArrayList<>();
    DataControl dataControl;

    protected abstract void addTaskView(Task task);
    protected abstract void deleteTask(int id);

    TasksModel(Context context) {
        dataControl = new DataControl(context);
//        dataControl.clearTasks();
        new Merger(this).start();
    }

    void start() {
        initTasks();
//        createTempEvent();
    }

    public void poll() {
        synchronized (tasks) {
            ArrayList<Task> newTasks= dataControl.getTasks();
            for (Task task: newTasks) {
                if (!tasks.contains(task)) {
                    Log.e("TASK FOUND", task.toString());
                    tasks.add(task);
                    addTaskView(task);
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

    private void initTasks() {
        synchronized (tasks) {
            for (Task task: tasks) {
                addTask(task);
            }
        }
    }

    private void addTask(Task task) {
        dataControl.addTask(task);
        tasks.add(task);
        addTaskView(task);
    }
}
