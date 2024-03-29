package kr.z33hyo.healery_test;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ActivityPerform extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perform);
        TextView textView = (TextView) findViewById(R.id.selectedTextView);
        //Intent intent = getIntent();
        //textView.setText(intent.getStringExtra("select"));
        SharedPreferences actvt = getSharedPreferences("activity",0);
        textView.setText(actvt.getString("select",""));
        Button btn_complete = (Button) findViewById(R.id.performCompleteButton);
        Button btn_stop = (Button) findViewById(R.id.performStopButton);
        btn_complete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(ActivityPerform.this, ActivityCompleted1.class);
                intent.putExtra("complete", true);
                startActivity(intent);
                finish();
                return;
            }
        });
        btn_stop.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(ActivityPerform.this, ActivityCompleted1.class);
                intent.putExtra("complete", false);
                startActivity(intent);
                finish();
                return;
            }
        });
    }
    @Override
    public void onBackPressed(){
        /*SharedPreferences actvt = getSharedPreferences("activity", 0);
        SharedPreferences.Editor editor = actvt.edit();
        editor.clear();
        editor.commit();
        super.onBackPressed();*/
    }
}
