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
import android.widget.TextView;

public class ReviewActivity extends AppCompatActivity {

    private ReviewManager reviewManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        reviewManager = new ReviewManager(MyCardsDBManager.getInstance(this));
        if(reviewManager.getNumCards() <= 0) {
            onNoMoreCardsToReview();
        }
    }

    private void onNoMoreCardsToReview() {
        setShowingCardFront();

        ((TextView) findViewById(R.id.tvFrontText)).setText("No more cards to review");
        findViewById(R.id.layoutReviewFrontButtons).setVisibility(View.GONE);
    }

    public void onShowAnswerClick(View v) {
        if(v.getId() != R.id.btnShowAnswer) {
            return;
        }
        if(reviewManager.getNumCards() <= 0) {
            onNoMoreCardsToReview();
            return;
        }
        setShowingCardFront();
    }

    private void setShowingCardBack() {
        findViewById(R.id.layoutReviewFrontButtons).setVisibility(View.GONE);
        findViewById(R.id.tvBackText).setVisibility(View.VISIBLE);
        findViewById(R.id.layoutScoreButtons).setVisibility(View.VISIBLE);
    }

    private void setShowingCardFront() {
        findViewById(R.id.layoutReviewFrontButtons).setVisibility(View.VISIBLE);
        findViewById(R.id.tvBackText).setVisibility(View.GONE);
        findViewById(R.id.layoutScoreButtons).setVisibility(View.GONE);
    }
}
