/*  Copyright (C) 2016-2018 Andreas Shimokawa, Carsten Pfeiffer

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

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.io.IOException;

import healery.gadgetbridge.devices.huami.HuamiFWHelper;
import healery.gadgetbridge.service.devices.miband2.Mi2FirmwareInfo;
import healery.gadgetbridge.devices.huami.HuamiFWHelper;
import healery.gadgetbridge.service.devices.miband2.Mi2FirmwareInfo;

public class MiBand2FWHelper extends HuamiFWHelper {

    public MiBand2FWHelper(Uri uri, Context context) throws IOException {
        super(uri, context);
    }

    @NonNull
    @Override
    protected void determineFirmwareInfo(byte[] wholeFirmwareBytes) {
        firmwareInfo = new Mi2FirmwareInfo(wholeFirmwareBytes);
        if (!firmwareInfo.isHeaderValid()) {
            throw new IllegalArgumentException("Not a Mi Band 2 firmware");
        }
    }
}
