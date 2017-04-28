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
 * A basic implementation of the <code>DatePattern</code> interface, using a simple pattern of
 * start and end dates. Any dates between the start and end dates are considered matches, and all
 * others are not matches.
 */
class SimpleDatePattern implements DatePattern {

    private static final long serialVersionUID = 421290834701L;

    private Date start;
    private Date end;
    private long repeatInterval = -1;
    private int numRepeats = 0;

    public SimpleDatePattern(Date start, Date end, long repeatInterval, int numRepeats) {
        this.start = start;
        this.end = end;
        this.repeatInterval = repeatInterval;
        this.numRepeats = numRepeats;

        if(this.end.getTime() < this.start.getTime()) {
            Date tmp = this.end;
            this.end = start;
            this.start = tmp;
        }
    }

    @Override
    public boolean dateMatches(Date d) {
        if (d == null || start == null || end == null) {
            return false;
        }

        long neededRepeats = 0;
        if (0 < repeatInterval) {
            neededRepeats = (d.getTime() - start.getTime()) / repeatInterval;
        }
        if (numRepeats < neededRepeats) {
            neededRepeats = numRepeats;
        }

        long trueStart = start.getTime() + neededRepeats * repeatInterval;
        long trueEnd = end.getTime() + neededRepeats * repeatInterval;

        return (trueStart <= d.getTime() && d.getTime() <= trueEnd);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SimpleDatePattern that = (SimpleDatePattern) o;

        if (repeatInterval != that.repeatInterval) {
            return false;
        }
        if (numRepeats != that.numRepeats) {
            return false;
        }
        if (start != null ? !start.equals(that.start) : that.start != null) {
            return false;
        }
        return end != null ? end.equals(that.end) : that.end == null;

    }

    @Override
    public int hashCode() {
        int result = start != null ? start.hashCode() : 0;
        result = 31 * result + (end != null ? end.hashCode() : 0);
        result = 31 * result + (int) (repeatInterval ^ (repeatInterval >>> 32));
        result = 31 * result + numRepeats;
        return result;
    }

    /**
     * Gets the next matching date after the given <code>Date</code>. If the <code>Date</code>
     * given is null, the current date/time is used instead.
     *
     * @param startDate the date to start from when searching for the next match.
     * @return a <code>Date</code> indicating the next matching date
     */
    public Date nextMatch(Date startDate) {
        if (startDate == null) {
            startDate = new Date();
        }
        if (this.dateMatches(startDate)) {
            return startDate;
        }

        long neededRepeats;
        if ((startDate.getTime() - start.getTime()) <= 0) {
            neededRepeats = 0;
        } else if (0 < repeatInterval) {
            neededRepeats = (startDate.getTime() - start.getTime()) / repeatInterval + 1;
        } else {
            return null;
        }

        if(numRepeats < neededRepeats) {
            return null;
        }

        return new Date(this.start.getTime() + repeatInterval * neededRepeats);
    }

    public Date getEndDate() {
        return end;
    }

    public Date getStartDate() {
        return start;
    }

    public long getRepeatInterval() {
        return repeatInterval;
    }

    public int getNumRepeats() {
        return numRepeats;
    }
}
