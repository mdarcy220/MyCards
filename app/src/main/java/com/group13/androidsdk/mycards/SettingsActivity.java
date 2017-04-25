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

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ((Switch) findViewById(R.id.switchDoNotDisturb)).setChecked(MyCardsDBManager.getInstance(
                this).getDoNotDisturb());
    }

    private void initListView() {
        final NotificationRule[] rules = MyCardsDBManager.getInstance(this)
                .getAllNotificationRules();
        NotificationRuleArrayAdapter ruleAdapter = new NotificationRuleArrayAdapter(this,
                R.layout.notification_rule_listitem,
                rules
        );
        ListView listView = ((ListView) findViewById(R.id.listViewRules));
        listView.setAdapter(ruleAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NotificationRule nr = rules[position];
                Intent intent = new Intent(SettingsActivity.this,
                        EditNotificationRuleActivity.class
                );
                intent.putExtra("ruleId", nr.getId());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        initListView();
    }

    public void onAddRuleClick(View v) {
        Intent intent = new Intent(SettingsActivity.this,
                EditNotificationRuleActivity.class
        );
        intent.putExtra("ruleId", -1);
        startActivity(intent);
    }

    public void onDoNotDisturbClick(View v) {
        MyCardsDBManager dbm = MyCardsDBManager.getInstance(this);
        dbm.setDoNotDisturb(((Switch) v).isChecked());
    }
}


class NotificationRuleArrayAdapter extends ArrayAdapter<NotificationRule> {

    private NotificationRule[] values;
    private AppCompatActivity context;

    private static class NotificationRuleViewHolder {
        TextView dateRangeLabel;
        Switch switchRuleEnabled;
    }

    public NotificationRuleArrayAdapter(@NonNull AppCompatActivity context,
                                        @LayoutRes int resource, NotificationRule[] values) {
        super(context, resource);
        this.context = context;
        this.values = values;
    }

    @Override
    public int getCount() {
        return this.values.length;
    }

    @NonNull
    @Override
    public View getView(int index, final View convertView, @NonNull ViewGroup parent) {
        View itemView = convertView;
        if (itemView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            itemView = inflater.inflate(R.layout.notification_rule_listitem, parent, false);
            NotificationRuleViewHolder holder = new NotificationRuleViewHolder();
            holder.dateRangeLabel = (TextView) itemView.findViewById(R.id.ruleDateRangleLabel);
            holder.switchRuleEnabled = (Switch) itemView.findViewById(R.id.switchRuleEnabled);
            itemView.setTag(holder);
        }

        final NotificationRule nr = this.values[index];
        NotificationRuleViewHolder holder = (NotificationRuleViewHolder) itemView.getTag();
        holder.dateRangeLabel.setText(labelFromDatePattern(nr.getDatePattern()));
        holder.switchRuleEnabled.setChecked(nr.isEnabled());
        holder.switchRuleEnabled.setFocusable(false);
        holder.switchRuleEnabled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nr.setEnabled(((Switch) v).isChecked());
                MyCardsDBManager.getInstance(context).upsertNotificationRule(nr);
            }
        });

        return itemView;
    }

    private String labelFromDatePattern(SimpleDatePattern datePattern) {
        if (datePattern == null) {
            return "null";
        }
        Calendar startDate = Calendar.getInstance();
        startDate.setTime(datePattern.getStartDate());
        Calendar endDate = Calendar.getInstance();
        endDate.setTime(datePattern.getEndDate());

        String repeatStr = "";
        if (datePattern.getNumRepeats() > 0) {
            repeatStr = String.format(Locale.US,
                    "\nRepeats every %d day(s), for a total of %d time(s)",
                    datePattern.getRepeatInterval() / (1000 * 60 * 60 * 24),
                    datePattern.getNumRepeats()
            );
        }

        return formatCalendarDate(startDate) + " " + formatCalendarTime(startDate) + " to " +
                formatCalendarDate(
                        endDate) + " " + formatCalendarTime(endDate) + repeatStr;
    }

    private String formatCalendarDate(Calendar calendar) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return String.format(Locale.US, "%04d-%02d-%02d", year, month, day);
    }

    private String formatCalendarTime(Calendar calendar) {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        return String.format(Locale.US, "%02d:%02d", hour, minute);
    }
}