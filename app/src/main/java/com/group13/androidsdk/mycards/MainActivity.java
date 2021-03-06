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

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<String> filterTags = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent serviceIntent = new Intent(this, NotificationService.class);
        this.startService(serviceIntent);
    }

    public void onStartReviewBtnClick(View v) {
        EditText editFilterTags = (EditText) findViewById(R.id.editFilterTags);
        String tagStr = editFilterTags.getText().toString();
        String[] tagStrs = tagStr.split(",");
        filterTags.clear();
        for (String tag : tagStrs) {
            tag = tag.trim();
            if (!tag.equals("")) {
                filterTags.add(tag);
            }
        }
        Intent intent = new Intent(this, ReviewActivity.class);
        intent.putExtra("filterTags", filterTags.toArray(new String[filterTags.size()]));
        startActivity(intent);
    }

    public void onBrowseCardsBtnClick(View v) {
        Intent intent = new Intent(this, BrowseCardsActivity.class);
        startActivity(intent);
    }

    public void onSettingsBtnClick(View v) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}
