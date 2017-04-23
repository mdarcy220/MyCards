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
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BrowseCardsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_cards);

        //System.out.println(MyCardsDBManager.getInstance(this).getAllCards()[0].getFrontText());
        CardArrayAdapter cardAdapter = new CardArrayAdapter(this, R.layout.browse_cards_listitem, MyCardsDBManager.getInstance(this).getAllCards());
        ((ListView)findViewById(R.id.listViewCards)).setAdapter(cardAdapter);
        cardAdapter.notifyDataSetChanged();
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
                System.out.println("HELLO, APPBAR");
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
        System.out.println("test2");
    }

    @Override
    public int getCount() {
        return this.values.length;
    }

    @NonNull
    @Override
    public View getView(int index, View convertView, @NonNull ViewGroup parent) {
        View itemView = convertView;
        if(itemView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            itemView = inflater.inflate(R.layout.browse_cards_listitem, parent, false);
            CardViewHolder holder = new CardViewHolder();
            holder.backTextView = (TextView) itemView.findViewById(R.id.listItemCardBackText);
            holder.frontTextView = (TextView) itemView.findViewById(R.id.listItemCardFrontText);
            itemView.setTag(holder);
        }

        Card c = this.values[index];
        CardViewHolder holder = (CardViewHolder)itemView.getTag();
        holder.backTextView.setText(c.getBackText());
        holder.frontTextView.setText(c.getFrontText());

        return itemView;
    }
}