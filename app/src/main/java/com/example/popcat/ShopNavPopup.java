package com.example.popcat;

import android.app.Dialog;
import android.content.Context;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class ShopNavPopup extends Dialog {
    private ImageButton btnClose;
    private Button btnImage;
    private Button btnBg;
    private Button btnSound;

    ShopNavPopup(@NonNull Context context){
        super(context);
        setContentView(R.layout.popup_shop_nav);

        btnImage = findViewById(R.id.btn_image);
        btnBg = findViewById(R.id.btn_bg);
        btnSound = findViewById(R.id.btn_sound);
        btnClose = findViewById(R.id.btn_shop_cancel);

        btnImage.setOnClickListener(v ->{
            ((MainActivity)MainActivity.mainContext).moveTo(ImageShopActivity.class);
            dismiss();
        });

        btnClose.setOnClickListener(v -> {
            dismiss();
        });


    }

}
