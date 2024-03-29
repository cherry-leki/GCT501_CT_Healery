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
package healery.gadgetbridge.service.devices.amazfitbip;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.net.Uri;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import healery.gadgetbridge.service.devices.amazfitbip.operations.AmazfitBipFetchLogsOperation;
import healery.gadgetbridge.GBApplication;
import healery.gadgetbridge.devices.huami.HuamiFWHelper;
import healery.gadgetbridge.devices.huami.amazfitbip.AmazfitBipFWHelper;
import healery.gadgetbridge.devices.huami.amazfitbip.AmazfitBipService;
import healery.gadgetbridge.devices.miband.MiBand2Service;
import healery.gadgetbridge.model.CallSpec;
import healery.gadgetbridge.model.DeviceType;
import healery.gadgetbridge.model.NotificationSpec;
import healery.gadgetbridge.model.NotificationType;
import healery.gadgetbridge.model.RecordedDataTypes;
import healery.gadgetbridge.service.btle.TransactionBuilder;
import healery.gadgetbridge.service.btle.actions.ConditionalWriteAction;
import healery.gadgetbridge.service.btle.profiles.alertnotification.AlertCategory;
import healery.gadgetbridge.service.btle.profiles.alertnotification.AlertNotificationProfile;
import healery.gadgetbridge.service.btle.profiles.alertnotification.NewAlert;
import healery.gadgetbridge.service.devices.amazfitbip.operations.AmazfitBipFetchLogsOperation;
import healery.gadgetbridge.service.devices.huami.HuamiIcon;
import healery.gadgetbridge.service.devices.miband.NotificationStrategy;
import healery.gadgetbridge.service.devices.miband2.MiBand2Support;
import healery.gadgetbridge.service.devices.miband2.operations.FetchActivityOperation;
import healery.gadgetbridge.service.devices.miband2.operations.FetchSportsSummaryOperation;
import healery.gadgetbridge.util.Prefs;
import healery.gadgetbridge.util.StringUtils;
import healery.gadgetbridge.util.Version;

public class AmazfitBipSupport extends MiBand2Support {

    private static final Logger LOG = LoggerFactory.getLogger(AmazfitBipSupport.class);

    public AmazfitBipSupport() {
        super(LOG);
    }

    @Override
    public NotificationStrategy getNotificationStrategy() {
        return new AmazfitBipTextNotificationStrategy(this);
    }

    @Override
    public void onNotification(NotificationSpec notificationSpec) {
        if (notificationSpec.type == NotificationType.GENERIC_ALARM_CLOCK) {
            onAlarmClock(notificationSpec);
            return;
        }

        String senderOrTiltle = StringUtils.getFirstOf(notificationSpec.sender, notificationSpec.title);

        String message = StringUtils.truncate(senderOrTiltle, 32) + "\0";
        if (notificationSpec.subject != null) {
            message += StringUtils.truncate(notificationSpec.subject, 128) + "\n\n";
        }
        if (notificationSpec.body != null) {
            message += StringUtils.truncate(notificationSpec.body, 128);
        }

        try {
            TransactionBuilder builder = performInitialized("new notification");
            AlertNotificationProfile<?> profile = new AlertNotificationProfile(this);
            profile.setMaxLength(230);

            byte customIconId = HuamiIcon.mapToIconId(notificationSpec.type);

            AlertCategory alertCategory = AlertCategory.CustomHuami;

            // The SMS icon for AlertCategory.SMS is unique and not available as iconId
            if (notificationSpec.type == NotificationType.GENERIC_SMS) {
                alertCategory = AlertCategory.SMS;
            }
            // EMAIL icon does not work in FW 0.0.8.74, it did in 0.0.7.90
            else if (customIconId == HuamiIcon.EMAIL) {
                alertCategory = AlertCategory.Email;
            }

            NewAlert alert = new NewAlert(alertCategory, 1, message, customIconId);
            profile.newAlert(builder, alert);
            builder.queue(getQueue());
        } catch (IOException ex) {
            LOG.error("Unable to send notification to Amazfit Bip", ex);
        }
    }

