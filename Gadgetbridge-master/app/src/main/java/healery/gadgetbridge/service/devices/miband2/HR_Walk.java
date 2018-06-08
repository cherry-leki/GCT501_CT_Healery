package healery.gadgetbridge.service.devices.miband2;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class HR_Walk extends Service {
    public HR_Walk() {
    }

    private int count;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
