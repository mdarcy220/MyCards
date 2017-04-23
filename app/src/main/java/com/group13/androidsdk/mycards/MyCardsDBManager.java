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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Database manager. This class provides a high-level interface to database operations in the app.
 */
class MyCardsDBManager extends SQLiteOpenHelper implements NotificationStorage, CardStorage {

    private static final String doNotDisturbKeyName = "notifications.do_not_disturb";
    private static Map<String, MyCardsDBManager> mInstances = new HashMap<>();
    
    private static final String CREATE_CARD_TABLE_SQL = "" +
            "CREATE TABLE card ( " +
            "    _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "    frontText TEXT NOT NULL DEFAULT '', " +
            "    backText TEXT NOT NULL DEFAULT '', " +
            "    easiness REAL NOT NULL DEFAULT 2.5, " +
            "    nextReviewDate INTEGER, " +
            "    lastReviewDate INTEGER, " +
            "    numRepetitions INTEGER, " +
            "    lastIncorrectRep INTEGER " +
            ");";

    private static final String CREATE_TAG_TABLE_SQL = "" +
            "CREATE TABLE tag (\n" +
            "    cardId INTEGER NOT NULL,\n" +
            "    tagName TEXT NOT NULL,\n" +
            "    PRIMARY KEY (cardId, tagName),\n" +
            "    FOREIGN KEY (cardId) REFERENCES card(_id)\n" +
            "        ON UPDATE CASCADE\n" +
            "        ON DELETE CASCADE\n" +
            ");";

    private static final String CREATE_NOTIFICATIONRULE_TABLE_SQL = "" +
            "CREATE TABLE notificationRule (" +
            "    _id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    enabled INTEGER(1) NOT NULL,\n" +
            "    nextMatchDate INTEGER NOT NULL,\n" +
            "    datePattern BLOB NOT NULL\n" +
            ");";

    private static final String CREATE_KEYVALUE_TABLE_SQL = "" +
            "CREATE TABLE keyValueStore (\n" +
            "    key TEXT PRIMARY KEY,\n" +
            "    valueInt INTEGER DEFAULT NULL,\n" +
            "    valueReal REAL DEFAULT NULL,\n" +
            "    valueText TEXT DEFAULT NULL,\n" +
            "    valueBlob BLOB DEFAULT NULL\n" +
            ");";

    private MyCardsDBManager(Context context,
                     String name,
                     SQLiteDatabase.CursorFactory factory,
                     int dbVersion) {
        super(context, name, factory, dbVersion);
    }

    public static synchronized MyCardsDBManager getInstance(Context context) {
        return getInstance(context, "mycardsdb");
    }

    public static synchronized MyCardsDBManager getInstance(Context context, String dbname) {
        if(!mInstances.containsKey(dbname)) {
            MyCardsDBManager instance = new MyCardsDBManager(context, dbname, null, 1);
            mInstances.put(dbname, instance);
        }
        return mInstances.get(dbname);
    }

