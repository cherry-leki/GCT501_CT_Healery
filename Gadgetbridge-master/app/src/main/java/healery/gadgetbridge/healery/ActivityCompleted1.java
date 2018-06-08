package healery.gadgetbridge.healery;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import healery.gadgetbridge.R;

public class ActivityCompleted1 extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.healery_activity_completed1);
        TextView txtview_before = (TextView) findViewById(R.id.beforeStateTextView);
        SharedPreferences actvt = getSharedPreferences("activity",0);
        String beforeStateStr = actvt.getString("beforeStressState","");
        txtview_before.setText(beforeStateStr);

        int beforeState=-1;
        if (beforeStateStr.equals(getResources().getString(R.string.stress_high))) beforeState=0;
        else if (beforeStateStr.equals(getResources().getString(R.string.stress_one_spoon))) beforeState = 1;
        else if (beforeStateStr.equals(getResources().getString(R.string.stress_high))) beforeState=2;

        /*현재 스트레스 상태 여기에!*/
        int nowState = 0;
        TextView txtview_now = (TextView)findViewById(R.id.nowStateTextView);

        TextView txtview = (TextView) findViewById(R.id.resultTextView);
        Button btn_complete = (Button) findViewById(R.id.button_complete);
        Button btn_yes = (Button) findViewById(R.id.button_yes);
        Button btn_no = (Button)findViewById(R.id.button_no);

        SharedPreferences setting = getSharedPreferences("setting", 0);
        SharedPreferences.Editor editor = setting.edit();
        DetailString dstr = new DetailString(setting);
        String actvtName = actvt.getString("select","");
        int cat = dstr.getCategoryFromString(actvtName);
        String weightname = "weight"+String.valueOf(cat);
        int weightvalue = setting.getInt(weightname,DetailString.defaultWeight);
        if (beforeState < nowState) {
            txtview.setText(getResources().getString(R.string.completed_good));
            btn_yes.setVisibility(View.INVISIBLE);
            btn_no.setVisibility(View.INVISIBLE);
            btn_complete.setVisibility(View.VISIBLE);
            if (getIntent().getBooleanExtra("complete",false))editor.putInt(weightname,weightvalue+1);
        }
        else {
            txtview.setText(getResources().getString(R.string.completed_bad));
            btn_yes.setVisibility(View.VISIBLE);
            btn_no.setVisibility(View.VISIBLE);
            btn_complete.setVisibility(View.INVISIBLE);
            if (weightvalue>1 && getIntent().getBooleanExtra("complete",false)) editor.putInt(weightname,weightvalue-1);
        }
        editor.commit();
        editor = actvt.edit();
        editor.clear();
        editor.commit();
        SharedPreferences report = getSharedPreferences("report", 0);
        editor = report.edit();
        int reportN = report.getInt("N", 0);
        editor.putString("activity"+String.valueOf(reportN),actvtName);
        editor.putInt("category"+String.valueOf(reportN),cat);
        editor.putInt("beforeStressState"+String.valueOf(reportN),beforeState);
        editor.putInt("nowStressState"+String.valueOf(reportN),nowState);
        editor.putLong("time"+String.valueOf(reportN),System.currentTimeMillis());
        editor.commit();
        btn_complete.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                finish();
                return;
            }
        });
        btn_yes.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                SharedPreferences actvt = getSharedPreferences("activity",0);
                SharedPreferences.Editor editor = actvt.edit();
                editor.putString("beforeStressState",((TextView)findViewById(R.id.nowStateTextView)).getText().toString());
                editor.commit();
                Intent intent = new Intent(ActivityCompleted1.this, ActivityRecommend.class);
                startActivity(intent);
                finish();
                return;
            }
        });
        btn_no.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                finish();
                return;
            }
        });
    }
}
