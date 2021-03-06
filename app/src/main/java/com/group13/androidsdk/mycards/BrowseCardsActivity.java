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
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class BrowseCardsActivity extends AppCompatActivity {

    private List<String> filterTags = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_cards);
    }

    private void initListView() {
        String[] filterTagsArr = new String[0];
        filterTagsArr = filterTags.toArray(filterTagsArr);
        final Card[] cards = 0 < filterTagsArr.length ? MyCardsDBManager.getInstance(this)
                .getCardsByTags(
                filterTagsArr) : MyCardsDBManager.getInstance(this).getAllCards();
        CardArrayAdapter cardAdapter = new CardArrayAdapter(this,
                R.layout.browse_cards_listitem,
                cards
        );
        ListView listView = ((ListView) findViewById(R.id.listViewCards));
        listView.setAdapter(cardAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Card c = cards[position];
                Intent intent = new Intent(BrowseCardsActivity.this, EditCardActivity.class);
                intent.putExtra("cardId", c.getId());
                startActivity(intent);
            }
        });
    }

    public void onFilterByTagButtonClick(View v) {
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
        initListView();
    }

    @Override
    public void onResume() {
        super.onResume();
        initListView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu m) {
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.browse_cards_appbar, m);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_card_appbar_button:
                Card c = new Card("", "");
                Intent intent = new Intent(BrowseCardsActivity.this, EditCardActivity.class);
                intent.putExtra("cardId", c.getId());
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}


class CardArrayAdapter extends ArrayAdapter<Card> {

    private Card[] values;
    private AppCompatActivity context;

    static class CardViewHolder {
        public TextView frontTextView;
        public TextView backTextView;
    }

    public CardArrayAdapter(@NonNull AppCompatActivity context,
                            @LayoutRes int resource, Card[] values) {
        super(context, resource);
        this.values = values;
        this.context = context;
    }

    @Override
    public int getCount() {
        return this.values.length;
    }

    @NonNull
    @Override
    public View getView(int index, View convertView, @NonNull ViewGroup parent) {
        View itemView = convertView;
        if (itemView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            itemView = inflater.inflate(R.layout.browse_cards_listitem, parent, false);
            CardViewHolder holder = new CardViewHolder();
            holder.backTextView = (TextView) itemView.findViewById(R.id.listItemCardBackText);
            holder.frontTextView = (TextView) itemView.findViewById(R.id.listItemCardFrontText);
            itemView.setTag(holder);
        }

        Card c = this.values[index];
        CardViewHolder holder = (CardViewHolder) itemView.getTag();
        holder.backTextView.setText(c.getBackText());
        holder.frontTextView.setText(c.getFrontText());

        return itemView;
    }
}