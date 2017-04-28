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

import java.util.Date;

/**
 * An interface for storage and retrieval of notification settings.
 */
@SuppressWarnings("unused")
interface NotificationStorage {

    /**
     * Deletes the notification rule with the given Id
     */
    void deleteNotificationRuleById(int ruleId);

    /**
     * If there is already a notification rule with the same ID as the given rule, the existing
     * rule in storage is updated to match the given rule. If no rule with matching ID exists,
     * the given rule is inserted into the storage as a new rule.
     *
     * @return The new ID of the rule in the storage (same as given if updating, new ID if
     * inserting)
     */
    long upsertNotificationRule(NotificationRule rule);

    /**
     * Gets the stored notification rule with ID matching <code>ruleID</code>. If no such rule
     * exists, this method returns <code>null</code>.
     */
    NotificationRule getNotificationRuleById(int ruleId);

    /**
     * Gets all notification rules in storage as an array. If there are no rules, an empty array
     * is returned.
     */
    NotificationRule[] getAllNotificationRules();

    /**
     * Gets all notification rules in storage for which the next match occurs at or before given
     * Date. If there are no such rules, returns an empty array.
     */
    NotificationRule[] getAllNotificationRulesBeforeDate(Date d);

    void setDoNotDisturb(boolean enabled);

    boolean getDoNotDisturb();
}
