package com.bencarlisle.timehack.tasks;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.bencarlisle.timehack.main.DataControl;

import java.util.ArrayList;
import java.util.Calendar;

public abstract class TaskModel {

    final ArrayList<Task> tasks = new ArrayList<>();
    DataControl dataControl;

    public abstract void addTaskView(Task task);
    public abstract void deleteTask(int id);

    public TaskModel(Context context) {
        dataControl = new DataControl(context);
//        dataControl.clearTasks();
        new Merger(this).start();
    }

    public void start() {
        initTasks();
        createTempEvent();
    }

    void poll() {
        synchronized (tasks) {
            ArrayList<Task> newTasks= dataControl.getTasks();
            for (Task task: newTasks) {
                if (!tasks.contains(task)) {
                    Log.e("TASK FOUND", task.toString());
                    tasks.add(task);
                    addTaskView(task);
                }
            }
            for (Task task: tasks) {
                if (!newTasks.contains(task)) {
                    Log.e("REMOVING TASK ", task.toString());
                    deleteTask(task.getId());
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
                Log.e("TASK", task.toString());
                addTask(task);
            }
        }
    }

    public void addTask(Task task) {
        dataControl.addTask(task);
        tasks.add(task);
        addTaskView(task);
    }


    String convertToDate(Calendar dueDate) {
        return (dueDate.get(Calendar.MONTH) + 1) + "/" + dueDate.get(Calendar.DATE);
    }
}
