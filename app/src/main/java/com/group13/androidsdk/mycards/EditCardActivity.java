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
import android.widget.TextView;

import java.util.Date;

public class EditCardActivity extends AppCompatActivity {

    private Card card = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_card);

        int cardId = this.getIntent().getExtras().getInt("cardId");
        card = MyCardsDBManager.getInstance(this).getCardById(cardId);
        if(card == null) {
            card = new Card("", "");
        }
        loadCardToLayout(card);
    }

    private void loadCardToLayout(Card c) {
        if(c == null) {
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
        for(String tag : tagStrs) {
            tag = tag.trim();
            if(!tag.equals("")) {
                c.addTag(tag);
            }
        }
    }


    public void onCancelAction(View v) {
        this.finish();
    }


    public void onDeleteAction(View v) {
        if(this.card != null) {
            MyCardsDBManager.getInstance(this).deleteCardById(this.card.getId());
        }
        this.finish();
    }

    public void onSaveAction(View v) {
        if(this.card != null) {
            saveLayoutToCard(this.card);
            MyCardsDBManager.getInstance(this).insertOrUpdateCard(this.card);
        }
        this.finish();
    }
}
