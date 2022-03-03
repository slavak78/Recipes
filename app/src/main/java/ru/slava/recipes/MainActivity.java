package ru.slava.recipes;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.github.rtoshiro.secure.SecureSharedPreferences;

public class MainActivity extends AppCompatActivity {
    Button nextstep;
    EditText tel;
    SecureSharedPreferences mSettings;

        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSettings = new SecureSharedPreferences(MainActivity.this);
        nextstep = findViewById(R.id.nextstep);
        tel = findViewById(R.id.tel);


        nextstep.setOnClickListener(v -> {
            String tel1 = tel.getText().toString().replaceAll("[^\\d]", "");
            tel1 = "+" + tel1;
            Intent intent = new Intent(MainActivity.this, RegistryActivity.class);
            intent.putExtra("tel", tel1);
            startActivity(intent);
                });


        tel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int y = s.length();
                if(y>=18) {
                    nextstep.setVisibility(View.VISIBLE);
                } else {
                    nextstep.setVisibility(View.GONE);
                }
            }
        });
    }
}
