package com.bencarlisle.timehack.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.bencarlisle.timehack.R;
import com.bencarlisle.timelibrary.main.Helper;
import com.bencarlisle.timelibrary.main.Returnable;
import com.bencarlisle.timelibrary.main.Task;
import com.google.api.services.calendar.model.Event;

import net.openid.appauth.AuthState;

import java.util.Calendar;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends GeneralActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new Organizer().startScheduler(this);
		setContentView(R.layout.activity_main);
		DataControl dataControl = new DataControl(this);
		AuthState authState  = dataControl.getAuthState();
		dataControl.close();
		if (authState == null || authState.hasClientSecretExpired()) {
			startActivity(new Intent(this, AuthenticateActivity.class));
		}
		Spinner spinner = findViewById(R.id.types);
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				setViewsFor(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				setViewsFor(0);
			}

		});

		EditText startTime = findViewById(R.id.start_time);
		new TimeSetter(startTime, this);
		EditText endTime = findViewById(R.id.end_time);
		new TimeSetter(endTime, this);
		EditText dueDate = findViewById(R.id.due_date);
		new DateSetter(dueDate, this);
	}

	private void setViewsFor(int position) {
		runOnUiThread(() -> {
			LinearLayout times = findViewById(R.id.times);
			EditText description = findViewById(R.id.description);
			LinearLayout days = findViewById(R.id.days);
			EditText dueDate = findViewById(R.id.due_date);
			LinearLayout taskExtras = findViewById(R.id.task_extras);
			Button submit = findViewById(R.id.submit);

			switch (position) {
				case 1:
					times.setVisibility(View.VISIBLE);
					description.setVisibility(View.VISIBLE);
					days.setVisibility(View.GONE);
					dueDate.setVisibility(View.GONE);
					taskExtras.setVisibility(View.GONE);
					submit.setVisibility(View.VISIBLE);
					break;
				case 2:
					times.setVisibility(View.VISIBLE);
					description.setVisibility(View.VISIBLE);
					days.setVisibility(View.VISIBLE);
					dueDate.setVisibility(View.GONE);
					taskExtras.setVisibility(View.GONE);
					submit.setVisibility(View.VISIBLE);
					break;
				case 3:
					times.setVisibility(View.GONE);
					description.setVisibility(View.VISIBLE);
					days.setVisibility(View.GONE);
					dueDate.setVisibility(View.VISIBLE);
					taskExtras.setVisibility(View.VISIBLE);
					submit.setVisibility(View.VISIBLE);
					break;
				default:
					times.setVisibility(View.GONE);
					description.setVisibility(View.GONE);
					days.setVisibility(View.GONE);
					dueDate.setVisibility(View.GONE);
					taskExtras.setVisibility(View.GONE);
					submit.setVisibility(View.GONE);
					break;
			}
		});
	}

	public void submitElement(View view) {
		new Thread(this::submitElementRunnable).start();
	}

	private void submitElementRunnable() {
		Spinner spinner = findViewById(R.id.types);
		EditText startTime = findViewById(R.id.start_time);
		EditText endTime = findViewById(R.id.end_time);
		EditText description = findViewById(R.id.description);
		LinearLayout days = findViewById(R.id.days);
		EditText dueDate = findViewById(R.id.due_date);
		EditText hoursRequired = findViewById(R.id.hours_required);
		EditText priority = findViewById(R.id.task_priority);
		int position = spinner.getSelectedItemPosition();
		switch (position) {
			case 1:
				createEvent(startTime, endTime, description);
				break;
			case 2:
				createReturnable(startTime, endTime, description, days);
				break;
			case 3:
				createTask(description, dueDate, hoursRequired, priority);
				break;
			default:
				Helper.makeToast(this, "An error occurred");
		}
		setViewsFor(0);
}

	private void createEvent(EditText startTime, EditText endTime, EditText description) {
		Calendar[] timesArray = getTimesArray(startTime, endTime);
		String descriptionText = description.getText().toString();
		Event event = Helper.getEvent(timesArray[0], timesArray[1], descriptionText, -1);
		DataControl dataControl = new DataControl(this);
		dataControl.addEvent(event);
		dataControl.close();
		Helper.makeToast(this, "Successfully created event");
	}

	private void createReturnable(EditText startTime, EditText endTime, EditText description, LinearLayout days) {
		Calendar[] timesArray = getTimesArray(startTime, endTime);
		String descriptionText = description.getText().toString();
		boolean[] daysArray = getDays(days);
		Event event = Helper.getEvent(timesArray[0], timesArray[1], descriptionText, -1);
		Returnable returnable = new Returnable(daysArray, event);
		DataControl dataControl = new DataControl(this);
		dataControl.addReturnable(returnable);
		dataControl.close();
		Helper.makeToast(this, "Successfully created returnable");
	}

	private void createTask(EditText description, EditText dueDate, EditText hoursRequired, EditText priority) {
		int hours = Integer.parseInt(hoursRequired.getText().toString());
		int priorityNumber = Integer.parseInt(priority.getText().toString());
		String descriptionText = description.getText().toString();
		Calendar dueDateDate = getDueDate(dueDate);
		Task task = new Task(dueDateDate, descriptionText, hours, priorityNumber, 0);
		DataControl dataControl = new DataControl(this);
		dataControl.addTask(task);
		dataControl.close();
		Helper.makeToast(this, "Successfully created task");
	}

	private Calendar[] getTimesArray(EditText startTime, EditText endTime) {
		Calendar[] timesArray = new Calendar[2];
		timesArray[0] = getTime(startTime);
		timesArray[1] = getTime(endTime);
		return timesArray;
	}

	private Calendar getTime(EditText time) {
		String timeText = time.getText().toString();
		Pattern pattern = Pattern.compile("^(\\d{1,2}):(\\d{2}) ([P|A]M)$");

		Matcher matcher = pattern.matcher(timeText);
		Calendar calendar = Calendar.getInstance();

		if(matcher.find() && matcher.groupCount() == 4) {
			int hour = Integer.parseInt(Objects.requireNonNull(matcher.group(1)));
			int minute = Integer.parseInt(Objects.requireNonNull(matcher.group(2)));
			int amOrPm = Objects.equals(matcher.group(3), "A") ? Calendar.AM : Calendar.PM;
			calendar.set(Calendar.HOUR, hour);
			calendar.set(Calendar.MINUTE, minute);
			calendar.set(Calendar.AM_PM, amOrPm);
		}
		return calendar;
	}

	private boolean[] getDays(LinearLayout days) {
		boolean[] daysArray = new boolean[7];
		for (int i = 0; i < 7; i++) {
			CheckBox day = (CheckBox) days.getChildAt(i);
			daysArray[i] = day.isChecked();
		}
		return daysArray;
	}

	private Calendar getDueDate(EditText dueDate) {
		String dueDateText = dueDate.getText().toString();
		Pattern pattern = Pattern.compile("^(\\d{1,2})/(\\d{1,2})/(\\d{4})$");

		Matcher matcher = pattern.matcher(dueDateText);
		Calendar calendar = Calendar.getInstance();

		if(matcher.find() && matcher.groupCount() == 4) {
			int month = Integer.parseInt(Objects.requireNonNull(matcher.group(1)));
			int day = Integer.parseInt(Objects.requireNonNull(matcher.group(2)));
			int year = Integer.parseInt(Objects.requireNonNull(matcher.group(2)));
			calendar.set(Calendar.MONTH, month);
			calendar.set(Calendar.DAY_OF_MONTH, day);
			calendar.set(Calendar.YEAR, year);
		}
		return calendar;
	}
}
