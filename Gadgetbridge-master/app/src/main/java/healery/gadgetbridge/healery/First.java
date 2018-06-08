package healery.gadgetbridge.healery;

import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

import healery.gadgetbridge.R;

public class First extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences setting = getSharedPreferences("setting", 0);

        Intent intent = getIntent();
        if (setting.contains("setting1") && intent.hasExtra("frommain")==false){
            SharedPreferences actvt = getSharedPreferences("activity", 0);
            intent = new Intent(First.this, ActivityMainNavi.class);
            startActivity(intent);
            if(actvt.contains("select")){
                intent = new Intent(First.this, ActivityPerform.class);
                startActivity(intent);
            }
            finish();
            return;
        }
        setContentView(R.layout.healery_activity_first);
        GridView gridView = (GridView) findViewById(R.id.category_gridview);
        ArrayList<String> items = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.category)));


        if (setting.contains("setting1")){
            String tmp = "";
            for(int i=0;i<items.size();i++) tmp = tmp+"0";
            gridView.setAdapter(new GridAdapter(items, setting.getString("category", tmp)));
            EditText nameEditText = (EditText) findViewById(R.id.nameEditText);
            RadioButton maleRadioButton = (RadioButton) findViewById(R.id.maleRadioButton);
            RadioButton femaleRadioButton = (RadioButton) findViewById(R.id.femaleRadioButton);
            EditText ageEditText = (EditText) findViewById(R.id.ageEditText);
            nameEditText.setText(setting.getString("name", ""));
            maleRadioButton.setChecked(setting.getBoolean("male",false));
            femaleRadioButton.setChecked(setting.getBoolean("female",false));
            ageEditText.setText(String.valueOf(setting.getInt("age",-1)));
            if (ageEditText.getText().toString()=="-1") ageEditText.setText("");
        }
        else gridView.setAdapter(new GridAdapter(items));


        Button btn_complete = (Button) findViewById(R.id.settingCompleteButton);
        btn_complete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                boolean male = ((RadioButton)findViewById(R.id.maleRadioButton)).isChecked();
                boolean female = ((RadioButton)findViewById(R.id.femaleRadioButton)).isChecked();
                if (!male && !female){
                    Toast.makeText(getApplicationContext(),"성별을 체크해주세요.",Toast.LENGTH_LONG).show();
                    return;
                }
                boolean flag=true;
                String age = ((EditText)findViewById(R.id.ageEditText)).getText().toString();
                if (age.length()==0) flag=false;
                if(flag==false){
                    Toast.makeText(getApplicationContext(),"나이를 제대로 입력해주세요.", Toast.LENGTH_LONG).show();
                    return;
                }
                SharedPreferences setting = getSharedPreferences("setting", 0);
                SharedPreferences.Editor editor = setting.edit();
                GridView gridView = (GridView)findViewById(R.id.category_gridview);
                String categorySetting="";
                for(int i=0;i<gridView.getCount();i++){
                    View v = gridView.getChildAt(i);
                    if(((CheckBox)v.findViewById(R.id.category_checkBox)).isChecked()) categorySetting += "1";
                    else categorySetting += "0";
                }
                editor.putBoolean("setting1", true);
                editor.putString("category",categorySetting);
                editor.putBoolean("male", male);
                editor.putBoolean("female",female);
                editor.putString("name",((EditText)findViewById(R.id.nameEditText)).getText().toString());
                editor.putInt("age",Integer.parseInt(age));
                for (int i=0;i<DetailString.n;i++){
                    editor.putInt("weight"+String.valueOf(i),DetailString.defaultWeight);
                }
                editor.commit();
                if (getIntent().hasExtra("frommain")==false) {
                    Intent intent = new Intent(First.this, ActivityMainNavi.class);
                    startActivity(intent);
                }
                finish();
                return;
            }
        });
        /*ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_multiple_choice, items);
        gridView.setAdapter(adapter);*/
    }

    private class GridAdapter extends BaseAdapter {
        ArrayList <String> cItems;
        ArrayList <Boolean> cCheck;
        int cCount;

        public GridAdapter(ArrayList<String> items){
            cCount = items.size();
            cItems = new ArrayList<String>(items);
            cCheck = new ArrayList<Boolean>();
            for (int i=0;i<cCount;i++){
                cCheck.add(false);
            }
        }
        public GridAdapter(ArrayList<String> items, String initialcheck){
            cCount = items.size();
            cItems = new ArrayList<String>(items);
            cCheck = new ArrayList<Boolean>();
            for (int i=0;i<cCount;i++){
                if(initialcheck.charAt(i)=='1') cCheck.add(true);
                else cCheck.add(false);
            }
        }
        public int getCount(){
            return cCount;
        }
        public Object getItem(int position){
            return cItems.get(position);
        }
        public long getItemId(int position){
            return position;
        }
        public View getView(int position, View convertView, ViewGroup parent){
            View view = convertView;

            /*if (view==null) view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_multiple_choice, parent, false);
            TextView txt = (TextView)view.findViewById(android.R.id.text1);
            txt.setText(cItems.get(position));*/
            if (view==null) view = LayoutInflater.from(parent.getContext()).inflate(R.layout.healery_category_gridview_item, parent, false);
            CheckBox chk = (CheckBox)view.findViewById(R.id.category_checkBox);
            chk.setText(cItems.get(position));
            chk.setChecked(cCheck.get(position));
            return view;
        }

    }
}
