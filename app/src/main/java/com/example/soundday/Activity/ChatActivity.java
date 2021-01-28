package com.example.soundday.Activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.soundday.Adapter.ChatAdapter;
import com.example.soundday.DB.Chat;
import com.example.soundday.Model.ChatActivityViewModel;
import com.example.soundday.R;
import com.example.soundday.databinding.ActivityChatBinding;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity
        implements ChatAdapter.HandleChatClick {

    //[[수정 필요]]
    private int count = 0;
    private static final String TAG = "ChatActivity";
    private String nxtString = "";

    private ActivityChatBinding binding;
    //뷰 바인딩을 통해 findviewbyid를 없애줌

    private ChatAdapter chatAdapter;
    private ChatActivityViewModel viewModel;

    //intent로 받는 애들
    private int diary_id;
    private String diary_Name;
    private boolean completed;

    //음성인식--------
    Context cThis;
    Intent SttIntent;     //음성 인식용
    SpeechRecognizer mRecognizer;
    TextToSpeech tts;     //음성 출력용

    //블루투스--------
    private final int REQUEST_BLUETOOTH_ENABLE = 100;
    ConnectedTask mConnectedTask = null;
    static BluetoothAdapter mBluetoothAdapter;
    private String mConnectedDeviceName = null;
    static boolean isConnectionError = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cThis = this;

        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat);

        //UserMainActivity에서 값을 넘겨준 걸 받음
        diary_Name = getIntent().getStringExtra("diaryName");
        diary_id = getIntent().getIntExtra("diaryId", 0);
        completed = getIntent().getBooleanExtra("completed", true);

        //상태바 투명하게
        statusbarTransparent();

        //툴바
        funcToolbar();

        //음성인식 init
        initRecorder();

        //블루투스 init
        if(!completed) {
            initBlueTooth();
        }

        //음성 인식
        binding.imageGotovoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("음성인식 시작!");
                if (ContextCompat.checkSelfPermission(cThis, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ChatActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
                    //권한을 허용하지 않는 경우
                } else {
                    //권한을 허용한 경우
                    try {
                        mRecognizer.startListening(SttIntent);
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        initrecyclerview();
        initviewmodel();
        viewModel.getAllChatList(diary_id);
    }

    //Method--------
    private void saveNewChat(String contents, boolean user) {
        //viewModel에 값을 넣어주기위해 item 객체를 만듦
        Chat item = new Chat();
        item.contents = contents;
        item.diaryId = diary_id;
        item.user = user;

        viewModel.insertChat(item);
    }

    //음성 인식------------
    private void initRecorder() {
        SttIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        SttIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getApplicationContext().getPackageName());
        SttIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");//한국어 사용
        mRecognizer = SpeechRecognizer.createSpeechRecognizer(cThis);
        mRecognizer.setRecognitionListener(listener);

        //음성출력 생성, 리스너 초기화
        tts = new TextToSpeech(cThis, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != android.speech.tts.TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });
    }

    //tts deprecated돼서 메소드를 따로 만들어줌
    @SuppressWarnings("deprecation")
    private void ttsUnder20(String text) {
        HashMap<String, String> map = new HashMap<>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, map);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void ttsGreater21(String text) {
        String utteranceId=this.hashCode() + "";
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }

    //음성 인식 리스너
    private RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle bundle) {
            //txtSystem.setText("onReadyForSpeech..........."+"\r\n"+txtSystem.getText());
        }

        @Override
        public void onBeginningOfSpeech() {
            Toast.makeText(cThis, "듣고 있어요! 말을 해주세요!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRmsChanged(float v) {

        }

        @Override
        public void onBufferReceived(byte[] bytes) {
            Toast.makeText(cThis, "하루를 정리하고 있어요!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onEndOfSpeech() {
            Toast.makeText(cThis, "완료! 하루를 정리하고 있어요", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(int i) {
            Toast.makeText(cThis, "죄송하지만, 천천히 다시 말해 주세요", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResults(Bundle results) {
            String key = "";
            key = SpeechRecognizer.RESULTS_RECOGNITION;
            ArrayList<String> mResult = results.getStringArrayList(key);
            String[] rs = new String[mResult.size()];
            mResult.toArray(rs);

            String contents = rs[0];
            //사용자가 아무 값도 안넣고, 저장버튼 누르는 걸 막음
            if (TextUtils.isEmpty(contents)) {
                Toast.makeText(ChatActivity.this, "채팅을 입력해주세요!", Toast.LENGTH_SHORT).show();
                return;
            }
            //서버에 값을 넣어준다!
            sendMessage(contents);
            saveNewChat(contents, true);
            nxtString += contents + "\n";
        }

        @Override
        public void onPartialResults(Bundle bundle) {
            // Toast.makeText(cThis, "이거 뭔지 모르겠어용!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onEvent(int i, Bundle bundle) {
            // txtSystem.setText("onEvent..........."+"\r\n"+txtSystem.getText());
        }
    };

    //블루투스 연결------------

    private void initBlueTooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            showErrorDialog("This device is not implement Bluetooth.");
            return;
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQUEST_BLUETOOTH_ENABLE);
        } else {
            Log.d(TAG, "Initialisation successful.");
            showPairedDevicesListDialog();
        }
    }

    private class ConnectTask extends AsyncTask<Void, Void, Boolean> {

        private BluetoothSocket mBluetoothSocket = null;
        private BluetoothDevice mBluetoothDevice = null;

        ConnectTask(BluetoothDevice bluetoothDevice) {
            mBluetoothDevice = bluetoothDevice;
            mConnectedDeviceName = bluetoothDevice.getName();

            //SPP
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

            try {
                mBluetoothSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(uuid);
                Log.d(TAG, "create socket for " + mConnectedDeviceName);

            } catch (IOException e) {
                Log.e(TAG, "socket create failed " + e.getMessage());
            }
        }


        @Override
        protected Boolean doInBackground(Void... params) {

            // Always cancel discovery because it will slow down a connection
            mBluetoothAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mBluetoothSocket.connect();
            } catch (IOException e) {
                // Close the socket
                try {
                    mBluetoothSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "unable to close() " +
                            " socket during connection failure", e2);
                }
                return false;
            }

            return true;
        }


        @Override
        protected void onPostExecute(Boolean isSucess) {

            if (isSucess) {
                connected(mBluetoothSocket);
            } else {
                isConnectionError = true;
                Log.d(TAG, "Unable to connect device");
                showErrorDialog("Unable to connect device");
            }
        }
    }


    public void connected(BluetoothSocket socket) {
        mConnectedTask = new ConnectedTask(socket);
        mConnectedTask.execute();
    }


    private class ConnectedTask extends AsyncTask<Void, String, Boolean> {

        private InputStream mInputStream = null;
        private OutputStream mOutputStream = null;
        private BluetoothSocket mBluetoothSocket = null;

        ConnectedTask(BluetoothSocket socket) {

            mBluetoothSocket = socket;
            try {
                mInputStream = mBluetoothSocket.getInputStream();
                mOutputStream = mBluetoothSocket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "socket not created", e);
            }

            Log.d(TAG, "connected to " + mConnectedDeviceName);
        }


        @Override
        protected Boolean doInBackground(Void... params) {

            byte[] readBuffer = new byte[1024];
            int readBufferPosition = 0;

            while (true) {
                if (isCancelled()) return false;

                try {

                    int bytesAvailable = mInputStream.available();

                    if (bytesAvailable > 0) {

                        byte[] packetBytes = new byte[bytesAvailable];

                        mInputStream.read(packetBytes);

                        for (int i = 0; i < bytesAvailable; i++) {

                            byte b = packetBytes[i];
                            if (b == '\n') {
                                byte[] encodedBytes = new byte[readBufferPosition];
                                System.arraycopy(readBuffer, 0, encodedBytes, 0,
                                        encodedBytes.length);
                                String recvMessage = new String(encodedBytes, "UTF-8");

                                readBufferPosition = 0;
                                Log.d(TAG, "recv message: " + recvMessage);
                                publishProgress(recvMessage);
                            } else {
                                readBuffer[readBufferPosition++] = b;
                            }
                        }
                    }
                } catch (IOException e) {

                    Log.e(TAG, "disconnected", e);
                    return false;
                }
            }

        }

        //다른 부분
        protected void onProgressUpdate(String... recvMessage) {
            if(!recvMessage[0].isEmpty()) {
                saveNewChat(recvMessage[0], false);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ttsGreater21(recvMessage[0]);
                } else {
                    ttsUnder20(recvMessage[0]);
                }
            }
        }

        @Override
        protected void onPostExecute(Boolean isSucess) {
            super.onPostExecute(isSucess);

            if (!isSucess) {
                closeSocket();
                Log.d(TAG, "Device connection was lost");
                isConnectionError = true;
                showErrorDialog("Device connection was lost");
            }
        }

        @Override
        protected void onCancelled(Boolean aBoolean) {
            super.onCancelled(aBoolean);

            closeSocket();
        }

        void closeSocket() {

            try {

                mBluetoothSocket.close();
                Log.d(TAG, "close socket()");

            } catch (IOException e2) {

                Log.e(TAG, "unable to close() " +
                        " socket during connection failure", e2);
            }
        }

        //값을 보냄
        void write(String msg) {
            msg += "\n";
            try {
                mOutputStream.write(msg.getBytes());
                mOutputStream.flush();
            } catch (IOException e) {
                Log.e(TAG, "Exception during send", e);
            }
        }
    }


    public void showPairedDevicesListDialog() {
        Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
        final BluetoothDevice[] pairedDevices = devices.toArray(new BluetoothDevice[0]);

        if (pairedDevices.length == 0) {
            showQuitDialog("No devices have been paired.\n"
                    + "You must pair it with another device.");
            return;
        }

        String[] items;
        items = new String[pairedDevices.length];
        for (int i = 0; i < pairedDevices.length; i++) {
            items[i] = pairedDevices[i].getName();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select device");
        builder.setCancelable(false);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                ConnectTask task = new ConnectTask(pairedDevices[which]);
                task.execute();
            }
        });
        builder.create().show();
    }

    public void showErrorDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Quit");
        builder.setCancelable(false);
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (isConnectionError) {
                    isConnectionError = false;
                    finish();
                }
            }
        });
        builder.create().show();
    }

    public void showQuitDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Quit");
        builder.setCancelable(false);
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        builder.create().show();
    }

    void sendMessage(String msg) {
        if (mConnectedTask != null) {
            mConnectedTask.write(msg);
            Log.d(TAG, "send message: " + msg);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_BLUETOOTH_ENABLE) {
            if (resultCode == RESULT_OK) {
                //BlueTooth is now Enabled
                showPairedDevicesListDialog();
            }
            if (resultCode == RESULT_CANCELED) {
                showQuitDialog("You need to enable bluetooth");
            }
        }
    }

    //--------------

    //뷰모델
    private void initviewmodel() {
        viewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(
                getApplication())).get(ChatActivityViewModel.class);

        //ChatList들이 실시간으로 반영되도록 observe함
        //데이터바인딩을 이용하면, observe패턴을 쓸 필요없이 xml에서 처리가 가능하다
        viewModel.getChatListObserver().observe(this, new Observer<List<Chat>>() {
            @Override
            public void onChanged(List<Chat> chats) {
                //items List가 비어있는 경우
                if (chats == null) {
                    binding.rcvChat.setVisibility(View.INVISIBLE);
                } else {
                    binding.rcvChat.setVisibility(View.VISIBLE);
                    chatAdapter.setChatList(chats);
                    binding.rcvChat.smoothScrollToPosition(chats.size() - 1);
                    //바뀐 chat이 리사이클러뷰에 적용되도록 adapter에게 알려줌
                }
            }
        });
    }

    //리사이클러뷰
    private void initrecyclerview() {
        binding.rcvChat.setLayoutManager(new LinearLayoutManager(this));
        chatAdapter = new ChatAdapter(this, this);
        binding.rcvChat.setAdapter(chatAdapter);
    }

    //툴바
    private void funcToolbar() {
        if (completed) {
            binding.tvFinish.setVisibility(View.INVISIBLE);
        } else {
            binding.tvFinish.setVisibility(View.VISIBLE);
        }

        binding.tvDiaryName.setText(diary_Name);

        binding.tvFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //일기 페이지로 이동
                if(nxtString.length() == 0) {
                    Toast.makeText(ChatActivity.this, "채팅을 시작해 일기를 적어보세요!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent = new Intent(ChatActivity.this, DiaryActivity.class);
                    intent.putExtra("diaryId", diary_id);
                    intent.putExtra("diaryContent", nxtString);
                    intent.putExtra("completed", completed);
                    startActivity(intent);
                    finish();
                }
            }
        });

        binding.imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //상태바 투명하게
    private void statusbarTransparent() {
        //style AppTheme에 <item name="android:statusBarColor">@android:color/transparent</item>
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }

    //On`Destroy
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mConnectedTask != null) {
            mConnectedTask.cancel(true);
        }
        if(tts !=null){
            tts.stop();
            tts.shutdown();
        }

        if(mRecognizer!=null){
            mRecognizer.destroy();
            mRecognizer.cancel();
            mRecognizer = null;
        }
    }

    //ChatAdapter의 HandleChatClick interface 구현------
    @Override
    public void removeItem(Chat Chat) {

    }
}