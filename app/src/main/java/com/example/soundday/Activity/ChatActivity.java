package com.example.soundday.Activity;

import android.Manifest;
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
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity
        implements ChatAdapter.HandleChatClick {

    private static final String TAG = "ChatActivity";

    private ActivityChatBinding binding;
    //뷰 바인딩을 통해 findviewbyid를 없애줌

    private ChatAdapter chatAdapter;
    private ChatActivityViewModel viewModel;

    //intent로 받는 애들
    private int diary_id;
    private String diary_Name;
    private boolean completed;

    //음성인식
    Context cThis;//context 설정
    //음성 인식용
    Intent SttIntent;
    SpeechRecognizer mRecognizer;
    //음성 출력용
    TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cThis=this;

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


        //전송버튼
        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String contents = "집에 있는 시간이 너무 길어지니깐 답답하고 기분도 다운되는거 같아 이런 기분을 어떻게 해소할 수 있을지 모르겠어";
                String contents = binding.etTyping.getText().toString();
                //사용자가 아무 값도 안넣고, 저장버튼 누르는 걸 막음
                if (TextUtils.isEmpty(contents)) {
                    Toast.makeText(ChatActivity.this, "채팅을 입력해주세요!", Toast.LENGTH_SHORT).show();
                    return;
                }
                saveNewChat(contents);
            }
        });

        //음성 인식
        binding.imageGotovoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("음성인식 시작!");
                if(ContextCompat.checkSelfPermission(cThis, Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(ChatActivity.this,new String[]{Manifest.permission.RECORD_AUDIO},1);
                    //권한을 허용하지 않는 경우
                }else{
                    //권한을 허용한 경우
                    try {
                        mRecognizer.startListening(SttIntent);
                    }catch (SecurityException e){e.printStackTrace();}
                }

            }
        });

        initrecyclerview();
        initviewmodel();
        viewModel.getAllChatList(diary_id);
    }

    //Method--------
    private void saveNewChat(String contents) {
        //viewModel에 값을 넣어주기위해 item 객체를 만듦
        Chat item = new Chat();
        item.contents = contents;
        item.diaryId = diary_id;
        item.user = true;

        viewModel.insertChat(item);

        //binding.etTyping.setText("");
        //값을 넣어줬으므로, edittext를 비워줌
    }

    //음성 인식------------
    private void initRecorder(){
        SttIntent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        SttIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,getApplicationContext().getPackageName());
        SttIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR");//한국어 사용
        mRecognizer= SpeechRecognizer.createSpeechRecognizer(cThis);
        mRecognizer.setRecognitionListener(listener);

        //음성출력 생성, 리스너 초기화
        tts=new TextToSpeech(cThis, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status!=android.speech.tts.TextToSpeech.ERROR){
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });
    }

    //음성 인식 리스너
    private RecognitionListener listener=new RecognitionListener() {
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
//            Toast.makeText(cThis, "죄송하지만, 천천히 다시 말해 주세요", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResults(Bundle results) {
            String key= "";
            key = SpeechRecognizer.RESULTS_RECOGNITION;
            ArrayList<String> mResult =results.getStringArrayList(key);
            String[] rs = new String[mResult.size()];
            mResult.toArray(rs);

            String contents = rs[0];
            //사용자가 아무 값도 안넣고, 저장버튼 누르는 걸 막음
            if (TextUtils.isEmpty(contents)) {
                Toast.makeText(ChatActivity.this, "채팅을 입력해주세요!", Toast.LENGTH_SHORT).show();
                return;
            }
            saveNewChat(contents);
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
                Intent intent = new Intent(ChatActivity.this, DiaryActivity.class);
                intent.putExtra("diaryId", diary_id);
                intent.putExtra("completed", completed);
                startActivity(intent);
                finish();
            }
        });

        binding.imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void statusbarTransparent() {
        //style AppTheme에 <item name="android:statusBarColor">@android:color/transparent</item>
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }

    //ChatAdapter의 HandleChatClick interface 구현------
    @Override
    public void removeItem(Chat Chat) {

    }
}