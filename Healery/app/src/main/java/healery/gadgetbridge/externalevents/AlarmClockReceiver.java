/*  Copyright (C) 2017-2018 Andreas Shimokawa, Carsten Pfeiffer

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
package healery.gadgetbridge.externalevents;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import healery.gadgetbridge.GBApplication;
import healery.gadgetbridge.model.NotificationSpec;
import healery.gadgetbridge.model.NotificationType;

public class AlarmClockReceiver extends BroadcastReceiver {
    /**
     * AlarmActivity and AlarmService (when unbound) listen for this broadcast intent
     * so that other applications can snooze the alarm (after ALARM_ALERT_ACTION and before
     * ALARM_DONE_ACTION).
     */
    public static final String ALARM_SNOOZE_ACTION = "com.android.deskclock.ALARM_SNOOZE";

    /**
     * AlarmActivity and AlarmService listen for this broadcast intent so that other
     * applications can dismiss the alarm (after ALARM_ALERT_ACTION and before ALARM_DONE_ACTION).
     */
    public static final String ALARM_DISMISS_ACTION = "com.android.deskclock.ALARM_DISMISS";

    /** A public action sent by AlarmService when the alarm has started. */
    public static final String ALARM_ALERT_ACTION = "com.android.deskclock.ALARM_ALERT";

    /** A public action sent by AlarmService when the alarm has stopped for any reason. */
    public static final String ALARM_DONE_ACTION = "com.android.deskclock.ALARM_DONE";
    private int lastId;


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (ALARM_ALERT_ACTION.equals(action)) {
            sendAlarm(true);
        } else if (ALARM_DONE_ACTION.equals(action)) {
            sendAlarm(false);
        }
    }



    private synchronized void sendAlarm(boolean on) {
        dismissLastAlarm();
        if (on) {
            lastId = generateId();
            NotificationSpec spec = new NotificationSpec();
            spec.type = NotificationType.GENERIC_ALARM_CLOCK;
            spec.id = lastId;
            spec.sourceName = "ALARMCLOCKRECEIVER";
            // can we get the alarm title somehow?
            GBApplication.deviceService().onNotification(spec);
        }
    }

    private void dismissLastAlarm() {
        if (lastId != 0) {
            GBApplication.deviceService().onDeleteNotification(lastId);
            lastId = 0;
        }
    }

    private int generateId() {
        // lacks negative values, but should be sufficient
        return (int) (Math.random() * Integer.MAX_VALUE);
    }
}
