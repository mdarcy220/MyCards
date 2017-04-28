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

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class ReviewActivity extends AppCompatActivity {

    private ReviewManager reviewManager = null;
    private Card curCard = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        reviewManager = new ReviewManager(MyCardsDBManager.getInstance(this));
        reviewManager.setFilterTags(getIntent().getStringArrayExtra("filterTags"));
        loadNextReview();
    }

    private void onNoMoreCardsToReview() {
        setShowingCardFront();
        ((TextView) findViewById(R.id.tvFrontText)).setText(R.string.no_more_cards_to_review);
        findViewById(R.id.layoutReviewFrontButtons).setVisibility(View.GONE);
    }

    public void onShowAnswerClick(View v) {
        if (v.getId() != R.id.btnShowAnswer) {
            return;
        }
        if (reviewManager.getNumCards() <= 0) {
            onNoMoreCardsToReview();
            return;
        }
        setShowingCardBack();
    }

    private void setShowingCardBack() {
        findViewById(R.id.layoutReviewFrontButtons).setVisibility(View.GONE);
        findViewById(R.id.tvBackText).setVisibility(View.VISIBLE);
        findViewById(R.id.layoutScoreButtons).setVisibility(View.VISIBLE);
        if (curCard != null) {
            ((TextView) findViewById(R.id.tvBackText)).setText(curCard.getBackText());
        }
    }

    private void setShowingCardFront() {
        findViewById(R.id.layoutReviewFrontButtons).setVisibility(View.VISIBLE);
        findViewById(R.id.tvBackText).setVisibility(View.GONE);
        findViewById(R.id.layoutScoreButtons).setVisibility(View.GONE);
        if (curCard != null) {
            ((TextView) findViewById(R.id.tvFrontText)).setText(curCard.getFrontText());
        }
    }

    public void onScoreCardButtonClick(View v) {
        int score;
        switch (v.getId()) {
            case R.id.btnRate0:
                score = 0;
                break;
            case R.id.btnRate1:
                score = 1;
                break;
            case R.id.btnRate2:
                score = 2;
                break;
            case R.id.btnRate3:
                score = 3;
                break;
            case R.id.btnRate4:
                score = 4;
                break;
            case R.id.btnRate5:
                score = 5;
                break;
            default:
                score = -1;
        }
        if (score == -1) {
            Log.e("score card btn click", "Unknown view clicked");
            return;
        }

        reviewManager.finishReview(curCard, score);
        loadNextReview();
    }

    private void loadNextReview() {
        if (reviewManager.getNumCards() <= 0) {
            curCard = null;
            onNoMoreCardsToReview();
        } else {
            curCard = reviewManager.getNextCard();
            setShowingCardFront();
        }
    }

    public void onSkipClick(View v) {
        loadNextReview();
    }
}
