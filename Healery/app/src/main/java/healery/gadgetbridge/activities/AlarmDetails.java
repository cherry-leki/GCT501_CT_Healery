/*  Copyright (C) 2015-2018 Andreas Shimokawa, Carsten Pfeiffer, Daniele
    Gobbetti, Lem Dulfo

    This file is part of Gadgetbridge.

    Gadgetbridge is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Gadgetbridge is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>. */
package healery.gadgetbridge.activities;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.TimePicker;

import healery.gadgetbridge.GBApplication;
import healery.gadgetbridge.R;
import healery.gadgetbridge.devices.DeviceCoordinator;
import healery.gadgetbridge.impl.GBAlarm;
import healery.gadgetbridge.impl.GBDevice;
import healery.gadgetbridge.util.DeviceHelper;

public class AlarmDetails extends AbstractGBActivity {

    private GBAlarm alarm;
    private TimePicker timePicker;
    private CheckedTextView cbSmartWakeup;
    private CheckedTextView cbMonday;
    private CheckedTextView cbTuesday;
    private CheckedTextView cbWednesday;
    private CheckedTextView cbThursday;
    private CheckedTextView cbFriday;
    private CheckedTextView cbSaturday;
    private CheckedTextView cbSunday;
    private GBDevice device;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_details);

        alarm = getIntent().getParcelableExtra("alarm");
        device = getIntent().getParcelableExtra(GBDevice.EXTRA_DEVICE);

        timePicker = (TimePicker) findViewById(R.id.alarm_time_picker);
        cbSmartWakeup = (CheckedTextView) findViewById(R.id.alarm_cb_smart_wakeup);
        cbMonday = (CheckedTextView) findViewById(R.id.alarm_cb_monday);
        cbTuesday = (CheckedTextView) findViewById(R.id.alarm_cb_tuesday);
        cbWednesday = (CheckedTextView) findViewById(R.id.alarm_cb_wednesday);
        cbThursday = (CheckedTextView) findViewById(R.id.alarm_cb_thursday);
        cbFriday = (CheckedTextView) findViewById(R.id.alarm_cb_friday);
        cbSaturday = (CheckedTextView) findViewById(R.id.alarm_cb_saturday);
        cbSunday = (CheckedTextView) findViewById(R.id.alarm_cb_sunday);


        cbSmartWakeup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((CheckedTextView) v).toggle();
            }
        });
        cbMonday.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((CheckedTextView) v).toggle();
            }
        });
        cbTuesday.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((CheckedTextView) v).toggle();
            }
        });
        cbWednesday.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((CheckedTextView) v).toggle();
            }
        });
        cbThursday.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((CheckedTextView) v).toggle();
            }
        });
        cbFriday.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((CheckedTextView) v).toggle();
            }
        });
        cbSaturday.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((CheckedTextView) v).toggle();
            }
        });
        cbSunday.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((CheckedTextView) v).toggle();
            }
        });

        timePicker.setIs24HourView(DateFormat.is24HourFormat(GBApplication.getContext()));
        timePicker.setCurrentHour(alarm.getHour());
        timePicker.setCurrentMinute(alarm.getMinute());

        cbSmartWakeup.setChecked(alarm.isSmartWakeup());
        int smartAlarmVisibility = supportsSmartWakeup() ? View.VISIBLE : View.GONE;
        cbSmartWakeup.setVisibility(smartAlarmVisibility);

        cbMonday.setChecked(alarm.getRepetition(GBAlarm.ALARM_MON));
        cbTuesday.setChecked(alarm.getRepetition(GBAlarm.ALARM_TUE));
        cbWednesday.setChecked(alarm.getRepetition(GBAlarm.ALARM_WED));
        cbThursday.setChecked(alarm.getRepetition(GBAlarm.ALARM_THU));
        cbFriday.setChecked(alarm.getRepetition(GBAlarm.ALARM_FRI));
        cbSaturday.setChecked(alarm.getRepetition(GBAlarm.ALARM_SAT));
        cbSunday.setChecked(alarm.getRepetition(GBAlarm.ALARM_SUN));

    }

    private boolean supportsSmartWakeup() {
        if (device != null) {
            DeviceCoordinator coordinator = DeviceHelper.getInstance().getCoordinator(device);
            return coordinator.supportsSmartWakeup(device);
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // back button
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateAlarm() {
        alarm.setSmartWakeup(supportsSmartWakeup() && cbSmartWakeup.isChecked());
        alarm.setRepetition(cbMonday.isChecked(), cbTuesday.isChecked(), cbWednesday.isChecked(), cbThursday.isChecked(), cbFriday.isChecked(), cbSaturday.isChecked(), cbSunday.isChecked());
        alarm.setHour(timePicker.getCurrentHour());
        alarm.setMinute(timePicker.getCurrentMinute());
        alarm.store();
    }

    @Override
    protected void onPause() {
        updateAlarm();
        super.onPause();
    }
}
