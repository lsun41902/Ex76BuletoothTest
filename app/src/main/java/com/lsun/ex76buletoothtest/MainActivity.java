package com.lsun.ex76buletoothtest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_DENIED){
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},10);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 10:
                if(grantResults[0]==PackageManager.PERMISSION_DENIED){
                    Toast.makeText(this, "장치를 탐색하는 기능을 제한합니다.", Toast.LENGTH_SHORT).show();
                }else {

                }
                break;
        }
    }

    public void clickServer(View view) {
        Intent intent=new Intent(this,ServerActivity.class);
        startActivity(intent);
    }

    public void clickClient(View view) {
        startActivity(new Intent(this,ClientActivity.class));
    }
}
