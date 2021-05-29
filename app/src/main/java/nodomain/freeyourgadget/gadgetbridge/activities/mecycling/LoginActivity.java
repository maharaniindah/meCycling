package nodomain.freeyourgadget.gadgetbridge.activities.mecycling;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import nodomain.freeyourgadget.gadgetbridge.R;
import nodomain.freeyourgadget.gadgetbridge.activities.ControlCenterv2;
import nodomain.freeyourgadget.gadgetbridge.activities.base.BaseActivity;

public class LoginActivity extends BaseActivity {

    public static final String isLoggedIn = "ISLOGGEDIN";

    Button btnLogin;
    EditText etEmail;
    EditText etPassword;
    TextView tvRegister;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        checkLoggedIn();
        initLayout();
        initListener();
    }

    private void checkLoggedIn(){
        boolean isLogIn = pref.getBoolean(isLoggedIn, false);
        if(isLogIn){
            successLogin();
        }
    }

    private void successLogin(){
        Intent intent = new Intent(this, ControlCenterv2.class);
        startActivity(intent);
        finish();
    }

    private void initListener(){
        btnLogin.setOnClickListener(v -> checkLogin());

        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });

    }

    private void checkLogin(){
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String truePassword = pref.getString(email, "-");
        if(password.equals(truePassword)){
            pref.edit().putBoolean(isLoggedIn, true).apply();
            successLogin();
        }else{
            Toast.makeText(this, "Password Salah", Toast.LENGTH_LONG).show();
        }

    }
    private void initLayout(){
        btnLogin = findViewById(R.id.btn_login);
        etEmail = findViewById(R.id.et_login_email);
        etPassword = findViewById(R.id.et_Login_password);
        tvRegister = findViewById(R.id.tv_login_toregister);

    }
}
