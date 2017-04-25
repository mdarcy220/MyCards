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

import java.util.Calendar;

import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.app.DatePickerDialog;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Locale;

/**
 * @author Mike D'Arcy.
 */

public class DatePickerFragment extends AppCompatDialogFragment implements DatePickerDialog
        .OnDateSetListener {

    EditText editview = null;

    public DatePickerFragment() {
        super();
    }

    public void setEditView(EditText e) {
        this.editview = e;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        this.editview.setText(String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, day));
    }
}