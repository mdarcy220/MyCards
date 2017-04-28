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

import java.io.Serializable;
import java.util.Date;

/**
 * A "pattern", similar to regex for dates.
 */
@SuppressWarnings("unused")
public interface DatePattern extends Serializable {
    /**
     * @return <code>true</code> if the given date matches the pattern, <code>false</code> otherwise
     */
    boolean dateMatches(Date d);
}