    @Override
    public void onFindDevice(boolean start) {
        CallSpec callSpec = new CallSpec();
        callSpec.command = start ? CallSpec.CALL_INCOMING : CallSpec.CALL_END;
        callSpec.name = "Gadgetbridge";
        onSetCallState(callSpec);
    }

    @Override
    public void handleButtonEvent() {
        // ignore
    }

    @Override
    protected AmazfitBipSupport setDisplayItems(TransactionBuilder builder) {
        if (gbDevice.getType() != DeviceType.AMAZFITBIP) {
            return this; // Disable for Cor for now
        }
        if (gbDevice.getFirmwareVersion() == null) {
            LOG.warn("Device not initialized yet, won't set menu items");
            return this;
        }

        Version version = new Version(gbDevice.getFirmwareVersion());
        if (version.compareTo(new Version("0.1.1.14")) < 0) {
            LOG.warn("Won't set menu items since firmware is too low to be safe");
            return this;
        }

        Prefs prefs = GBApplication.getPrefs();
        Set<String> pages = prefs.getStringSet("bip_display_items", null);
        LOG.info("Setting display items to " + (pages == null ? "none" : pages));
        byte[] command = AmazfitBipService.COMMAND_CHANGE_SCREENS.clone();

        boolean shortcut_weather = false;
        boolean shortcut_alipay = false;

        if (pages != null) {
            if (pages.contains("status")) {
                command[1] |= 0x02;
            }
            if (pages.contains("activity")) {
                command[1] |= 0x04;
            }
            if (pages.contains("weather")) {
                command[1] |= 0x08;
            }
            if (pages.contains("alarm")) {
                command[1] |= 0x10;
            }
            if (pages.contains("timer")) {
                command[1] |= 0x20;
            }
            if (pages.contains("compass")) {
                command[1] |= 0x40;
            }
            if (pages.contains("settings")) {
                command[1] |= 0x80;
            }
            if (pages.contains("alipay")) {
                command[2] |= 0x01;
            }
            if (pages.contains("shortcut_weather")) {
                shortcut_weather = true;
            }
            if (pages.contains("shortcut_alipay")) {
                shortcut_alipay = true;
            }
        }
        builder.write(getCharacteristic(MiBand2Service.UUID_CHARACTERISTIC_3_CONFIGURATION), command);
        setShortcuts(builder, shortcut_weather, shortcut_alipay);

        return this;
    }

    private void setShortcuts(TransactionBuilder builder, boolean weather, boolean alipay) {
        LOG.info("Setting shortcuts: weather=" + weather + " alipay=" + alipay);

        // Basically a hack to put weather first always, if alipay is the only enabled one
        // there are actually two alipays set but the second one disabled.... :P
        byte[] command = new byte[]{0x10,
                (byte) ((alipay || weather) ? 0x80 : 0x00), (byte) (weather ? 0x02 : 0x01),
                (byte) ((alipay && weather) ? 0x81 : 0x01), 0x01,
        };

        builder.write(getCharacteristic(MiBand2Service.UUID_CHARACTERISTIC_3_CONFIGURATION), command);
    }

    @Override
    public void onFetchRecordedData(int dataTypes) {
        try {
            // FIXME: currently only one data type supported, these are meant to be flags
            if (dataTypes == RecordedDataTypes.TYPE_ACTIVITY) {
                new FetchActivityOperation(this).perform();
            } else if (dataTypes == RecordedDataTypes.TYPE_GPS_TRACKS) {
                new FetchSportsSummaryOperation(this).perform();
            } else if (dataTypes == RecordedDataTypes.TYPE_DEBUGLOGS) {
                new AmazfitBipFetchLogsOperation(this).perform();
            }
            else {
                LOG.warn("fetching multiple data types at once is not supported yet");
            }
        } catch (IOException ex) {
            LOG.error("Unable to fetch recorded data types" + dataTypes, ex);
        }
    }

    @Override
    public boolean onCharacteristicChanged(BluetoothGatt gatt,
                                           BluetoothGattCharacteristic characteristic) {
        boolean handled = super.onCharacteristicChanged(gatt, characteristic);
        if (!handled) {
            UUID characteristicUUID = characteristic.getUuid();
            if (MiBand2Service.UUID_CHARACTERISTIC_3_CONFIGURATION.equals(characteristicUUID)) {
                return handleConfigurationInfo(characteristic.getValue());
            }
        }
        return false;
    }

