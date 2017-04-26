/*
 * Copyright (c) 2017 Michael D'Arcy and Brianne O'Niel.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.group13.androidsdk.mycards;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class EditNotificationRuleActivity extends AppCompatActivity {

    NotificationRule rule = null;
    private EditText editStartDate;
    private EditText editEndDate;
    private EditText editStartTime;
    private EditText editEndTime;
    private EditText editRepeatInterval;
    private EditText editRepeatTimes;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_notification_rule);

        editStartDate = (EditText) findViewById(R.id.editStartDate);
        editStartTime = (EditText) findViewById(R.id.editStartTime);
        editEndDate = (EditText) findViewById(R.id.editEndDate);
        editEndTime = (EditText) findViewById(R.id.editEndTime);
        editRepeatInterval = (EditText) findViewById(R.id.editRepeatInterval);
        editRepeatTimes = (EditText) findViewById(R.id.editRepeatTimes);

        int ruleId = this.getIntent().getExtras().getInt("ruleId");
        rule = MyCardsDBManager.getInstance(this).getNotificationRuleById(ruleId);
        if (rule == null) {
            rule = new NotificationRule(-1,
                    new SimpleDatePattern(new Date(), new Date(), 0, 0),
                    true
            );
        }
        loadNotificationRuleToLayout(rule);

        initDatePickers();
        initTimePickers();
    }

    private void initDatePickers() {
        List<EditText> editTexts = new ArrayList<>();
        editTexts.add(editStartDate);
        editTexts.add(editEndDate);
        for (EditText e : editTexts) {
            e.setFocusable(false);
            e.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatePickerFragment fragment = new DatePickerFragment();
                    fragment.setEditView((EditText) v);
                    fragment.show(getSupportFragmentManager(), "datePicker");
                }
            });
        }
    }

    private void initTimePickers() {
        List<EditText> editTexts = new ArrayList<>();
        editTexts.add(editStartTime);
        editTexts.add(editEndTime);
        for (EditText e : editTexts) {
            e.setFocusable(false);
            e.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TimePickerFragment fragment = new TimePickerFragment();
                    fragment.setEditView((EditText) v);
                    fragment.show(getSupportFragmentManager(), "timePicker");
                }
            });
        }
    }

    private void loadNotificationRuleToLayout(NotificationRule rule) {
        if (rule == null) {
            return;
        }
        Calendar startDate = Calendar.getInstance();
        startDate.setTimeInMillis(rule.getDatePattern().getStartDate().getTime());
        editStartDate.setText(formatCalendarDate(startDate));
        editStartTime.setText(formatCalendarTime(startDate));

        Calendar endDate = Calendar.getInstance();
        endDate.setTimeInMillis(rule.getDatePattern().getEndDate().getTime());
        editEndDate.setText(formatCalendarDate(endDate));
        editEndTime.setText(formatCalendarTime(endDate));

        editRepeatInterval.setText(String.valueOf(rule.getDatePattern().getRepeatInterval() /
                (1000 * 60 * 60 * 24)));
        editRepeatTimes.setText(String.valueOf(rule.getDatePattern().getNumRepeats()));

    }

    private String formatCalendarDate(Calendar calendar) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return String.format(Locale.US, "%04d-%02d-%02d", year, month+1, day);
    }

    private String formatCalendarTime(Calendar calendar) {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        return String.format(Locale.US, "%02d:%02d", hour, minute);
    }

    private void saveLayoutToNotificationRule(NotificationRule rule) {
        if (rule == null) {
            return;
        }
        Calendar startDate = Calendar.getInstance();
        parseCalendarDate(startDate, editStartDate.getText().toString());
        parseCalendarTime(startDate, editStartTime.getText().toString());

        Calendar endDate = Calendar.getInstance();
        parseCalendarDate(endDate, editEndDate.getText().toString());
        parseCalendarTime(endDate, editEndTime.getText().toString());

        long repeatInterval = Integer.parseInt(editRepeatInterval.getText().toString())*1000*60*60*24;
        int numRepeats = Integer.parseInt(editRepeatTimes.getText().toString());

        SimpleDatePattern datePattern = new SimpleDatePattern(startDate.getTime(),
                endDate.getTime(),
                repeatInterval,
                numRepeats
        );

        rule.setDatePattern(datePattern);
    }

    private void parseCalendarDate(Calendar calendar, String isoDateStr) {
        String[] tokens = isoDateStr.trim().split("-");
        if (tokens.length != 3) {
            return;
        }
        int year = Integer.parseInt(tokens[0]);
        int month = Integer.parseInt(tokens[1]) - 1;
        int day = Integer.parseInt(tokens[2]);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
    }

    private void parseCalendarTime(Calendar calendar, String timeStr) {
        String[] tokens = timeStr.trim().split(":");
        if (tokens.length != 2) {
            return;
        }
        int hour = Integer.parseInt(tokens[0]);
        int minute = Integer.parseInt(tokens[1]) - 1;
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
    }

    public void onCancelAction(View v) {
        this.finish();
    }


    public void onDeleteAction(View v) {
        if (this.rule != null) {
            MyCardsDBManager.getInstance(this).deleteNotificationRuleById(this.rule.getId());
        }
        this.finish();
    }

    public void onSaveAction(View v) {
        if (this.rule != null) {
            saveLayoutToNotificationRule(this.rule);
            MyCardsDBManager.getInstance(this).upsertNotificationRule(this.rule);
        }
        this.finish();
    }
}
