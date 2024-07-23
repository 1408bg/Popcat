package com.example.popcat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ImageShopActivity extends AppCompatActivity {
    private Button btnBack;
    private TextView scoreCounter;
    private TextView costText;
    private Button btnBuy;
    private ImageView imgPopcat;
    private ImageButton btnLeft;
    private ImageButton btnRight;
    private byte score;
    private byte click;
    private int strength;
    private byte sel;
    private int now;
    private byte btnState;
    private boolean[] isBought;
    private int[] imgArr;
    private int[] cost;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_image);
        Intent intent = getIntent();

        sel = 0;
        btnState = 0;
        cost = new int[]{0, 10, 20};
        isBought = intent.getBooleanArrayExtra("isBought");

        score = intent.getByteExtra("point", (byte)0);
        click = intent.getByteExtra("click", (byte)0);
        now = intent.getIntExtra("now", 0);
        strength = intent.getIntExtra("Strength", 1);

        btnBack = findViewById(R.id.btn_back);
        scoreCounter = findViewById(R.id.sub_score_counter);
        costText = findViewById(R.id.cost);
        btnBuy = findViewById(R.id.btn_buy);
        btnLeft = findViewById(R.id.left_arrow);
        btnRight = findViewById(R.id.right_arrow);
        imgPopcat = findViewById(R.id.sub_popcat);

        imgArr = new int[3];
        imgArr[0] = R.drawable.i1_close;
        imgArr[1] = R.drawable.i2_close;
        imgArr[2] = R.drawable.i3_close;
        imgPopcat.setImageResource(imgArr[sel]);

        scoreCounter.setText("Score : " + String.valueOf(score));

        btnBack.setOnClickListener(v -> {
            Intent intents = new Intent(ImageShopActivity.this, MainActivity.class);
            intents.putExtra("point", score);
            intents.putExtra("click", click);
            intents.putExtra("now", now);
            intents.putExtra("isBought", isBought);
            intents.putExtra("strength", strength);
            startActivity(intents);
        });

        btnRight.setOnClickListener(v -> {
            if (sel != 2){
                sel++;
                updateSel();
            }
        });

        btnLeft.setOnClickListener(v -> {
            if (sel != 0){
                sel--;
                updateSel();
            }
        });

        btnBuy.setOnClickListener(v -> {
            switch(btnState){
                case(1):
                    now = sel;
                    btnState = 0;
                case(2):
                    if (score >= cost[sel] && !isBought[sel]){
                        score -= cost[sel];
                        now = sel;
                        isBought[sel] = true;
                        scoreCounter.setText("Score : " + String.valueOf(score));
                        btnState = 0;
                    }
            }
            updateSel();
        });
        updateSel();
    }

    private void updateSel(){
        if (now == sel){
            btnState = 0;
        }
        else if (isBought[sel]){
            btnState = 1;
        }
        else {
            btnState = 2;
        }
        costText.setText("cost : " + String.valueOf(cost[sel]));
        imgPopcat.setImageResource(imgArr[sel]);
        if (sel == 0){
            btnLeft.setVisibility(View.INVISIBLE);
        }
        else {
            btnLeft.setVisibility(View.VISIBLE);
        }
        if (sel == 2){
            btnRight.setVisibility(View.INVISIBLE);
        }
        else {
            btnRight.setVisibility(View.VISIBLE);
        }
        switch(btnState){
            case(0):
                btnBuy.setText("Selected");
                btnBuy.setBackgroundColor(Color.rgb(100, 100, 100));
                break;
            case(1):
                btnBuy.setText("Select");
                btnBuy.setBackgroundColor(Color.rgb(103, 80, 164));
                break;
            case(2):
                btnBuy.setText("Buy");
                if (isBought[sel] || score < cost[sel]) {
                    btnBuy.setBackgroundColor(Color.rgb(100, 100, 100));
                }
                else {
                    btnBuy.setBackgroundColor(Color.rgb(103, 80, 164));
                }
        }
    }
}
