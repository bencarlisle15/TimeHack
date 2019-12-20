package com.bencarlisle.timehack.returnables;

import android.os.Bundle;
import android.view.View;

import com.bencarlisle.timehack.R;
import com.bencarlisle.timehack.main.GeneralActivity;

public class ReturnablesActivity extends GeneralActivity {

    private ReturnablesHandler returnablesHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_returnables);
        returnablesHandler = new ReturnablesHandler(this);
    }

    public void deleteReturnable(View view) {
        returnablesHandler.deleteReturnable(view.getId());
    }
}
