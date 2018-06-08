package healery.gadgetbridge.activities;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.WindowManager;
import android.widget.TextView;

import com.github.mikephil.charting.data.LineDataSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import healery.gadgetbridge.entities.MiBandActivitySample;
import healery.gadgetbridge.GBApplication;
import healery.gadgetbridge.R;
import healery.gadgetbridge.entities.MiBandActivitySample;
import healery.gadgetbridge.impl.GBDevice;
import healery.gadgetbridge.model.ActivitySample;
import healery.gadgetbridge.model.DeviceService;
import healery.gadgetbridge.model.Measurement;

public class HealeryActivity extends AbstractGBActivity{
    private GBDevice device;
    private TextView show_heartRateText, show_walkText, show_stressText;
    private String[] stressDegree = {"좋음", "스트레스 한스푼", "스트레스 가득"};
    private int stressCount;

    // ------
    private static final Logger LOG = LoggerFactory.getLogger(HealeryActivity.class);
    private static final int MAX_STEPS_PER_MINUTE = 300;
    private static final int MIN_STEPS_PER_MINUTE = 60;
    private static final int RESET_COUNT = 10; // reset the max steps per minute value every 10s


    private final Steps mSteps = new Steps();
    private ScheduledExecutorService pulseScheduler;
    private int maxStepsResetCounter;
    private int mHeartRate;
    // ------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hrandwalk);

        show_heartRateText = findViewById(R.id.show_heartRateText);
        show_walkText = findViewById(R.id.show_walkText);
        show_stressText = findViewById(R.id.show_stressText);

        GBApplication.deviceService().onHeartRateTest();

        device = getIntent().getParcelableExtra(GBDevice.EXTRA_DEVICE);

        IntentFilter filterLocal = new IntentFilter();
        filterLocal.addAction(DeviceService.ACTION_REALTIME_SAMPLES);
        filterLocal.addAction(DeviceService.ACTION_HEARTRATE_MEASUREMENT);
        filterLocal.addAction(DeviceService.ACTION_ENABLE_REALTIME_HEARTRATE_MEASUREMENT);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, filterLocal);
    }

    //------
    private class Steps {
        private int steps;
        private int lastTimestamp;
        private int currentStepsPerMinute;
        private int maxStepsPerMinute;
        private int lastStepsPerMinute;

        public int getStepsPerMinute(boolean reset) {
            lastStepsPerMinute = currentStepsPerMinute;
            int result = currentStepsPerMinute;
            if (reset) {
                currentStepsPerMinute = 0;
            }
            return result;
        }

        public int getTotalSteps() {
            return steps;
        }

        public int getMaxStepsPerMinute() {
            return maxStepsPerMinute;
        }

        public void updateCurrentSteps(int stepsDelta, int timestamp) {
            try {
                if (steps == 0) {
                    steps += stepsDelta;
                    lastTimestamp = timestamp;
                    return;
                }

                int timeDelta = timestamp - lastTimestamp;
                currentStepsPerMinute = calculateStepsPerMinute(stepsDelta, timeDelta);
                if (currentStepsPerMinute > maxStepsPerMinute) {
                    maxStepsPerMinute = currentStepsPerMinute;
                    maxStepsResetCounter = 0;
                }
                steps += stepsDelta;
                lastTimestamp = timestamp;
            } catch (Exception ex) {
            }
        }

        private int calculateStepsPerMinute(int stepsDelta, int seconds) {
            if (stepsDelta == 0) {
                return 0; // not walking or not enough data per mills?
            }
            if (seconds <= 0) {
                throw new IllegalArgumentException("delta in seconds is <= 0 -- time change?");
            }

            int oneMinute = 60;
            float factor = oneMinute / seconds;
            int result = (int) (stepsDelta * factor);
            if (result > MAX_STEPS_PER_MINUTE) {
                // ignore, return previous value instead
                result = lastStepsPerMinute;
            }
            return result;
        }
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case DeviceService.ACTION_REALTIME_SAMPLES: {
                    MiBandActivitySample sample = (MiBandActivitySample) intent.getSerializableExtra(DeviceService.EXTRA_REALTIME_SAMPLE);
                    addSample(sample);
                    break;
                }
            }
        }
    };

    private void addSample(ActivitySample sample) {
        int heartRate = sample.getHeartRate();
        int timestamp = sample.getTimestamp();
        if (HeartRateUtils.isValidHeartRateValue(heartRate)) {
            setCurrentHeartRate(heartRate);
            show_heartRateText.setText("Heart Rate: " + heartRate);
        }

        int steps = sample.getSteps();
        if (steps != ActivitySample.NOT_MEASURED) {
            addEntries(steps, timestamp);
            show_walkText.setText("Steps: " + steps);
        }

        if(heartRate > 85) {
            if(steps < 40) stressCount++;
            if(stressCount > 4){
                stressCount = 5;
                show_stressText.setText(stressDegree[2]);
            }
        } else if(heartRate > 74){
            show_stressText.setText(stressDegree[1]);
        } else if (heartRate < 69){
            stressCount--;
            if(stressCount < 1) stressCount = 0;
        }
        if(stressCount < 2) {
            show_stressText.setText(stressDegree[0]);
        }
    }

    private void setCurrentHeartRate(int heartRate) {
        mHeartRate = heartRate;
    }

    private int getCurrentHeartRate() {
        int result = mHeartRate;
        mHeartRate = -1;
        return result;
    }

    private void addEntries(int steps, int timestamp) {
        mSteps.updateCurrentSteps(steps, timestamp);
        if (++maxStepsResetCounter > RESET_COUNT) {
            maxStepsResetCounter = 0;
            mSteps.maxStepsPerMinute = 0;
        }
        // Or: count down the steps until goal reached? And then flash GOAL REACHED -> Set stretch goal
        LOG.info("Steps: " + steps + ", total: " + mSteps.getTotalSteps() + ", current: " + mSteps.getStepsPerMinute(false));

    }

    @Override
    public void onPause() {
        enableRealtimeTracking(true);
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        enableRealtimeTracking(true);
    }

    private ScheduledExecutorService startActivityPulse() {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                pulse();
            }
        }, 0, getPulseIntervalMillis(), TimeUnit.MILLISECONDS);
        return service;
    }

    private void stopActivityPulse() {
        if (pulseScheduler != null) {
            pulseScheduler.shutdownNow();
            pulseScheduler = null;
        }
    }

    /**
     * Called in the UI thread.
     */
    private void pulse() {
        //addEntries(translateTimestamp(System.currentTimeMillis()));

        // have to enable it again and again to keep it measuring
        GBApplication.deviceService().onEnableRealtimeHeartRateMeasurement(true);
    }

    private int getPulseIntervalMillis() {
        return 1000;
    }

    private void enableRealtimeTracking(boolean enable) {
        if (enable && pulseScheduler != null) {
            // already running
            return;
        }

        GBApplication.deviceService().onEnableRealtimeSteps(enable);
        GBApplication.deviceService().onEnableRealtimeHeartRateMeasurement(enable);
        GBApplication.deviceService().onHeartRateTest();
        if (enable) {
            if (this != null) {
                this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
            pulseScheduler = startActivityPulse();
        } else {
            stopActivityPulse();
            if (this != null) {
                this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        }
    }

    //------


    private GBDevice getDevice() {
        return device;
    }
}
