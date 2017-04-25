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

import java.util.Date;

/**
 * An interface for storage and retrieval of <code>Card</code> objects.
 */
@SuppressWarnings("unused")
interface CardStorage {
    /**
     * Deletes the card with the given ID from the storage
     */
    void deleteCardById(int cardId);

    /**
     * If there is a card already in the storage that has the same ID as the given card, that
     * card is updated so its fields match those of the given card. If there is no such card, a
     * new card is inserted into the storage. If the given card has ID -1, a new ID for the card
     * will be returned.
     */
    long upsertCard(Card card);

    /**
     * @return the card with the given ID from the storage (<code>null</code> if no such card
     * exists)
     */
    Card getCardById(int cardId);

    /**
     * @return an array of all cards in the storage (empty array if no cards)
     */
    Card[] getAllCards();

    /**
     * @return an array of cards scheduled to be reviewed before the given <code>Date</code>
     * (empty array if no cards)
     */
    Card[] getCardsForReviewBefore(Date d);
}
