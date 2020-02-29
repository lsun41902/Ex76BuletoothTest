package com.lsun.ex76buletoothtest;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class ClientActivity extends AppCompatActivity {

    static final UUID BT_UUID= UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static final int REQ_ENABLE=10;
    public static final int REQ_DISCOVERY=20;
    BluetoothAdapter bluetoothAdapter;
    BluetoothSocket socket;
    DataInputStream dis;
    DataOutputStream dos;
    ClienteThread clienteThread;
    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        getSupportActionBar().setTitle("CLIENT");
        tv=findViewById(R.id.tv);
        //블루투스 관리자 객체 소환
        bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();//1번
        if(bluetoothAdapter==null){
            Toast.makeText(this, "이 기기는 블루투스가 없습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }//2번

        if (bluetoothAdapter.isEnabled()){
            //서버 블루투스 장치를 탐색하고 리스트를 보여주는 액티비티를 새로 만들것입니다.
            discoveryBluetoothDevices();
        }else {
            Intent intent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent,REQ_ENABLE);
        }//3번

    }//onCreate

    //주변의 블루투스장치들을 탐색 하여 리스트로 보여주는 액티비티를
    void discoveryBluetoothDevices(){
        Intent intent=new Intent(this,BTDevicesListActivity.class);
        startActivityForResult(intent,REQ_DISCOVERY);
    }//5번


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQ_ENABLE:
                if(resultCode==RESULT_CANCELED){
                    Toast.makeText(this, "블루투스 허용을 거부 하셨습니다.", Toast.LENGTH_SHORT).show();
                }else {
                    //서버 블루투스 장치 탐색 및 리스트 보기
                    discoveryBluetoothDevices();
                }//4번
                break;
            case REQ_DISCOVERY:
                if(resultCode==RESULT_OK){
                    //연결할 블루투스 장치의 mac주소를 얻어왔다
                    String deviceAddress=data.getStringExtra("Address");
                    //이 주소를 통해 socket연결작업 실행하는 별도의 Thread객체 생성 및 실행
                    clienteThread=new ClienteThread(deviceAddress);
                    clienteThread.start();

                }
                    break;

        }
    }

    class ClienteThread extends Thread{
        String address;

        public ClienteThread(String address) {
            this.address = address;
        }
        @Override
        public void run() {
            //블루투스 소켓 생성 하기위한 블루투스 장치객체를 만든겁니다.
            BluetoothDevice device =bluetoothAdapter.getRemoteDevice(address);
            //디바이스를 통해 소켓연결
            try {
                socket=device.createInsecureRfcommSocketToServiceRecord(BT_UUID);
                socket.connect();//연결 시도
                //연결이 성공되었다고 메세지
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv.setText("서버와 연결 하였습니다.");
                    }
                });
                dis=new DataInputStream(socket.getInputStream());
                dos=new DataOutputStream(socket.getOutputStream());
                //원하는 통신작업을 수행...

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}//client Activity class
