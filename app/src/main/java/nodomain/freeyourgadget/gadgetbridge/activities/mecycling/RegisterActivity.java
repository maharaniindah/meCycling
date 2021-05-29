package nodomain.freeyourgadget.gadgetbridge.activities.mecycling;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import nodomain.freeyourgadget.gadgetbridge.R;
import nodomain.freeyourgadget.gadgetbridge.activities.base.BaseActivity;

public class RegisterActivity extends BaseActivity {

    Button btnRegister;
    EditText etEmail;
    EditText etPassword;
    TextView tvLogin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initLayout();
        initListener();
    }

    private void initListener(){
        tvLogin.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });

        btnRegister.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            pref.edit().putString(email, password).apply();
            Toast.makeText(this, "berhasil register", Toast.LENGTH_LONG).show();
        });
    }

    private void initLayout(){
        btnRegister = findViewById(R.id.btn_register);
        etEmail = findViewById(R.id.et_register_email);
        etPassword = findViewById(R.id.et_register_password);
        tvLogin = findViewById(R.id.tv_register_toLogin);
    }
}
