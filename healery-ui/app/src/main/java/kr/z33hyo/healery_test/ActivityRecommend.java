package kr.z33hyo.healery_test;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class ActivityRecommend extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                return;
            }
        });
        //다른 추천-->구현해야함!!

        SharedPreferences setting = getSharedPreferences("setting", 0);
        DetailString whatRecommend = new DetailString(setting);

        String[] actvt_str = whatRecommend.getRandomStrings();


        Button btn0 = (Button)findViewById(R.id.recommendButton0);
        Button btn1 = (Button)findViewById(R.id.recommendButton1);
        Button btn2 = (Button)findViewById(R.id.recommendButton2);
        btn0.setText(actvt_str[0]);
        btn1.setText(actvt_str[1]);
        btn2.setText(actvt_str[2]);
        View.OnClickListener recommendListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityRecommend.this, ActivityPerform.class);
                //intent.putExtra("select",((Button)view).getText().toString());
                SharedPreferences actvt = getSharedPreferences("activity", 0);
                SharedPreferences.Editor editor = actvt.edit();
                editor.putString("select", ((Button)view).getText().toString());
                editor.commit();
                startActivity(intent);
                finish();
                return;
            }
        };
        btn0.setOnClickListener(recommendListener);
        btn1.setOnClickListener(recommendListener);
        btn2.setOnClickListener(recommendListener);

        Button btn_other = (Button)findViewById(R.id.otherButton);
        btn_other.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(ActivityRecommend.this, ActivityRecommend.class);
                startActivity(intent);
                overridePendingTransition(0,0);
                finish();
                return;
            }
        });
    }
}
