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

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Locale;

/**
 * @author Mike D'Arcy.
 */

public class TimePickerFragment extends AppCompatDialogFragment implements TimePickerDialog
        .OnTimeSetListener {

    EditText editview = null;

    public TimePickerFragment() {
        super();
    }

    public void setEditView(EditText e) {
        this.editview = e;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int hourOfDay = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(), this, hourOfDay, minute, true);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        this.editview.setText(String.format(Locale.US, "%02d:%02d", hourOfDay, minute));
    }
}