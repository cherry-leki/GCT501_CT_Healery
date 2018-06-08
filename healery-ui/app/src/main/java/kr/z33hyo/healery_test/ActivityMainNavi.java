package kr.z33hyo.healery_test;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ActivityMainNavi extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_navi);
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


        TextView txtview_state = (TextView)findViewById(R.id.stressStateTextView);
        ImageView imgview_state = (ImageView)findViewById(R.id.stressStateImage);
        TextView txtview_go = (TextView)findViewById(R.id.goRecommendTextView);
        if (true){ //높은 스트레스
            txtview_state.setText(getResources().getString(R.string.stress_high));
            txtview_go.setText(getResources().getString(R.string.go_recommend_high));
            imgview_state.setImageDrawable(getResources().getDrawable(R.drawable.stress_high));
        }
        else if(false){ //한스푼 스트레스
            txtview_state.setText(getResources().getString(R.string.stress_one_spoon));
            txtview_go.setText(getResources().getString(R.string.go_recommend_one_spoon));
            imgview_state.setImageDrawable(getResources().getDrawable(R.drawable.stress_one_spoon));
        }else{ //평온
            txtview_state.setText(getResources().getString(R.string.stress_no));
            txtview_go.setText(getResources().getString(R.string.go_recommend_no));
            imgview_state.setImageDrawable(getResources().getDrawable(R.drawable.stress_no));
        }
        Button btn_recommend = (Button) findViewById(R.id.recommendButton);
        btn_recommend.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(ActivityMainNavi.this, ActivityRecommend.class);
                SharedPreferences actvt = getSharedPreferences("activity", 0);
                SharedPreferences.Editor editor = actvt.edit();
                editor.putString("beforeStressState", ((TextView)findViewById(R.id.stressStateTextView)).getText().toString());
                editor.commit();
                startActivity(intent);
            }
        });
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
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
