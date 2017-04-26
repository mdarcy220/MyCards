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


import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.SystemClock;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Instances;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;


/**
 * A daemon that handles notification generation.
 * <br>
 */
public class NotificationService extends IntentService {

    public static final String[] INSTANCE_PROJECTION = new String[]{
            Instances.EVENT_ID,      // 0
            Instances.BEGIN,         // 1
            Instances.END,           // 2
            Instances.TITLE          // 3
    };

    // The indices for the projection array above.
    private static final int PROJECTION_BEGIN_INDEX = 1;
    private static final int PROJECTION_END_INDEX = 2;

    private long lastNotifMillis = 0;

    private List<NotificationRule> ruleList = new ArrayList<>();

    public NotificationService() {
        super("MyCardsNotificationService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        this.lastNotifMillis = intent.getLongExtra("lastRunElapsedRealtime", 0);
        rescheduleNotifications();
    }

    private void sendNotification() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.small_logo)
                        .setContentTitle("Time to review!")
                        .setContentText("You should review some flashcards!")
                        .setAutoCancel(true)
                        .setVibrate(new long[]{0, 100, 300, 80, 80, 80});

        Intent resultIntent = new Intent(this, ReviewActivity.class);
        resultIntent.putExtra("notificationId", 12);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(12, mBuilder.build());
    }

    private List<NotificationRule> getCalendarAsNotificationRules() {
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(Calendar.HOUR_OF_DAY, 0);
        beginTime.set(Calendar.MINUTE, 0);
        beginTime.set(Calendar.SECOND, 0);
        long startMillis = beginTime.getTimeInMillis();

        Calendar endTime = Calendar.getInstance();
        endTime.set(Calendar.HOUR_OF_DAY, 23);
        endTime.set(Calendar.MINUTE, 59);
        endTime.set(Calendar.SECOND, 59);
        long endMillis = endTime.getTimeInMillis();

        Cursor cur;
        ContentResolver cr = getContentResolver();

        String selection = "";//Instances.CALENDAR_ID + " = 1";
        String[] selectionArgs = new String[]{};

        Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon();
        ContentUris.appendId(builder, startMillis);
        ContentUris.appendId(builder, endMillis);

        cur = cr.query(builder.build(),
                INSTANCE_PROJECTION,
                selection,
                selectionArgs,
                null
        );

        List<NotificationRule> rules = new ArrayList<>();

        if (cur == null) {
            return rules;
        }

        while (cur.moveToNext()) {
            Calendar beginCalendar = Calendar.getInstance();
            beginCalendar.setTimeInMillis(cur.getLong(PROJECTION_BEGIN_INDEX));

            Calendar endCalendar = Calendar.getInstance();
            endCalendar.setTimeInMillis(cur.getLong(PROJECTION_END_INDEX));

            SimpleDatePattern datePattern = new SimpleDatePattern(
                    beginCalendar.getTime(),
                    endCalendar.getTime(),
                    0,
                    0
            );
            rules.add(new NotificationRule(-1, datePattern, true));
            System.out.println(cur.getString(3));
        }
        cur.close();
        return rules;
    }

    private void rescheduleNotifications() {
        final long FIFTEEN_MINUTES = 15 * 60 * 60 * 1000;

        List<NotificationRule> rules = new ArrayList<>();
        Collections.addAll(rules, MyCardsDBManager.getInstance(this).getAllNotificationRules());
        rules.addAll(getCalendarAsNotificationRules());

        MyCardsDBManager dbm = MyCardsDBManager.getInstance(this);
        if (!(Math.abs(this.lastNotifMillis - SystemClock.elapsedRealtime()) < FIFTEEN_MINUTES ||
                dbm.getDoNotDisturb() ||
                dateMatchesAnyRule(new Date(), rules) ||
                dbm.getCardsForReviewBefore(new Date()).length == 0)) {
            sendNotification();
        }

        Intent intent = new Intent(this, NotificationService.class);
        intent.putExtra("lastRunElapsedRealtime", SystemClock.elapsedRealtime());
        PendingIntent pi = PendingIntent.getService(this,
                0,
                intent,
                PendingIntent.FLAG_ONE_SHOT
        );

        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + FIFTEEN_MINUTES,
                pi
        );
    }

    private boolean dateMatchesAnyRule(Date d, List<NotificationRule> ruleList) {
        for (NotificationRule rule : ruleList) {
            if (rule.getDatePattern().dateMatches(d)) {
                return true;
            }
        }
        return false;
    }
}
