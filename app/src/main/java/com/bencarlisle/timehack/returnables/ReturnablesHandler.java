package com.bencarlisle.timehack.returnables;

import android.app.Activity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.bencarlisle.timehack.R;
import com.bencarlisle.timelibrary.main.DataControllable;
import com.bencarlisle.timelibrary.main.Helper;
import com.bencarlisle.timelibrary.main.Returnable;
import com.bencarlisle.timelibrary.main.ViewAdapter;

public class ReturnablesHandler extends ReturnablesModel {

    private ViewAdapter viewAdapter;
    private Activity activity;

    public ReturnablesHandler(DataControllable dataControllable, Activity activity) {
        super(dataControllable);
        this.activity = activity;
        viewAdapter = new ViewAdapter(activity, R.id.returnables);
        ((ListView) activity.findViewById(R.id.returnables)).setAdapter(viewAdapter);
        startPolling(1);
    }


    public void deleteReturnable(int id) {
        activity.runOnUiThread(() -> {
            dataControl.removeReturnable(id);
            synchronized (returnables) {
                for (int i = 0; i < returnables.size(); i++) {
                    if (returnables.get(i).getId() == id) {
                        viewAdapter.removeAt(i);
                        returnables.remove(i);
                        break;
                    }
                }
            }
        });
    }

    public void addReturnableView(Returnable returnable) {
        activity.runOnUiThread(() -> addReturnablesViewRunnable(returnable));
    }

    private void addReturnablesViewRunnable(Returnable returnable) {
        View returnableView = View.inflate(activity, R.layout.returnable, null);
        ((TextView) returnableView.findViewById(R.id.description)).setText(returnable.getEvent().getDescription());
        ((TextView) returnableView.findViewById(R.id.days)).setText(returnable.getDaysString());
        ((TextView) returnableView.findViewById(R.id.start_time)).setText(Helper.convertTimeToString(returnable.getEvent().getStartTime()));
        ((TextView) returnableView.findViewById(R.id.end_time)).setText(Helper.convertTimeToString(returnable.getEvent().getEndTime()));
        returnableView.setId(returnable.getId());
        viewAdapter.add(returnableView);
    }
}