    private boolean handleConfigurationInfo(byte[] value) {
        if (value == null || value.length < 4) {
            return false;
        }
        if (value[0] == 0x10 && value[1] == 0x0e && value[2] == 0x01) {
            String gpsVersion = new String(value, 3, value.length - 3);
            LOG.info("got gps version = " + gpsVersion);
            gbDevice.setFirmwareVersion2(gpsVersion);
            return true;
        }
        return false;
    }

    // this probably does more than only getting the GPS version...
    private AmazfitBipSupport requestGPSVersion(TransactionBuilder builder) {
        LOG.info("Requesting GPS version");
        builder.write(getCharacteristic(MiBand2Service.UUID_CHARACTERISTIC_3_CONFIGURATION), AmazfitBipService.COMMAND_REQUEST_GPS_VERSION);
        return this;
    }

    private AmazfitBipSupport setLanguage(TransactionBuilder builder) {

        String language = Locale.getDefault().getLanguage();
        String country = Locale.getDefault().getCountry();

        LOG.info("Setting watch language, phone language = " + language + " country = " + country);

        final byte[] command_new;
        final byte[] command_old;
        String localeString;

        switch (GBApplication.getPrefs().getInt("amazfitbip_language", -1)) {
            case 0:
                command_old = AmazfitBipService.COMMAND_SET_LANGUAGE_SIMPLIFIED_CHINESE;
                localeString = "zh_CN";
                break;
            case 1:
                command_old = AmazfitBipService.COMMAND_SET_LANGUAGE_TRADITIONAL_CHINESE;
                localeString = "zh_TW";
                break;
            case 2:
                command_old = AmazfitBipService.COMMAND_SET_LANGUAGE_ENGLISH;
                localeString = "en_US";
                break;
            case 3:
                command_old = AmazfitBipService.COMMAND_SET_LANGUAGE_SPANISH;
                localeString = "es_ES";
                break;
            default:
                switch (language) {
                    case "zh":
                        if (country.equals("TW") || country.equals("HK") || country.equals("MO")) { // Taiwan, Hong Kong,  Macao
                            command_old = AmazfitBipService.COMMAND_SET_LANGUAGE_TRADITIONAL_CHINESE;
                            localeString = "zh_TW";
                        } else {
                            command_old = AmazfitBipService.COMMAND_SET_LANGUAGE_SIMPLIFIED_CHINESE;
                            localeString = "zh_CN";
                        }
                        break;
                    case "es":
                        command_old = AmazfitBipService.COMMAND_SET_LANGUAGE_SPANISH;
                        localeString = "es_ES";
                        break;
                    default:
                        command_old = AmazfitBipService.COMMAND_SET_LANGUAGE_ENGLISH;
                        localeString = "en_US";
                        break;
                }
        }
        command_new = AmazfitBipService.COMMAND_SET_LANGUAGE_NEW_TEMPLATE;
        System.arraycopy(localeString.getBytes(), 0, command_new, 3, localeString.getBytes().length);

        builder.add(new ConditionalWriteAction(getCharacteristic(MiBand2Service.UUID_CHARACTERISTIC_3_CONFIGURATION)) {
            @Override
            protected byte[] checkCondition() {
                if (gbDevice.getType() == DeviceType.AMAZFITBIP && new Version(gbDevice.getFirmwareVersion()).compareTo(new Version("0.1.0.77")) >= 0) {
                    return command_new;
                } else {
                    return command_old;
                }
            }
        });

        return this;
    }


    @Override
    public void phase2Initialize(TransactionBuilder builder) {
        super.phase2Initialize(builder);
        LOG.info("phase2Initialize...");
        setLanguage(builder);
        requestGPSVersion(builder);
    }

    @Override
    public HuamiFWHelper createFWHelper(Uri uri, Context context) throws IOException {
        return new AmazfitBipFWHelper(uri, context);
    }
}
