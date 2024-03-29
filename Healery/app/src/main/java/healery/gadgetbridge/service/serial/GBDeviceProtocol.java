/*  Copyright (C) 2015-2018 Andreas Shimokawa, Carsten Pfeiffer, Julien
    Pivotto, Steffen Liebergeld

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
package healery.gadgetbridge.service.serial;

import java.util.UUID;

import healery.gadgetbridge.deviceevents.GBDeviceEvent;
import healery.gadgetbridge.deviceevents.GBDeviceEvent;
import healery.gadgetbridge.impl.GBDevice;
import healery.gadgetbridge.model.CalendarEventSpec;
import healery.gadgetbridge.model.CannedMessagesSpec;
import healery.gadgetbridge.model.NotificationSpec;

public abstract class GBDeviceProtocol {

    private GBDevice mDevice;

    protected GBDeviceProtocol(GBDevice device) {
        mDevice = device;
    }

    public byte[] encodeNotification(NotificationSpec notificationSpec) {
        return null;
    }

    public byte[] encodeDeleteNotification(int id) {
        return null;
    }

    public byte[] encodeSetTime() {
        return null;
    }

    public byte[] encodeSetCallState(String number, String name, int command) {
        return null;
    }

    public byte[] encodeSetCannedMessages(CannedMessagesSpec cannedMessagesSpec) {
        return null;
    }

    public byte[] encodeFirmwareVersionReq() {
        return null;
    }

    public byte[] encodeAppInfoReq() {
        return null;
    }

    public byte[] encodeScreenshotReq() {
        return null;
    }

    public byte[] encodeAppDelete(UUID uuid) {
        return null;
    }

    public byte[] encodeAppStart(UUID uuid, boolean start) {
        return null;
    }

    public byte[] encodeAppReorder(UUID[] uuids) {
        return null;
    }

    public byte[] encodeSynchronizeActivityData() {
        return null;
    }

    public byte[] encodeReboot() {
        return null;
    }

    public byte[] encodeFindDevice(boolean start) {
        return null;
    }

    public byte[] encodeEnableRealtimeSteps(boolean enable) {
        return null;
    }

    public byte[] encodeEnableHeartRateSleepSupport(boolean enable) {
        return null;
    }

    public byte[] encodeEnableRealtimeHeartRateMeasurement(boolean enable) { return null; }

    public byte[] encodeAddCalendarEvent(CalendarEventSpec calendarEventSpec) {
        return null;
    }

    public byte[] encodeDeleteCalendarEvent(byte type, long id) {
        return null;
    }

    public byte[] encodeSendConfiguration(String config) {
        return null;
    }

    public byte[] encodeTestNewFunction() { return null; }

    public GBDeviceEvent[] decodeResponse(byte[] responseData) {
        return null;
    }

    public GBDevice getDevice() {
        return mDevice;
    }
}
