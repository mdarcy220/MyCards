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
 * Represents a "rule" for the notification rules list. Each notification rule has a date pattern
 * (used to select times that the notification is active) and a boolean to indicate whether the
 * rule is enabled. It also has an ID so it can be uniquely identified. The methods themselves
 * should be mostly self-explanatory (just getter/setter type methods).
 */
@SuppressWarnings("unused")
public class NotificationRule {

    private int id = -1;
    private SimpleDatePattern datePattern = new SimpleDatePattern(new Date(), new Date(), 0, 0);
    private boolean enabled = true;


    public NotificationRule(int id, SimpleDatePattern datePattern, boolean enabled) {
        this.id = id;
        this.datePattern = datePattern;
        this.enabled = enabled;
    }

    public NotificationRule(int id, SimpleDatePattern datePattern) {
        this(id, datePattern, true);
    }


    public void setDatePattern(SimpleDatePattern dp) {
        this.datePattern = dp;
    }

    public SimpleDatePattern getDatePattern() {
        return this.datePattern;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        NotificationRule that = (NotificationRule) o;

        if (id != that.id) {
            return false;
        }
        if (enabled != that.enabled) {
            return false;
        }
        return datePattern != null ? datePattern.equals(that.datePattern) : that.datePattern ==
                null;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (datePattern != null ? datePattern.hashCode() : 0);
        result = 31 * result + (enabled ? 1 : 0);
        return result;
    }
}
