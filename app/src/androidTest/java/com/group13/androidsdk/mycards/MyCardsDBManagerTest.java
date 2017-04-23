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

import android.support.test.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit tests for MyCardsDBManager
 */
public class MyCardsDBManagerTest {
    private MyCardsDBManager dbm = null;
    @Before
    public void setUp() throws Exception {
        InstrumentationRegistry.getTargetContext().deleteDatabase("mycardsdb2");
        dbm = MyCardsDBManager.getInstance(InstrumentationRegistry.getTargetContext(), "mycardsdb2");

    }

    @After
    public void tearDown() throws Exception {
        InstrumentationRegistry.getTargetContext().deleteDatabase("mycardsdb2");
    }

    @Test
    public void deleteCardById() throws Exception {
        List<Card> cards = new ArrayList<>();
        cards.add(new Card(1, "frontside", "backside", new Date(), new Date(), 2.2, 1, 0));
        cards.add(new Card(2, "", "", new Date(), new Date(), 2.2, 2, 1));
        cards.add(new Card(3, "f3", "b3", new Date(0), new Date(0), -1, 0, 1));
        cards.add(new Card(700, "front4", "back4", new Date(), new Date(), 2.2, 1, 0));
        cards.get(1).addTag("mytag1");
        cards.get(1).addTag("mytag2");

        for(Card card : cards) {
            card.setId((int) dbm.insertOrUpdateCard(card));
        }


        for(Card card : cards) {
            Card card2 = dbm.getCardById(card.getId());
            assertEquals("Inserted and retreived cards must be equal", card, card2);
        }

        dbm.deleteCardById(3);

        assertNull("deleted card must not be in database", dbm.getCardById(3));


        for(Card card : cards) {
            Card card2 = dbm.getCardById(card.getId());
            if(card.getId() != 3) {
                assertEquals("Inserted and retreived cards must be equal", card, card2);
            }
        }
    }

    @Test
    public void insertOrUpdateCard() throws Exception {
        List<Card> cards = new ArrayList<>();
        cards.add(new Card(1, "frontside", "backside", new Date(), new Date(), 2.2, 1, 0));
        cards.add(new Card(2, "", "", new Date(), new Date(), 2.2, 2, 1));
        cards.add(new Card(3, "f3", "b3", new Date(0), new Date(0), -1, 0, 1));
        cards.add(new Card(700, "front4", "back4", new Date(), new Date(), 2.2, 1, 0));
        cards.get(1).addTag("mytag1");
        cards.get(1).addTag("mytag2");

        for(Card card : cards) {
            card.setId((int) dbm.insertOrUpdateCard(card));
        }
        for(Card card : cards) {
            Card card2 = dbm.getCardById(card.getId());
            assertEquals("Inserted and retreived cards must be equal", card, card2);
        }

        assertEquals("Card must be inserted at proper index", cards.get(3), dbm.getCardById(700));
    }

    @Test
    public void getCardById() throws Exception {
        List<Card> cards = new ArrayList<>();
        cards.add(new Card(1, "frontside", "backside", new Date(), new Date(), 2.2, 1, 0));
        cards.add(new Card(2, "", "", new Date(), new Date(), 2.2, 2, 1));
        cards.add(new Card(3, "f3", "b3", new Date(0), new Date(0), -1, 0, 1));
        cards.add(new Card(700, "front4", "back4", new Date(), new Date(), 2.2, 1, 0));

        cards.get(1).addTag("mytag1");
        cards.get(1).addTag("mytag2");

        for(Card card : cards) {
            card.setId((int) dbm.insertOrUpdateCard(card));
        }

        assertNull("getCardById() must be null if ID does not exist", dbm.getCardById(4));

        Card card2 = dbm.getCardById(3);
        assertEquals("Inserted and retreived cards must be equal", cards.get(2), card2);
    }

    @Test
    public void getAllCards() throws Exception {

        assertEquals("getAllCards() must not be null if there are cards", 0, dbm.getAllCards().length);
        List<Card> cards = new ArrayList<>();
        cards.add(new Card(1, "frontside", "backside", new Date(), new Date(), 2.2, 1, 0));
        cards.add(new Card(2, "", "", new Date(), new Date(), 2.2, 2, 1));
        cards.add(new Card(3, "f3", "b3", new Date(0), new Date(0), -1, 0, 1));
        cards.add(new Card(700, "front4", "back4", new Date(), new Date(), 2.2, 1, 0));

        cards.get(1).addTag("mytag1");
        cards.get(1).addTag("mytag2");

        for(Card card : cards) {
            card.setId((int) dbm.insertOrUpdateCard(card));
        }

        assertNotNull("getAllCards() must not be null if there are cards", dbm.getAllCards());
        assertEquals("getAllCards() must return all the cards in the database", 4, dbm.getAllCards().length);

    }

    @Test
    public void getCardsForReviewBefore() throws Exception {

    }

