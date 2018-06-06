package kr.z33hyo.healery_test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ActivityCompleted1 extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed1);

        TextView txtview = (TextView) findViewById(R.id.resultTextView);
        Button btn_complete = (Button) findViewById(R.id.button_complete);
        Button btn_yes = (Button) findViewById(R.id.button_yes);
        Button btn_no = (Button)findViewById(R.id.button_no);
        if (false) {
            txtview.setText(getResources().getString(R.string.completed_good));
            btn_yes.setVisibility(View.INVISIBLE);
            btn_no.setVisibility(View.INVISIBLE);
            btn_complete.setVisibility(View.VISIBLE);
        }
        else {
            txtview.setText(getResources().getString(R.string.completed_bad));
            btn_yes.setVisibility(View.VISIBLE);
            btn_no.setVisibility(View.VISIBLE);
            btn_complete.setVisibility(View.INVISIBLE);
        }
        btn_complete.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                finish();
            }
        });
        btn_yes.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(ActivityCompleted1.this, ActivityRecommend.class);
                startActivity(intent);
                finish();
            }
        });
        btn_no.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                finish();
            }
        });
    }
}
