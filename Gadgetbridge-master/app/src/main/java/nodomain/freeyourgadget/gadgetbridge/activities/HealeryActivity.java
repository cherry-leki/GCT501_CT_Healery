package nodomain.freeyourgadget.gadgetbridge.activities;

import android.os.Bundle;

import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;

public class HealeryActivity extends AbstractGBActivity{
    private GBDevice device;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        device = getIntent().getParcelableExtra(GBDevice.EXTRA_DEVICE);
    }

    private GBDevice getDevice() {
        return device;
    }
}
