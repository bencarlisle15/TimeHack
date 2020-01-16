package com.bencarlisle.timelibrary.returnables;

import android.os.Bundle;
import android.view.View;

import com.bencarlisle.timelibrary.R;
import com.bencarlisle.timelibrary.main.GeneralActivity;

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
