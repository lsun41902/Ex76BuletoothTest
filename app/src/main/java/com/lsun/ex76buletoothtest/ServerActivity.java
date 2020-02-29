package com.lsun.ex76buletoothtest;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.UUID;

public class ServerActivity extends AppCompatActivity {

    static final UUID BT_UUID= UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static final int REQ_ENABLE=10;
    public static final int REQ_DISCOVERYABLE=20;
    TextView tv;
    BluetoothServerSocket serverSocket;
    BluetoothSocket socket;
    BluetoothAdapter bluetoothAdapter;
    DataInputStream dis;//자료형 단위로 보낼수 있음. 하지만 그냥 inputstream을 사용하면 byte단위로 보내지기때문에 작업을 한번 더해야함
    DataOutputStream dos;
    ServerThread serverThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
        getSupportActionBar().setTitle("SERVER");
        tv=findViewById(R.id.tv);

        bluetoothAdapter= BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter==null){
            Toast.makeText(this, "이 기기에는 블루투스가 없습니다.", Toast.LENGTH_SHORT).show();
            finish();//이 순간 바로 끝나는게 아님. 꺼지는데 걸리는 시간동안 밑에가 실행이됨
            return;
        }
        //블루투스가 켜져있는지 확인
        if(bluetoothAdapter.isEnabled()){
            //서버 소켓 생성 작업 시작
            createServerSocket();

        }else {
            //블루투스 장치 on선택 액티비티 보이기
            Intent intent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent,REQ_ENABLE);
        }

    }//oncreate Method

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQ_ENABLE:
                if(resultCode==RESULT_CANCELED){
                    //Enable을 시키지 않았으므로 프로그램종료
                    Toast.makeText(this, "블루투스를 허용하지 않았습니다.\n앱을 종료합니다.", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }else{
                    //서버 소캣 생성및 실행
                    createServerSocket();
                }
                break;
            case REQ_DISCOVERYABLE:
                if(resultCode==RESULT_CANCELED){
                    Toast.makeText(this,"블루투스 탐색을 허용하지 않았습니다.\n다른 장치에서 이 장치를 찾을 수 없습니다.",Toast.LENGTH_SHORT).show();
                }else {

                }
        }
    }
    //서버소켓 생성 작업 하는 메소드
    void createServerSocket(){
        //통신을 하기위한 별도의 쓰레드 객체 생성
        serverThread=new ServerThread();
        serverThread.start();
        //상대방이 내 디바이스의 BT를 탐색하는 것을 허용하기
        allowDiscovery();

    }
    //bluetooth 탐색 허용 작업 메소드
    void allowDiscovery(){
        //탐색허용 여부를 보여주는 다이얼로그 모양의 액티비티를 실행하셔야합니다.
        Intent intent= new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        //탐색 시간은 기본 120, 최대 300초 설정 가능
        intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,300);//탐색 시간 설정하기
        startActivityForResult(intent,REQ_DISCOVERYABLE);
    }


    //inner class/////////////
    class ServerThread extends Thread{//준비를 하고있다가 start를 해주면 시작
        @Override
        public void run() {
            //서버소켓 생성
            try {
                serverSocket=bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord("SERVER",BT_UUID);
                showText("서버 소켓 생성했습니다.\n클라이언트의 접속을 기다립니다.");
                //클라이언트의 접속 기다리기..
                socket=serverSocket.accept();//클라이언트가 접속할때까지 대기
                showText("클라이언트가 접속했습니다.\n");
                //접속이 되었으니 둘사이에 데이터를 주고받을
                //무지개로드(Stream)만들기
                dis=new DataInputStream(socket.getInputStream());
                dos=new DataOutputStream(socket.getOutputStream());
                //dis&dos를 이용해서 원하는 통신작업을 수행!

            } catch (IOException e) {
                e.printStackTrace();
            }
        }//run
        void showText(final String msg){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tv.append(msg);
                }
            });
        }
    }//serverThread


}//server activity
