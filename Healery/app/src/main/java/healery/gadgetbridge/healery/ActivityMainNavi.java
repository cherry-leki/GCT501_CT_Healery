package healery.gadgetbridge.healery;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.RemoteInput;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import healery.gadgetbridge.GBApplication;
import healery.gadgetbridge.R;
import healery.gadgetbridge.activities.ControlCenterv2;
import healery.gadgetbridge.activities.HealeryActivity;
import healery.gadgetbridge.activities.HeartRateUtils;
import healery.gadgetbridge.devices.miband.MiBand2Service;
import healery.gadgetbridge.entities.MiBandActivitySample;
import healery.gadgetbridge.model.ActivitySample;
import healery.gadgetbridge.model.DeviceService;
import healery.gadgetbridge.model.NotificationSpec;
import healery.gadgetbridge.model.NotificationType;
import healery.gadgetbridge.util.GB;

import static healery.gadgetbridge.util.GB.NOTIFICATION_CHANNEL_ID;

public class ActivityMainNavi extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView txtview_state, txtview_go;
    ImageView imgview_state;

    public static int stressCount;
    public static String stressState;

    private String gender;
    private int age;
    private int goodStressLevel;
    private int warnStressLevel;
    private int badStressLevel;

    static final String STATE_STRESS_COUNT = "stressCount";

    // ------
    private static final Logger LOG = LoggerFactory.getLogger(HealeryActivity.class);
    private static final int MAX_STEPS_PER_MINUTE = 300;
    private static final int MIN_STEPS_PER_MINUTE = 60;
    private static final int RESET_COUNT = 10; // reset the max steps per minute value every 10s


    private final Steps mSteps = new Steps();
    private ScheduledExecutorService pulseScheduler;
    private int maxStepsResetCounter;
    private int mHeartRate;

    private static final String EXTRA_REPLY = "reply";
    private static final String ACTION_REPLY
            = "healery.gadgetbridge.DebugActivity.action.reply";
    public static boolean isMissionGoing = false;
    // ------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.healery_activity_main_navi);
        // 지효언니 부분
        Toolbar toolbar = ( Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        SharedPreferences setting = getSharedPreferences("setting",0);
        View headerView = navigationView.getHeaderView(0);
        TextView txtview_header = (TextView) headerView.findViewById(R.id.naviHeaderTextView);
        txtview_header.setText(setting.getString("name","")+getResources().getString(R.string.greeting));

        setStressLevel(setting);

        txtview_state = (TextView)findViewById(R.id.stressStateTextView);
        imgview_state = (ImageView)findViewById(R.id.stressStateImage);
        txtview_go = (TextView)findViewById(R.id.goRecommendTextView);

        setStressText();

        Button btn_recommend = (Button) findViewById(R.id.recommendButton);
        btn_recommend.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(ActivityMainNavi.this, ActivityRecommend.class);
                SharedPreferences actvt = getSharedPreferences("activity", 0);
                SharedPreferences.Editor editor = actvt.edit();
                editor.putString("beforeStressState", ((TextView)findViewById(R.id.stressStateTextView)).getText().toString());
                editor.commit();
                isMissionGoing = true;
                startActivity(intent);
            }
        });


        // 내부분
        IntentFilter filterLocal = new IntentFilter();
        filterLocal.addAction(DeviceService.ACTION_REALTIME_SAMPLES);
        filterLocal.addAction(DeviceService.ACTION_ENABLE_REALTIME_HEARTRATE_MEASUREMENT);
        filterLocal.addAction(ACTION_REPLY);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, filterLocal);
    }

    private void setStressLevel(SharedPreferences setting) {
        gender = setting.getBoolean("male",true) ? "male" : "female";
        age = setting.getInt("age",0);

        int[] getConstLevel = HeartRateConst.getHeartRate(gender, age);
        goodStressLevel = getConstLevel[0];
        warnStressLevel= getConstLevel[1];
        badStressLevel = getConstLevel[2];
    }

    private void setStressText() {
        if (stressCount >= 4){ //높은 스트레스
            txtview_state.setText(getResources().getString(R.string.stress_high));
            txtview_go.setText(getResources().getString(R.string.go_recommend_high));
            imgview_state.setImageDrawable(getResources().getDrawable(R.drawable.stress_high));
            stressState = getResources().getString(R.string.stress_high);
        }
        else if(stressCount >= 2){ //한스푼 스트레스
            txtview_state.setText(getResources().getString(R.string.stress_one_spoon));
            txtview_go.setText(getResources().getString(R.string.go_recommend_one_spoon));
            imgview_state.setImageDrawable(getResources().getDrawable(R.drawable.stress_one_spoon));
            stressState = getResources().getString(R.string.stress_one_spoon);
        } else { //평온
            txtview_state.setText(getResources().getString(R.string.stress_no));
            txtview_go.setText(getResources().getString(R.string.go_recommend_no));
            imgview_state.setImageDrawable(getResources().getDrawable(R.drawable.stress_no));
            stressState = getResources().getString(R.string.stress_no);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main_navi, menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_report) {
        }/* else if (id == R.id.nav_category_setting) {

        } */else if (id == R.id.nav_setting) {
            Intent intent = new Intent(ActivityMainNavi.this, First.class);
            intent.putExtra("frommain", true);
            startActivity(intent);
        }
        else if (id==R.id.nav_initialize){
            SharedPreferences setting = getSharedPreferences("setting", 0);
            SharedPreferences actvt = getSharedPreferences("activity", 0);
            SharedPreferences report = getSharedPreferences("report", 0);
            SharedPreferences.Editor editor = setting.edit();
            editor.clear();
            editor.commit();
            editor = actvt.edit();
            editor.clear();
            editor.commit();
            editor = report.edit();
            editor.clear();
            editor.commit();
            ActivityCompat.finishAffinity(this);
            setStressLevel(setting);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
            System.out.println("Heart Rate: " + heartRate);
        }

        int steps = sample.getSteps();
        if (steps != ActivitySample.NOT_MEASURED) {
            addEntries(steps, timestamp);
            System.out.println("Steps: " + steps);
        }

        if(heartRate > badStressLevel) {
            if(steps < 40) stressCount++;
            if(stressCount > 4) stressCount = 5;

        } else if (heartRate > warnStressLevel){
            if(steps < 40) {
                if (stressCount < 2) stressCount++;
                else stressCount = 3;
            }
        } else if (heartRate < goodStressLevel){
            stressCount--;
            if(stressCount < 1) stressCount = 0;
        }

        if (stressCount > 3 && !isMissionGoing) {
            sendNotification();
        }

        setStressText();

        ControlCenterv2.getAdapter().notifyDataSetChanged();
        System.out.println("stress: "+stressCount);
    }

    private void setCurrentHeartRate(int heartRate) {
        mHeartRate = heartRate;
    }

    public int getCurrentHeartRate() {
        int result = mHeartRate;
        return result;
    }

    static public String getCurrentStressState() {
        String result = stressState;
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
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        stressCount = savedInstanceState.getInt(STATE_STRESS_COUNT);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_STRESS_COUNT, stressCount);

        super.onSaveInstanceState(outState);
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

    private void sendNotification() {
        Intent notificationIntent = new Intent(getApplicationContext(), ActivityMainNavi.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                notificationIntent, 0);

        NotificationManager nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        RemoteInput remoteInput = new RemoteInput.Builder(EXTRA_REPLY).build();

        Intent replyIntent = new Intent(ACTION_REPLY);

        PendingIntent replyPendingIntent = PendingIntent.getBroadcast(this, 0, replyIntent, 0);

        NotificationCompat.Action action =
                new NotificationCompat.Action.Builder(android.R.drawable.ic_input_add, "Reply", replyPendingIntent)
                        .addRemoteInput(remoteInput)
                        .build();

        NotificationCompat.WearableExtender wearableExtender = new NotificationCompat.WearableExtender().addAction(action);

        NotificationCompat.Builder ncomp = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("긴급! 스트레스 상황!!")
                .setContentText("스트레스 상태에 도달했어요! Healery에게 활동을 추천받아보세요!")
                .setTicker("스트레스 상태에 도달했어요! Healery에게 활동을 추천받아보세요!")
                .setSmallIcon(R.drawable.stress_high_small)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .extend(wearableExtender);

        if (nManager != null) {
            nManager.notify(0, ncomp.build());
        }
    }
    //------
}
