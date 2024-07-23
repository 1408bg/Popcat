package com.example.popcat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    //외부 접근용
    public static Context mainContext;
    //popup_setting
    private SettingPopup settingDialog;
    private ShopNavPopup shopNavDialog;
    // SaveData
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    // ViewLoad
    private ImageView imgPopcat;
    private TextView tvCounter;
    private TextView scoreCounter;
    private Button btnSetting;
    private Button btnShop;
    // etc..
    private ConstraintLayout main;
    private Handler handler;
    private SoundPool popSound;
    private final Random rand = new Random();
    private ColorMatrix matrix;
    // val
    private int popSoundId; // 소리 저장용
    private byte clickCount; // 클릭 횟수
    private byte score; // 점수 (클릭 100)
    private int[] position = {0, 0}; // img 이동 위치
    private int[][] imgArr; // 이미지 배열
    private int now; // 현재 이미지
    private boolean[] isBought; // 이미지 보유 여부
    private int strength; // 배율 (점수 100)

    @SuppressLint({"ClickableViewAccessibility", "ResourceAsColor"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // _____기본 환경 설정_____
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent(); // shop 에서 이동 시 데이터 전달용 intent
        mainContext = this; // 외부 참조용 객체

        pref = getPreferences(Context.MODE_PRIVATE); // 저장용 preferences
        editor = pref.edit(); // 실제로 저장 하는 객체

        handler = new Handler(Looper.getMainLooper()); // 반복 실행용 handler

        DisplayMetrics displayMetrics = new DisplayMetrics(); // 화면 크기 구하는 용
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics); // 화면 크기 구하기
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        matrix = new ColorMatrix();

        // dialog 화면 설정
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.8f;
        getWindow().setAttributes(layoutParams);

        // _____변동 데이터_____
        // 이미지 배열
        imgArr = new int[3][];
        imgArr[0] = new int[]{R.drawable.i1_close, R.drawable.i1_open};
        imgArr[1] = new int[]{R.drawable.i2_close, R.drawable.i2_open};
        imgArr[2] = new int[]{R.drawable.i3_close, R.drawable.i3_open};

        // _____기본 데이터 설정_____
        // Activity
        main = findViewById(R.id.main);
        // View
        imgPopcat = findViewById(R.id.img_popcat);
        tvCounter = findViewById(R.id.tv_counter);
        scoreCounter = findViewById(R.id.score_counter);
        btnSetting = findViewById(R.id.btn_popup);
        btnShop = findViewById(R.id.btn_shop);
        // Sound
        popSound = new SoundPool.Builder().build();
        popSoundId = popSound.load(this, R.raw.raw_pop, 1);
        // 외부 / 장면 데이터 가져 오기
        score = intent.getByteExtra("point", (byte)pref.getInt("Score",0));
        clickCount = intent.getByteExtra("click", (byte) pref.getInt("Count",0));
        strength = intent.getIntExtra("Strength", pref.getInt("Strength", 1));
        now = intent.getIntExtra("now", 0);
        tvCounter.setText("Click : " + clickCount);
        scoreCounter.setText("Score : "+ score);
        isBought = intent.getBooleanArrayExtra("isBought");
        if (isBought == null){
            isBought = new boolean[]{pref.getBoolean("B1", true), pref.getBoolean("B2", false), pref.getBoolean("B3", false)};
        }

        // _____실제 화면 설정_____
        imgPopcat.setImageResource(imgArr[now][0]);
        main.setBackgroundColor(Color.rgb(255 - clickCount, 235 - clickCount, 215 - clickCount));
        tvCounter.setTextSize(24 + ((float) clickCount / 10));

        filter();

        // [Button] Dialog(PopupSetting) 열기
        btnSetting.setOnClickListener(v -> {
            settingDialog = new SettingPopup(this, score, (int)clickCount);
            settingDialog.show();
        });

        // [Button] Shop(SubActivity)으로 이동
        btnShop.setOnClickListener(v -> {
            shopNavDialog = new ShopNavPopup(this);
            shopNavDialog.show();
        });

        // [Img] 클릭 이벤트
        imgPopcat.setOnTouchListener((view, motionEvent) -> {
            position = new int[]{rand.nextInt()%(width/3), rand.nextInt()%(height/3)};
            switch (motionEvent.getAction()) {
                case (MotionEvent.ACTION_DOWN):
                    clickCount+= (byte) strength;
                    // Score 증가 로직
                    if (clickCount >= 100) {
                        clickCount -= 100;
                        score++;
                    }
                    if (score == 100){
                        score = 0;
                        strength++;
                        filter();
                    }
                    // 입 벌리기
                    imgPopcat.setImageResource(imgArr[now][1]);
                    // 소리 재생
                    popSound.play(popSoundId, 1f, 1f, 0, 0, 1f);
                    // 위치 변경
                    imgPopcat.setTranslationX(position[0]);
                    imgPopcat.setTranslationY(position[1]);
                    update();
                    return true;
                case (MotionEvent.ACTION_UP):
                    // 입 닫기
                    imgPopcat.setImageResource(imgArr[now][0]);
                    return true;
            }
            return false;
        });

        // Score 색 변경용
        new Thread((new Runnable() {
            int i = 0;
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (i == 255){
                            i = 0;
                        }
                        scoreCounter.setTextColor(Color.rgb(i+=5, (i%50)*4, (i*7)%255));
                        handler.postDelayed(this,50); // 50ms 주기로 반복 실행
                    }
                });
            }
        })).start();
    }
    // 다른 activity 전환, 앱 위에 팝업
    @Override
    protected void onPause(){
        super.onPause();

        save();
    }
    // 앱의 background 전환
    @Override
    protected void onStop(){
        super.onStop();

        save();
    }
    // 초기화 함수
    public void clear(){
        editor.clear();
        editor.apply();
        editor.commit();
        clickCount = 0;
        score = 0;
        strength = 1;
        main.setBackgroundColor(Color.rgb(255,235,215));
        tvCounter.setText("Click : " + Integer.toString(clickCount));
        scoreCounter.setText("Score : "+ score);
        tvCounter.setTextSize(24);
        imgPopcat.setTranslationX(0);
        imgPopcat.setTranslationY(0);
        isBought = new boolean[]{true, false, false};
        now = 0;
        imgPopcat.setImageResource(imgArr[now][0]);
        imgPopcat.clearColorFilter();
    }
    // 저장 함수
    public void save(){
        editor.putInt("Score", score);
        editor.putInt("Count", clickCount);
        editor.putInt("Strength", strength);
        editor.putBoolean("B1", isBought[0]);
        editor.putBoolean("B2", isBought[1]);
        editor.putBoolean("B3", isBought[2]);
        editor.apply();
        editor.commit();
    }
    // update 대비 로딩용
    public void load(int value){
        if (value == 6974){
            score = 99;
            clickCount = 90;
            isBought = new boolean[]{true, false, false};
            imgPopcat.setImageResource(imgArr[now][0]);
            update();
        }
    }
    // textView update
    private void update(){
        main.setBackgroundColor(Color.rgb(255 - clickCount, 235 - clickCount, 215 - clickCount));
        tvCounter.setTextSize(24 + ((float) clickCount / 10));
        tvCounter.setText("Click : " + clickCount);
        scoreCounter.setText("Score : "+ score);
    }
    // popup_img 필터
    private void filter(){
        matrix.setSaturation(1+((float)strength/5));
        ColorMatrixColorFilter cf = new ColorMatrixColorFilter(matrix);
        imgPopcat.setColorFilter(cf);
    }
    // intents
    public void moveTo(Class activity){
        Intent intents = new Intent(MainActivity.this, activity);
        intents.putExtra("point", score);
        intents.putExtra("click", clickCount);
        intents.putExtra("now", now);
        intents.putExtra("isBought", isBought);
        intents.putExtra("Strength", strength);
        startActivity(intents);
    }
}