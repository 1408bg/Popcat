package com.example.popcat;

import android.app.Dialog;
import android.content.Context;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class InputPopupActivity extends Dialog {
    EditText inputValue;
    Button btnGet;

    InputPopupActivity(@NonNull Context context){
        super(context);
        setContentView(R.layout.popup_text_input);

        inputValue = findViewById(R.id.input_code);
        btnGet = findViewById(R.id.btn_get);

        btnGet.setOnClickListener(v -> {
            if (inputValue.getText().toString().isEmpty()){
                return;
            }
            ((MainActivity)MainActivity.mainContext).load(Integer.parseInt(inputValue.getText().toString()));
            dismiss();
        });
    }
}