    @Test
    public void deleteNotificationRuleById() throws Exception {
        List<NotificationRule> notificationRules = new ArrayList<>();
        notificationRules.add(new NotificationRule(1, new SimpleDatePattern(new Date(), new Date(), 0, 0)));
        notificationRules.add(new NotificationRule(2, new SimpleDatePattern(new Date(10000), new Date(15000), 0, 0)));
        notificationRules.add(new NotificationRule(3, new SimpleDatePattern(new Date(10000), new Date(15000), 10000, 5)));
        notificationRules.add(new NotificationRule(700, new SimpleDatePattern(new Date(10000), new Date(), 0, 0)));

        for(NotificationRule notificationRule : notificationRules) {
            notificationRule.setId((int) dbm.insertOrUpdateNotificationRule(notificationRule));
        }

        for(NotificationRule notificationRule : notificationRules) {
            NotificationRule notificationRule2 = dbm.getNotificationRuleById(notificationRule.getId());
            assertEquals("Inserted and retreived notificationRules must be equal", notificationRule, notificationRule2);
        }

        dbm.deleteNotificationRuleById(3);

        assertNull("getCardById() must be null if ID does not exist", dbm.getNotificationRuleById(3));

        for(NotificationRule notificationRule : notificationRules) {
            NotificationRule notificationRule2 = dbm.getNotificationRuleById(notificationRule.getId());
            if(notificationRule.getId() != 3) {
                assertEquals("Inserted and retreived notificationRules must be equal",
                        notificationRule,
                        notificationRule2
                );
            }
        }
    }

    @Test
    public void insertOrUpdateNotificationRule() throws Exception {
        List<NotificationRule> notificationRules = new ArrayList<>();
        notificationRules.add(new NotificationRule(-1, new SimpleDatePattern(new Date(), new Date(), 0, 0)));
        notificationRules.add(new NotificationRule(-1, new SimpleDatePattern(new Date(10000), new Date(15000), 0, 0)));
        notificationRules.add(new NotificationRule(-1, new SimpleDatePattern(new Date(10000), new Date(15000), 10000, 5)));
        notificationRules.add(new NotificationRule(700, new SimpleDatePattern(new Date(10000), new Date(), 0, 0)));

        for(NotificationRule notificationRule : notificationRules) {
            notificationRule.setId((int) dbm.insertOrUpdateNotificationRule(notificationRule));
        }
        for(NotificationRule notificationRule : notificationRules) {
            NotificationRule notificationRule2 = dbm.getNotificationRuleById(notificationRule.getId());
            assertEquals("Inserted and retreived notificationRules must be equal", notificationRule, notificationRule2);
        }

        assertEquals("NotificationRule must be inserted at proper index", notificationRules.get(3), dbm.getNotificationRuleById(700));
    }

    @Test
    public void getNotificationRuleById() throws Exception {
        List<NotificationRule> notificationRules = new ArrayList<>();
        notificationRules.add(new NotificationRule(1, new SimpleDatePattern(new Date(), new Date(), 0, 0)));
        notificationRules.add(new NotificationRule(2, new SimpleDatePattern(new Date(10000), new Date(15000), 0, 0)));
        notificationRules.add(new NotificationRule(3, new SimpleDatePattern(new Date(10000), new Date(15000), 10000, 5)));
        notificationRules.add(new NotificationRule(700, new SimpleDatePattern(new Date(10000), new Date(), 0, 0)));

        for(NotificationRule notificationRule : notificationRules) {
            notificationRule.setId((int) dbm.insertOrUpdateNotificationRule(notificationRule));
        }
        assertNull("getCardById() must be null if ID does not exist", dbm.getNotificationRuleById(4));
        NotificationRule rule2 = dbm.getNotificationRuleById(3);

        assertEquals("Inserted and retreived cards must be equal", notificationRules.get(2), rule2);
    }

    @Test
    public void getAllNotificationRules() throws Exception {

    }

    @Test
    public void getAllNotificationRulesBeforeDate() throws Exception {

    }

    @Test
    public void setDoNotDisturb() throws Exception {
        // Duplicate tests to compensate for initial database state
        dbm.setDoNotDisturb(true);
        assertTrue(dbm.getDoNotDisturb());
        dbm.setDoNotDisturb(false);
        assertFalse(dbm.getDoNotDisturb());
        dbm.setDoNotDisturb(true);
        assertTrue(dbm.getDoNotDisturb());
        dbm.setDoNotDisturb(false);
        assertFalse(dbm.getDoNotDisturb());
    }

    @Test
    public void getDoNotDisturb() throws Exception {
        // Duplicate tests to compensate for initial database state
        dbm.setDoNotDisturb(true);
        assertTrue(dbm.getDoNotDisturb());
        dbm.setDoNotDisturb(false);
        assertFalse(dbm.getDoNotDisturb());
        dbm.setDoNotDisturb(true);
        assertTrue(dbm.getDoNotDisturb());
        dbm.setDoNotDisturb(false);
        assertFalse(dbm.getDoNotDisturb());
    }

}