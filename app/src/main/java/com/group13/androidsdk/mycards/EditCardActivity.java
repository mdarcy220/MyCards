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

/*
 * Mike D'Arcy (2553280)
 * Brianne O'Neil (2583119)
 * CIS 470 Final Project - Group 13
 * 28 April 2017
 */

package com.group13.androidsdk.mycards;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditCardActivity extends AppCompatActivity {

    private Card card = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_card);

        int cardId = this.getIntent().getExtras().getInt("cardId");
        card = MyCardsDBManager.getInstance(this).getCardById(cardId);
        if (card == null) {
            card = new Card("", "");
            findViewById(R.id.cardStatsContainer).setVisibility(View.GONE);
        }
        loadCardToLayout(card);
    }


    private String formatDateAsString(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, day);
    }


    private void loadCardToLayout(Card c) {
        if (c == null) {
            return;
        }
        ((EditText) findViewById(R.id.tvEditCardFrontText)).setText(c.getFrontText());
        ((EditText) findViewById(R.id.tvEditCardBackText)).setText(c.getBackText());
        String tagStr = "";
        for (String tag : c.getTags()) {
            tagStr += tag + ", ";
        }
        // Remove the final comma
        if (tagStr.length() > 2) {
            tagStr = tagStr.substring(0, tagStr.length() - 2);
        }
        ((EditText) findViewById(R.id.tvEditTags)).setText(tagStr);

        // Load card statistics
        ((TextView) findViewById(R.id.tvLastReviewDate)).setText(formatDateAsString(c
                .getLastReviewDate()));
        ((TextView) findViewById(R.id.tvNextReviewDate)).setText(formatDateAsString(c
                .getNextReviewDate()));
        ((TextView) findViewById(R.id.tvTotalReviews)).setText(String.valueOf(c
                .getNumRepetitions()));
        ((TextView) findViewById(R.id.tvEasiness)).setText(String.valueOf(c.getEasiness()));

    }

    private void saveLayoutToCard(Card c) {
        if (c == null) {
            return;
        }
        c.setFrontText(((EditText) findViewById(R.id.tvEditCardFrontText)).getText().toString());
        c.setBackText(((EditText) findViewById(R.id.tvEditCardBackText)).getText().toString());

        String tagStr = ((EditText) findViewById(R.id.tvEditTags)).getText().toString();
        String[] tagStrs = tagStr.split(",");
        c.clearTags();
        for (String tag : tagStrs) {
            tag = tag.trim();
            if (!tag.equals("") && !isStringInArray(c.getTags(), tag)) {
                c.addTag(tag);
            }
        }
    }

    private boolean isStringInArray(String[] arr, String str) {
        if(arr == null || arr.length == 0) {
            return false;
        }
        for(String s : arr) {
            if(str.equals(s)) {
                return true;
            }
        }
        return false;
    }


    public void onCancelAction(View v) {
        this.finish();
    }


    public void onDeleteAction(View v) {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Deleting Card")
                .setMessage("Are you sure you want to delete this card? This cannot be undone.")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (EditCardActivity.this.card != null) {
                            MyCardsDBManager.getInstance(EditCardActivity.this).deleteCardById(
                                    EditCardActivity.this.card.getId());
                        }
                        EditCardActivity.this.finish();
                    }

                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public void onSaveAction(View v) {
        if (this.card != null) {
            saveLayoutToCard(this.card);
            MyCardsDBManager.getInstance(this).upsertCard(this.card);
        }
        this.finish();
    }
}
