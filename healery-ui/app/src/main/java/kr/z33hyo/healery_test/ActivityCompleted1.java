package kr.z33hyo.healery_test;

import android.app.Activity;
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

        TextView txtview = (TextView) findViewById(R.id.textView11);
        Button buttonComplete = (Button) findViewById(R.id.button_complete);
        Button buttonYes = (Button) findViewById(R.id.button_yes);
        Button buttonNo = (Button)findViewById(R.id.button_no);
        if (false) {
            txtview.setText(getResources().getString(R.string.completed_good));
            buttonYes.setVisibility(View.INVISIBLE);
            buttonNo.setVisibility(View.INVISIBLE);
            buttonComplete.setVisibility(View.VISIBLE);
        }
        else{
            txtview.setText(getResources().getString(R.string.completed_bad));
            buttonYes.setVisibility(View.VISIBLE);
            buttonNo.setVisibility(View.VISIBLE);
            buttonComplete.setVisibility(View.INVISIBLE);
        }
    }
}
