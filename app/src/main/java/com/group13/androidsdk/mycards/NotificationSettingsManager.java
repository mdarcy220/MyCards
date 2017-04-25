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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Basically handles all the backend functionality of the notification settings UI. As with the
 * other classes marked "Instantiable" in the diagram, this class would be instantiated before use:
 *
 * <pre>
 * NotificationSettingsManager nsm = new NotificationSettingsManager();
 * nsm.setDoNotDisturb(true);
 * // ... And so on. The methods should be mostly self-explanatory
 * </pre>
 */
@SuppressWarnings("unused")
public class NotificationSettingsManager {

    private List<NotificationRule> rules = new ArrayList<>();
    private boolean doNotDisturbEnabled = false;
    private NotificationStorage ns = null;

    public NotificationSettingsManager(NotificationStorage ns) {
        this.ns = ns;
    }

    public void addRule(NotificationRule nr) {
        this.rules.add(nr);
        ns.upsertNotificationRule(nr);
    }

    /**
     * Removes a notification rule from the system (including from the underlying storage). If
     * multiple rules exist with the given id, they will all be removed.
     *
     * @param ruleId The id of the rule to remove
     */
    public void removeRule(int ruleId) {
        ns.deleteNotificationRuleById(ruleId);
        this.reloadRules();
    }

    private void reloadRules() {
        this.setRules(ns.getAllNotificationRules());
    }

    public void setDoNotDisturbEnabled(boolean enabled) {
        this.doNotDisturbEnabled = enabled;
        ns.setDoNotDisturb(enabled);
    }

    public boolean isDoNotDisturbEnabled() {
        return this.doNotDisturbEnabled;
    }

    public NotificationRule[] getRules() {
        return (NotificationRule[]) this.rules.toArray();
    }

    /**
     * Overwrite the entire rule list with the given rule list.
     */
    public void setRules(NotificationRule[] rules) {
        this.rules.clear();
        Collections.addAll(this.rules, rules);
    }
}
