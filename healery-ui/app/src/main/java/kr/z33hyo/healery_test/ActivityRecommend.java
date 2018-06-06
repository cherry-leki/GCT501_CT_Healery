package kr.z33hyo.healery_test;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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
            }
        });
        //다른 추천-->구현해야함!!
        Button btn0 = (Button)findViewById(R.id.recommendButton0);
        Button btn1 = (Button)findViewById(R.id.recommendButton1);
        Button btn2 = (Button)findViewById(R.id.recommendButton2);
        View.OnClickListener recommendListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityRecommend.this, ActivityPerform.class);
                intent.putExtra("select",((Button)view).getText().toString());
                startActivity(intent);
                finish();
            }
        };
        btn0.setOnClickListener(recommendListener);
        btn1.setOnClickListener(recommendListener);
        btn2.setOnClickListener(recommendListener);
    }
}