    @Override
    public void deleteCardById(int cardId) {
        SQLiteDatabase db = this.getWritableDatabase();
        Object[] args = {cardId};
        try {
            db.beginTransaction();
            db.execSQL("DELETE FROM card WHERE _id = ?;", args);
            db.execSQL("DELETE FROM tag WHERE cardId = ?", args);
            db.setTransactionSuccessful();
        } catch (SQLiteException e) {
            Log.e("DBManager", "deleteCardById(" + cardId + ") failed with SQLiteException: " +
                    e.getLocalizedMessage());
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    private static ContentValues cardToContentValues(Card card) {
        ContentValues cv = new ContentValues();
        if (0 <= card.getId()) {
            cv.put("_id", card.getId());
        }
        cv.put("nextReviewDate", card.getNextReviewDate().getTime());
        cv.put("lastReviewDate", card.getLastReviewDate().getTime());
        cv.put("easiness", card.getEasiness());
        cv.put("frontText", card.getFrontText());
        cv.put("backText", card.getBackText());
        cv.put("numRepetitions", card.getNumRepetitions());
        cv.put("lastIncorrectRep", card.getLastIncorrectRep());
        return cv;
    }

    @Override
    public long insertOrUpdateCard(Card card) {
        ContentValues cv = cardToContentValues(card);
        String[] whereArgs = {String.valueOf(card.getId())};
        SQLiteDatabase db = this.getWritableDatabase();
        long inserted_rowid = -1;
        try {
            db.beginTransaction();
            db.delete("card", "_id = ?", whereArgs);
            inserted_rowid = db.insert("card", null, cv);
            ContentValues tagValues = new ContentValues();
            tagValues.put("cardId", card.getId());
            for (String tagName : card.getTags()) {
                tagValues.put("tagName", tagName);
                db.insert("tag", null, tagValues);
            }
            db.setTransactionSuccessful();
        } catch (SQLiteException e) {
            Log.e("DBManager", "insertOrUpdateCard(card): " + e.getLocalizedMessage());
        } finally {
            db.endTransaction();
            db.close();
        }
        return inserted_rowid;
    }

    /**
     * Converts the record currently pointed by the given Cursor into a Card object. Note that
     * this method does NOT add tags to the card.
     */
    private static Card cardFromCursor(Cursor cursor) {
        if (cursor.isBeforeFirst() || cursor.isAfterLast()) {
            return null;
        }

        return new Card(cursor.getInt(cursor.getColumnIndex("_id")),
                cursor.getString(cursor.getColumnIndex("frontText")),
                cursor.getString(cursor.getColumnIndex("backText")),
                new Date(cursor.getLong(cursor.getColumnIndex("nextReviewDate"))),
                new Date(cursor.getLong(cursor.getColumnIndex("lastReviewDate"))),
                cursor.getDouble(cursor.getColumnIndex("easiness")),
                cursor.getInt(cursor.getColumnIndex("numRepetitions")),
                cursor.getInt(cursor.getColumnIndex("lastIncorrectRep"))
        );
    }

    /**
     * Produces an array of cards from the given Cursor (assumed to contain records from the Card
     * table). Note that this method does NOT add tags to the cards.
     */
    private static Card[] cardArrayFromCursor(Cursor cursor) {
        List<Card> cards = new ArrayList<>();
        boolean status = cursor.moveToFirst();
        while (status) {
            cards.add(cardFromCursor(cursor));
            status = cursor.moveToNext();
        }
        return cards.toArray(new Card[cards.size()]);
    }

    private static void addCardTagsFromDb(Card card, SQLiteDatabase db) {
        if(card == null) {
            return;
        }
        Cursor c = db.rawQuery("SELECT * FROM tag WHERE cardId = ?;",
                new String[]{String.valueOf(card.getId())}
        );

        if (!c.moveToFirst()) {
            return;
        }
        do {
            card.addTag(c.getString(c.getColumnIndex("tagName")));
        } while (c.moveToNext());
        c.close();
    }

    @Override
    public Card getCardById(int cardId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] args = {String.valueOf(cardId)};
        Cursor c = db.rawQuery("SELECT * FROM card WHERE _id = ?;", args);
        c.moveToFirst();
        Card ret = cardFromCursor(c);
        addCardTagsFromDb(ret, db);

        // Cleanup
        c.close();
        db.close();

        return ret;
    }

    @Override
    public Card[] getAllCards() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM card;", null);
        Card[] ret = cardArrayFromCursor(c);
        for (Card card : ret) {
            addCardTagsFromDb(card, db);
        }
        // Cleanup
        c.close();
        db.close();

        return ret;
    }

