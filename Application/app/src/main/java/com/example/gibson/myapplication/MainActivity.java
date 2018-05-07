package com.example.gibson.myapplication;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.example.gibson.myapplication.Services.DatabaseService;
import com.example.gibson.myapplication.Services.MQTT_SERVICE;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.example.gibson.myapplication.MainPageFragment.BluetoothRequestCode;

/**
 * Created by gibson on 20/03/2018.
 */

public class MainActivity extends AppCompatActivity {

  private final int NUM_PAGES = 4;
  public static MQTT_SERVICE mqtt_service;
  private static DatabaseService databaseService;
  private ViewPager viewPager;
  private ViewPagerAdapter pagerAdapter;
  private TabLayout tabLayout;
  public static RequestQueue requestQueue;
  private static Context mContext;
  public static boolean isLogin;
  static AlertDialog dialog;


  public static DatabaseService getDatabaseService() {
    return databaseService;
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_viewpager);
    databaseService = new DatabaseService(this);

    if (ContextCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_DENIED) {
        Log.v("request", "permission");
      if(Build.VERSION.SDK_INT == Build.VERSION_CODES.M)
        requestPermissions(
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                BluetoothRequestCode);

    }
    isLogin = false;

    init();
  }

  public void init() {
    mContext = this;
    JSONArray mqttArray = getDatabaseService().getMqtt();
    Log.v("mqttarray", mqttArray.toString());
    if(mqttArray != null && mqttArray.length() != 0) {
      try {
        JSONObject mqttObj = mqttArray.getJSONObject(0);
        String host = "tcp://" + mqttObj.getString("host");
        String topic = mqttObj.getString("topic");
        String username = mqttObj.getString("username");
        String password = mqttObj.getString("password");
        Log.v("host", host);

        mqtt_service = new MQTT_SERVICE(this, host, topic, username, password);
        mqtt_service.startConnect();

      } catch (JSONException e) {
        e.printStackTrace();
      }
    }

    tabLayout = findViewById(R.id.tabLayout);
    viewPager = findViewById(R.id.pager);
    pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
    viewPager.setAdapter(pagerAdapter);
    tabLayout.setupWithViewPager(viewPager);

    // Iterate over all tabs and set the custom view
    for(int i = 0; i < NUM_PAGES; i++) {
      TabLayout.Tab tab = tabLayout.getTabAt(i);
      tab.setCustomView(pagerAdapter.getTabView(i));
    }

    // Initial Cache
    Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);

    // Set up the network to use HttpURLConnection as the HTTP client.
    Network network = new BasicNetwork(new HurlStack());

    requestQueue = new RequestQueue(cache, network);
    requestQueue.start();

    databaseService.insertContact("asd", "qwe");
    Log.v("database", databaseService.getContact().toString());
    databaseService.deleteContact("asd", "qwe");
    Log.v("database", databaseService.getContact().toString());
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.toolbar_main, menu);
    return super.onCreateOptionsMenu(menu);
  }


  void requestLocationPermission() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("Request Location Permission");
    builder.setMessage("Please allow location permission or the apps will be quit");
    builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialogInterface, int i) {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                BluetoothRequestCode);
      }
    });
    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialogInterface, int i) {
        System.exit(0);
      }
    });
    AlertDialog dialog = builder.create();
    dialog.setCancelable(false);
    dialog.show();
  }


  public static void showLoading(String message) {
    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
    View view = View.inflate(mContext, R.layout.loading_view, null);
    TextView textView = view.findViewById(R.id.message);
    textView.setText(message );
    builder.setCancelable(false);
    builder.setView(view);
    dialog = builder.create();
    dialog.show();
  }

  public static void dissmissLoading() {
    dialog.dismiss();
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    switch(requestCode) {
      case BluetoothRequestCode:
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        } else {
          requestLocationPermission();
        }
        break;
    }

  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    Intent intent;
    switch (item.getItemId()) {
      case R.id.action_beacon:
        intent = new Intent(this, BeaconFragment.class);
        startActivity(intent);
        break;

      case R.id.action_setting:
        intent = new Intent(this, SettingFragment.class);
        startActivity(intent);
        break;
    }

    return super.onOptionsItemSelected(item);
  }

  public static void sendToast(String msg) {
    Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
  }

  public static void sendBroadcastMessage(Intent intent) {
    mContext.sendBroadcast(intent);
  }


  // Custom View Pager Adapter
  private class ViewPagerAdapter extends FragmentStatePagerAdapter {

    String[] title = new String[]{
            getResources().getString(R.string.title_beacon) ,
            getResources().getString(R.string.title_contact),
            getResources().getString(R.string.title_manage_beacon),
            getResources().getString(R.string.title_setting)};

    public ViewPagerAdapter(FragmentManager fm) {
      super(fm);
    }

    @Override
    public Fragment getItem(int position) {
      switch (position) {
        case 0:
          return new MainPageFragment();
        case 1:
          return new ContactFragment();
        case 2:
          return new BeaconFragment();
        case 3:
          if(isLogin)
            return new AccountFragment();
          return new LoginFragment();
      }
      return null;
    }
//
//    @Override
//    public CharSequence getPageTitle(int position) {
//      return title[position];
//    }

    public View getTabView(int position) {
      View v = getLayoutInflater().inflate(R.layout.custom_tab, null);
      TextView tab_item = v.findViewById(R.id.tab_item);
      tab_item.setText(title[position]);
      tab_item.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.tab_icon);

      return v;
    }



    @Override
    public int getCount() {
      return NUM_PAGES;
    }


  }

}
