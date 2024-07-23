package com.example.popcat;

import android.app.Dialog;
import android.content.Context;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class SettingPopup extends Dialog {
    private InputPopupActivity inputDialog;
    private TextView score;
    private TextView click;
    private Button btnReset;
    private Button btnLoad;
    private ImageButton btnClose;

    SettingPopup(@NonNull Context context, int scoreP, int clickP) {
        super(context);
        setContentView(R.layout.popup_setting);

        score = findViewById(R.id.setting_score);
        click = findViewById(R.id.setting_click);
        btnReset = findViewById(R.id.btn_reset);
        btnLoad = findViewById(R.id.btn_load);
        btnClose = findViewById(R.id.btn_setting_cancel);

        score.setText("Score : " + Integer.toString(scoreP));
        click.setText("Click : " + Integer.toString(clickP));

        btnLoad.setOnClickListener(v -> {
            inputDialog = new InputPopupActivity(context);
            inputDialog.show();
            dismiss();
        });

        btnReset.setOnClickListener(v ->{
            ((MainActivity)MainActivity.mainContext).clear();
            dismiss();
        });

        btnClose.setOnClickListener(v -> {
            dismiss();
        });
    }
}