    @Override
    public Card[] getCardsForReviewBefore(Date d) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] args = {String.valueOf(d.getTime())};
        Cursor c = db.rawQuery("SELECT * FROM card WHERE nextReviewDate < ?;", args);
        Card[] ret = cardArrayFromCursor(c);
        for (Card card : ret) {
            addCardTagsFromDb(card, db);
        }

        // Cleanup
        c.close();
        db.close();

        return ret;
    }

    @Override
    public void deleteNotificationRuleById(int ruleId) {
        SQLiteDatabase db = this.getWritableDatabase();
        Object[] args = {ruleId};
        try {
            db.beginTransaction();
            db.execSQL("DELETE FROM notificationRule WHERE _id = ?;", args);
            db.setTransactionSuccessful();
        } catch (SQLiteException e) {
            Log.e("DBManager",
                    "deleteNotificationRuleById(" + ruleId + ") failed with SQLiteException: " +
                            e.getLocalizedMessage()
            );
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    private static byte[] serializeToByteArray(Serializable obj) {
        try {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            (new ObjectOutputStream(byteStream)).writeObject(obj);
            return byteStream.toByteArray();
        } catch (IOException e) {
            return null;
        }
    }

    private static Object deserializeFromByteArray(byte[] arr) {
        try {
            ByteArrayInputStream byteStream = new ByteArrayInputStream(arr);
            return (new ObjectInputStream(byteStream)).readObject();
        } catch (IOException e) {
            return null;
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private static ContentValues ruleToContentValues(NotificationRule rule) {
        Date nextMatch = rule.getDatePattern().nextMatch(new Date());
        long nextMatchEpoch = nextMatch != null ? nextMatch.getTime() : Long.MAX_VALUE;

        ContentValues cv = new ContentValues();
        if (0 <= rule.getId()) {
            cv.put("_id", rule.getId());
        }

        cv.put("enabled", rule.isEnabled());
        cv.put("nextMatchDate", nextMatchEpoch);
        cv.put("datePattern", serializeToByteArray(rule.getDatePattern()));
        return cv;
    }

    @Override
    public long insertOrUpdateNotificationRule(NotificationRule rule) {
        ContentValues cv = ruleToContentValues(rule);
        String[] whereArgs = {String.valueOf(rule.getId())};
        SQLiteDatabase db = this.getWritableDatabase();
        long inserted_rowid = -1;
        try {
            db.beginTransaction();
            db.delete("notificationRule", "_id = ?", whereArgs);
            inserted_rowid = db.insert("notificationRule", null, cv);
            db.setTransactionSuccessful();
        } catch (SQLiteException e) {
            Log.e("DBManager", "insertOrUpdateNotificationRule(rule): " + e.getLocalizedMessage());
        } finally {
            db.endTransaction();
            db.close();
        }
        return inserted_rowid;
    }

    private NotificationRule ruleFromCursor(Cursor cursor) {
        if (cursor.isBeforeFirst() || cursor.isAfterLast()) {
            return null;
        }

        return new NotificationRule(cursor.getInt(cursor.getColumnIndex("_id")),
                (SimpleDatePattern) deserializeFromByteArray(cursor.getBlob(cursor.getColumnIndex(
                        "datePattern"))),
                (cursor.getInt(cursor.getColumnIndex("enabled")) == 1)
        );
    }

    private NotificationRule[] ruleArrayFromCursor(Cursor cursor) {
        List<NotificationRule> rules = new ArrayList<>();
        boolean status = cursor.moveToFirst();
        while (status) {
            rules.add(ruleFromCursor(cursor));
            status = cursor.moveToNext();
        }

        return rules.toArray(new NotificationRule[rules.size()]);
    }

    @Override
    public NotificationRule getNotificationRuleById(int ruleId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] args = {String.valueOf(ruleId)};
        Cursor c = db.rawQuery("SELECT * FROM notificationRule WHERE _id = ?;", args);
        c.moveToFirst();
        NotificationRule ret = ruleFromCursor(c);

        // Cleanup
        c.close();
        db.close();

        return ret;
    }

    @Override
    public NotificationRule[] getAllNotificationRules() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM notificationRule;", null);
        NotificationRule[] ret = ruleArrayFromCursor(c);

        // Cleanup
        c.close();
        db.close();

        return ret;
    }

    @Override
    public NotificationRule[] getAllNotificationRulesBeforeDate(Date d) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] args = {String.valueOf(d.getTime())};
        Cursor c = db.rawQuery("SELECT * FROM notificationRule WHERE nextMatchDate <= ?;", args);
        NotificationRule[] ret = ruleArrayFromCursor(c);

        // Cleanup
        c.close();
        db.close();

        return ret;
    }

    @Override
    public void setDoNotDisturb(boolean enabled) {
        ContentValues cv = new ContentValues();
        cv.put("key", doNotDisturbKeyName);
        cv.put("valueInt", enabled ? 1 : 0);
        String[] args = {doNotDisturbKeyName};
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransaction();
            db.delete("keyValueStore", "key = ?", args);
            db.insert("keyValueStore", null, cv);
            db.setTransactionSuccessful();
        } catch (SQLiteException e) {
            Log.e("DBManager", "setDoNotDisturb(): " + e.getLocalizedMessage());
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    @Override
    public boolean getDoNotDisturb() {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] args = {doNotDisturbKeyName};
        Cursor c = db.rawQuery("SELECT valueInt FROM keyValueStore WHERE key = ?;", args);
        c.moveToFirst();
        boolean ret = c.getInt(c.getColumnIndex("valueInt")) == 1;

        // Cleanup
        c.close();
        db.close();

        return ret;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.beginTransaction();
            db.execSQL(CREATE_CARD_TABLE_SQL);
            db.execSQL(CREATE_TAG_TABLE_SQL);
            db.execSQL(CREATE_NOTIFICATIONRULE_TABLE_SQL);
            db.execSQL(CREATE_KEYVALUE_TABLE_SQL);

            ContentValues cv = new ContentValues();
            cv.put("key", doNotDisturbKeyName);
            cv.put("valueInt", 0);
            db.insert("keyValueStore", null, cv);

            db.setTransactionSuccessful();
        } catch (SQLiteException e) {
            Log.e("DBManager", "onCreate(db): " + e.getLocalizedMessage());
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO: Implement this
    }
}
