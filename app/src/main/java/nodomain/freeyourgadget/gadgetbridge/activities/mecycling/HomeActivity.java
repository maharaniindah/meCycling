package nodomain.freeyourgadget.gadgetbridge.activities.mecycling;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.List;

import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.R;
import nodomain.freeyourgadget.gadgetbridge.activities.ControlCenterv2;
import nodomain.freeyourgadget.gadgetbridge.activities.base.BaseActivity;
import nodomain.freeyourgadget.gadgetbridge.activities.mecycling.util.Helpers;
import nodomain.freeyourgadget.gadgetbridge.devices.DeviceManager;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;

import static nodomain.freeyourgadget.gadgetbridge.activities.ControlCenterv2.connectedDeviceTag;
import static nodomain.freeyourgadget.gadgetbridge.activities.mecycling.MapActivity.isUserCycling;
import static nodomain.freeyourgadget.gadgetbridge.activities.mecycling.MapActivity.userDistances;
import static nodomain.freeyourgadget.gadgetbridge.activities.mecycling.MapActivity.userLocation;
import static nodomain.freeyourgadget.gadgetbridge.activities.mecycling.ProfileActivity.userBirthYear;
import static nodomain.freeyourgadget.gadgetbridge.activities.mecycling.ProfileActivity.userGender;
import static nodomain.freeyourgadget.gadgetbridge.activities.mecycling.ProfileActivity.userHeight;
import static nodomain.freeyourgadget.gadgetbridge.activities.mecycling.ProfileActivity.userWeight;

public class HomeActivity extends BaseActivity {

    Button btnStart;
    Button btnProfile;
    Button btnLogout;
    ImageView ivDisconnect;
    DeviceManager deviceManager;
    List<GBDevice> deviceList;
    GBDevice connectedDevice;
    GBDevice previouslyConnectedDevice;
    String previouslyConnectedDeviceTag;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initLayout();
        initListener();
        pref.edit()
                .remove(userLocation)
                .remove("alarm")
                .remove("counter")
                //.remove(userDistances)
                .apply();

        previouslyConnectedDeviceTag = pref.getString(connectedDeviceTag, "");
        deviceManager = ((GBApplication) getApplication()).getDeviceManager();
        deviceList = deviceManager.getDevices();
        for(int i = 0; i<deviceList.size(); i++){
            if(deviceList.get(i).isConnected()){
                connectedDevice = deviceList.get(i);
            }
            if(deviceList.get(i).toString().equals(previouslyConnectedDeviceTag)){
                previouslyConnectedDevice = deviceList.get(i);
            }
        }

        findViewById(R.id.test_notif).setOnClickListener(v-> {
            Helpers.showNotification(this);
        });
    }

    private void initLayout(){
        btnLogout = findViewById(R.id.btn_home_logout);
        btnProfile = findViewById(R.id.btn_home_profile);
        btnStart = findViewById(R.id.btn_home_start);
        ivDisconnect = findViewById(R.id.iv_disconnect);
    }

    private void initListener(){
        btnLogout.setOnClickListener(v->{
            pref.edit().remove(LoginActivity.isLoggedIn).apply();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });
        btnStart.setOnClickListener(v->{

            if(deviceManager.getDevices().isEmpty()){
                Helpers.showAlertDeviceDisconnected();
                Intent intent =  new Intent(this, ControlCenterv2.class);
                startActivity(intent);
                finish();
                return;
            }
            int userWeightData = pref.getInt(userWeight, 0);
            int userHeightData = pref.getInt(userHeight, 0);
            int userBirthYearData = pref.getInt(userBirthYear, 0);
            int userGenderCode = pref.getInt(userGender, 0);

            boolean isValid = userWeightData != 0 &&
                              userHeightData != 0 &&
                              userBirthYearData != 0 &&
                              userGenderCode != 0;
            if(!isValid){
                String message = "Silahkan lengkapi data pada halaman profile terlebih dahulu";
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                return;
            }
            pref.edit().putBoolean(isUserCycling, true).apply();
            Intent intent = new Intent(this, MapActivity.class);
            startActivity(intent);
        });

        btnProfile.setOnClickListener(v->{
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        });

        ivDisconnect.setOnClickListener(v -> {
            if(connectedDevice != null){
                GBApplication.deviceService().disconnect();
            }else{
                GBApplication.deviceService().connect(previouslyConnectedDevice);
            }
        });
    }


}
