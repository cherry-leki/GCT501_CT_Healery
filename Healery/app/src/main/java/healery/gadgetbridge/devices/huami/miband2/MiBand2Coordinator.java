/*  Copyright (C) 2016-2018 Andreas Shimokawa, Carsten Pfeiffer, José Rebelo

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
package healery.gadgetbridge.devices.huami.miband2;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import healery.gadgetbridge.devices.miband.MiBand2Service;
import healery.gadgetbridge.devices.miband.MiBandConst;
import healery.gadgetbridge.devices.InstallHandler;
import healery.gadgetbridge.devices.huami.HuamiCoordinator;
import healery.gadgetbridge.impl.GBDevice;
import healery.gadgetbridge.impl.GBDeviceCandidate;
import healery.gadgetbridge.model.DeviceType;

public class MiBand2Coordinator extends HuamiCoordinator {
    private static final Logger LOG = LoggerFactory.getLogger(MiBand2Coordinator.class);

    @Override
    public DeviceType getDeviceType() {
        return DeviceType.MIBAND2;
    }

    @NonNull
    @Override
    public DeviceType getSupportedType(GBDeviceCandidate candidate) {
        if (candidate.supportsService(MiBand2Service.UUID_SERVICE_MIBAND2_SERVICE)) {
            return DeviceType.MIBAND2;
        }

        // and a heuristic for now
        try {
            BluetoothDevice device = candidate.getDevice();
//            if (isHealthWearable(device)) {
            String name = device.getName();
            if (name != null && name.equalsIgnoreCase(MiBandConst.MI_BAND2_NAME)) {
                return DeviceType.MIBAND2;
            }
//            }
        } catch (Exception ex) {
            LOG.error("unable to check device support", ex);
        }
        return DeviceType.UNKNOWN;

    }

    @Override
    public InstallHandler findInstallHandler(Uri uri, Context context) {
        MiBand2FWInstallHandler handler = new MiBand2FWInstallHandler(uri, context);
        return handler.isValid() ? handler : null;
    }

    @Override
    public boolean supportsHeartRateMeasurement(GBDevice device) {
        return true;
    }

    @Override
    public boolean supportsWeather() {
        return false;
    }
}
