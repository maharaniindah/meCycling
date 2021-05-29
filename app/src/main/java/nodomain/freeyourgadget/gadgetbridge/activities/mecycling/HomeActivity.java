package nodomain.freeyourgadget.gadgetbridge.activities.mecycling;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;

import nodomain.freeyourgadget.gadgetbridge.R;
import nodomain.freeyourgadget.gadgetbridge.activities.base.BaseActivity;

public class HomeActivity extends BaseActivity {

    Button btnStart;
    Button btnProfile;
    Button btnLogout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initLayout();
        initListener();
    }

    private void initLayout(){
        btnLogout = findViewById(R.id.btn_home_logout);
        btnProfile = findViewById(R.id.btn_home_profile);
        btnStart = findViewById(R.id.btn_home_start);
    }

    private void initListener(){
        btnLogout.setOnClickListener(v->{
            pref.edit().remove(LoginActivity.isLoggedIn).apply();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });
        btnStart.setOnClickListener(v->{
            Intent intent = new Intent(this, MapActivity.class);
            startActivity(intent);
        });

        btnProfile.setOnClickListener(v->{
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        });
    }


}
