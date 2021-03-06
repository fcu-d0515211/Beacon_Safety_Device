//package com.example.gibson.myapplication;
//
//import android.app.Activity;
//import android.bluetooth.BluetoothAdapter;
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.graphics.drawable.Drawable;
//import android.media.AudioManager;
//import android.media.MediaPlayer;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Handler;
//import android.support.annotation.Nullable;
//import android.support.v7.app.AppCompatActivity;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.MediaController;
//import android.widget.VideoView;
//
//import com.example.gibson.myapplication.Fragment.ContactFragment;
//import com.example.gibson.myapplication.Services.BeaconDetectService;
//import com.sinch.android.rtc.calling.Call;
//
//import java.io.IOException;
//
//public class ReceiveBeaconActivity extends AppCompatActivity {
//
//  Button cancelBtn;
//  static Intent intent;
//  public BluetoothAdapter mBluetoothAdapter;
//  public static final int BluetoothRequestCode = 2;
//  static MediaPlayer mediaPlayer;
//  public static Context mContext;
//  public static Boolean cancel;
//  Call call;
//  public static ImageView dog;
//  public static Drawable sleepDog;
//  public static Drawable angeryDog;
//
//
//  static VideoView videoView;
//
//
//  @Override
//  protected void onCreate(@Nullable Bundle savedInstanceState) {
//    super.onCreate(savedInstanceState);
//    setContentView(R.layout.activity_receivebeacon);
//    mContext = this;
//    intent = new Intent(this, BeaconDetectService.class);
//    startBeaconReceive();
//    cancel=false;
//    dog = findViewById(R.id.dogImg);
//    sleepDog = getResources().getDrawable(R.drawable.dog1);
//    angeryDog =getResources().getDrawable(R.drawable.dog2);
//    cancelBtn = findViewById(R.id.cancelBtn);
//    cancelBtn.setOnClickListener(new View.OnClickListener() {
//      @Override
//      public void onClick(View v) {
//        stopService(intent);
//        ContactFragment.endcall();
//        cancel= true;
//        finish();
//      }
//    });
//    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//
////    startVideo();
//  }
//
//  public static void playMedia(int music_source, int volume) {
//    AudioManager manager = (AudioManager) mContext.getSystemService(AUDIO_SERVICE);
//    manager.setStreamVolume(
//            AudioManager.STREAM_MUSIC,
//            volume,
//            AudioManager.ADJUST_RAISE);
//    stopMedia();
//
//    mediaPlayer = MediaPlayer.create(mContext, music_source);
//    mediaPlayer.start();
//  }
//
//  public static void stopMedia() {
//    Log.v("call", "stop media");
//    if(mediaPlayer != null && mediaPlayer.isPlaying())
//      mediaPlayer.stop();
//    Log.v("call", "stop media finished");
//  }
//  public static void setSleepDog(){
//    dog.setImageDrawable(sleepDog);
//  }
//
//  public static void setAngryDog(){
//    dog.setImageDrawable(angeryDog);
//  }
//
////  public void startVideo() {
////    videoView = findViewById(R.id.videoView);
////    videoView.setMediaController(new MediaController(this));
////
////    videoView.setVideoURI(Uri.parse("android.resource://" +getPackageName()+ "/"+R.raw.dog_bark));
////    videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
////      @Override
////      public void onCompletion(MediaPlayer mp) {
////        mp.stop();
////        try {
////          mp.prepare();
////          mp.start();
////        } catch (IOException e) {
////          e.printStackTrace();
////        }
////      }
////    });
////    videoView.start();
////  }
////
////  public static void stopVideo() {
////    Log.v("call", "stop video");
////    if (videoView.isPlaying())
////      videoView.stopPlayback();
////    Log.v("call", "stop video finished");
////
////  }
//
//
//
//  void checkBluetooth() {
//    if (mBluetoothAdapter != null) {
//      if (!mBluetoothAdapter.isEnabled()) {
//        startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), BluetoothRequestCode);
//      }
//    } else {
//      startBeaconReceive();
//    }
//  }
//
//  @Override
//  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//    super.onActivityResult(requestCode, resultCode, data);
//    if(requestCode == BluetoothRequestCode) {
//      if(resultCode == PackageManager.PERMISSION_GRANTED) {
//        startBeaconReceive();
//      }
//    }
//  }
//
//  public void startBeaconReceive() {
//    if(mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
//      startBeaconService();
//    }
//  }
//
//  public static void startBeaconService() {
//    mContext.startService(intent);
//  }
//
//  public static void stopBeaconService() {
//    Log.v("call", "stop service");
//    mContext.stopService(intent);
//    Log.v("call", "stop service finished");
//
//  }
//
//  @Override
//  protected void onResume() {
//    checkBluetooth();
//    startBeaconReceive();
//    setSleepDog();
////    startVideo();
//    super.onResume();
//  }
//
//  @Override
//  protected void onPause() {
//    stopMedia();
//    stopBeaconService();
//    super.onPause();
//  }
//
//  @Override
//  protected void onDestroy() {
//    super.onDestroy();
//    stopMedia();
//    stopBeaconService();
//    MainActivity.receiveMode = false;
//  }
//}
