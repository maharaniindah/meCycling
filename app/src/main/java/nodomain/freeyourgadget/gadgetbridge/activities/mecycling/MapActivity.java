package nodomain.freeyourgadget.gadgetbridge.activities.mecycling;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.R;
import nodomain.freeyourgadget.gadgetbridge.activities.base.BaseActivity;
import nodomain.freeyourgadget.gadgetbridge.activities.mecycling.model.Distance;
import nodomain.freeyourgadget.gadgetbridge.activities.mecycling.model.Speed;
import nodomain.freeyourgadget.gadgetbridge.activities.mecycling.model.UserLocation;
import nodomain.freeyourgadget.gadgetbridge.activities.mecycling.util.Helpers;
import nodomain.freeyourgadget.gadgetbridge.model.ActivitySample;
import nodomain.freeyourgadget.gadgetbridge.model.DeviceService;

import static nodomain.freeyourgadget.gadgetbridge.activities.mecycling.ProfileActivity.userWeight;

public class MapActivity extends BaseActivity implements OnMapReadyCallback {

    public static final String userLocation = "userLocation";
    public static final String userDistances = "userDistances";
    public static final String userSpeeds = "userSpeeds";
    public static final String isUserCycling = "isUserCycling";

    private Polyline mutablePolyline;
    private FusedLocationProviderClient fusedLocationClient;
    private GoogleMap googleMap;
    private Timer timer;
    private Timer timerHeartRate;
    private TextView textDistanceUser;
    private TextView textSpeedUser;
    private Button btnStop;

    private double distance = 0;
    private double speedAvg = 0;
    private int BB = 0;
    private double EC = 0;
    private double calNeeds = 0;
    private int duration = 0;

    public void startTimer(){
        System.out.println("timer startd");
        if(timer != null){
            return;
        }

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run() {

                getMyLocationUpdate();
            }
        }, 0, 2000);
    }

    private void stop(){
        if(timer==null){
            return;
        }
        timer.cancel();
        timer = null;
    }

    private void stopTimerHeartRate(){
        if(timerHeartRate==null){
            return;
        }
        timerHeartRate.cancel();
        timerHeartRate = null;
    }

    public void startTimerHeartRate() { //timer buat scan heart rate
        if(timerHeartRate != null){
            return;
        }
        timerHeartRate = new Timer();
        timerHeartRate.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                GBApplication.deviceService().onHeartRateTest();
            }
        }, 0,60*1000); //datanya diambil tiap menit
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() { //buat ngambil data detak jantung
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(DeviceService.ACTION_REALTIME_SAMPLES.equals(action)) {
                Serializable message = intent.getSerializableExtra(DeviceService.EXTRA_REALTIME_SAMPLE);
                if (message instanceof ActivitySample) {
                    ActivitySample sample = (ActivitySample) message;
                    String msg = "heart rate: " + sample.getHeartRate();
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                    if(sample.getHeartRate() > Helpers.calculateMaxHeartRate(pref)){
                        Helpers.showNotification(getApplicationContext());
                    }
                }
            }
        }
    };
    @Override
    protected void onStop() {
        super.onStop();
        stop();
        startTimerHeartRate();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startTimer();
        startTimerHeartRate();
    }

    @SuppressLint("MissingPermission")
    public void getMyLocationUpdate() {

        System.out.println("getting location");
        Set<String> locs = pref.getStringSet(userLocation, new HashSet<>());
        List<UserLocation> userLocs = new ArrayList<>();
        String[] locsArr = locs.toArray(new String[0]);
        for (int i = 0; i < locsArr.length; i++) {
            userLocs.add(new UserLocation(locsArr[i]));
        }
        Collections.sort(userLocs);
        List<LatLng> latLngs = new ArrayList<>();
        for (int i = 0; i < userLocs.size(); i++) {
            latLngs.add(userLocs.get(i).latLng);
        }
        Set<String> distances = new HashSet<>();

        double speed = 0;
        if(latLngs.size() > 1){
            double lat1 = latLngs.get(latLngs.size() - 2).latitude;
            double lon1 = latLngs.get(latLngs.size() - 2).longitude;
            double lat2 = latLngs.get(latLngs.size() - 1).latitude;
            double lon2 = latLngs.get(latLngs.size() - 1).longitude;
            double dist = Helpers.calculateHaversine(lat1,lon1,lat2,lon2);
            speed = dist / (2*3600); //km/jam

            distances = new HashSet<>(pref.getStringSet(userDistances, new HashSet<>()));
            String value = dist + ";" + (distances.size()+1);
            distances.add(value);
            System.out.println("size dits "+distances.size()+" val: "+dist);

            Set<String> speedUser = new HashSet<>(pref.getStringSet(userDistances, new HashSet<>()));
            String valueSpeed = speed + ";" + (speedUser.size()+1);
            speedUser.add(valueSpeed);
            pref.edit()
                    .putStringSet(userDistances, distances)
                    .putStringSet(userSpeeds, speedUser)
                    .apply();

            if(!speedUser.isEmpty()){
                String[] speedArr = speedUser.toArray(new String[0]);
                List<Speed> speedList = new ArrayList<>();
                double sumSpeed = 0;
                for (String s : speedArr) {
                    speedList.add(new Speed(s));
                    sumSpeed += speedList.get(speedList.size() - 1).speed;
                }
                speedAvg = sumSpeed / speedList.size();
                int MET;
                if(speedAvg < 16){
                    MET = 4;
                } else if (speedAvg <= 19){
                    MET = 6;
                } else if (speedAvg <= 22){
                    MET = 8;
                }else if (speedAvg <= 26){
                    MET = 10;
                }else if (speedAvg <= 30){
                    MET = 12;
                } else {
                    MET = 14;
                }
                EC = ((MET * 7.7 * (BB * 2.2))/200) * (duration/60.0);
            }
        }
        double distanceUser = 0;
        if(!distances.isEmpty()){
            String[] distArr = distances.toArray(new String[0]);
            List<Distance> distanceList = new ArrayList<>();
            for(int i = 0; i<distArr.length; i++){
                distanceList.add(new Distance(distArr[i]));
                distanceUser += distanceList.get(distanceList.size() - 1).distance;
            }
        }
        System.out.println("got size: " + userLocs.size());
        System.out.println("distance : " + distanceUser);


        double finalDistanceUser = distanceUser;
        this.distance = distanceUser;
        double finalSpeed = speed;
        runOnUiThread(() -> {
            mutablePolyline.setPoints(latLngs);
            String distanceString = String.valueOf(finalDistanceUser);
            //TextView textDistanceUser = findViewById(R.id.tv_map_user_distance);
            distanceString = reduceDecimal(distanceString);
            String textLabel = getString(R.string.jarak_yang_ditempuh, distanceString);
            textDistanceUser.setText(textLabel);

            String speedString = String.valueOf(finalSpeed);
            speedString = reduceDecimal(speedString);
            String speedLabel = getString(R.string.kecepatan, speedString);
            textSpeedUser.setText(speedLabel);
        });

//        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location ->{
//            if(location != null){
//                List<LatLng> currentLines = mutablePolyline.getPoints();
//                LatLng newPoint = new LatLng(location.getLatitude(), location.getLongitude());
//                currentLines.add(newPoint);
//                Toast.makeText(this, "size polyline item: "+currentLines.size(), Toast.LENGTH_LONG).show();
//                mutablePolyline.setPoints(currentLines);
//            } else {
//                Toast.makeText(this, "got null location",Toast.LENGTH_LONG).show();
//            }
//        } );
    }

    private String reduceDecimal(String input){
        if(input.split("[.]").length > 1 && input.length() > 3){
            String main = input.split("[.]")[0];
            String decimal = input.split("[.]")[1];
            decimal = decimal.substring(0,3);
            input = main + "." + decimal;
        }
        return input;
    }

    private void setupHeartRateHanlder(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(DeviceService.ACTION_REALTIME_SAMPLES);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, filter);
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        System.out.println("test oncreate");
        setupHeartRateHanlder();
        BB = pref.getInt(userWeight, 0);
        textDistanceUser = findViewById(R.id.tv_map_user_distance);
        textSpeedUser = findViewById(R.id.tv_map_user_speed);
        btnStop = findViewById(R.id.btn_map_stop);

        textDistanceUser.setText(getString(R.string.jarak_yang_ditempuh, "0"));
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_cycling);
        if(mapFragment != null && !this.isFinishing()){
            mapFragment.getMapAsync(this);
        }else{
            Toast.makeText(this, "got null map", Toast.LENGTH_LONG).show();
        }
        btnStop.setOnClickListener(v ->{
            calNeeds = Helpers.calculateCaloriesNeed(pref);
            System.out.println("duration: "+duration);
            pref.edit().putBoolean(isUserCycling, false).apply();
            Intent intent = new Intent(this, ResultActivity.class);
            intent.putExtra("distance", reduceDecimal(String.valueOf(distance)));
            intent.putExtra("speed", reduceDecimal(String.valueOf(speedAvg)));
            intent.putExtra("cal_burn", reduceDecimal(String.valueOf(EC)));
            intent.putExtra("cal_need", reduceDecimal(String.valueOf(calNeeds)));
            startActivity(intent);
            finish();
        });
        //mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap _googleMap) {
        googleMap = _googleMap;
        mutablePolyline = googleMap.addPolyline(new PolylineOptions()
        .color(getColor(R.color.blacklist_checkboxes))
        .width(2f)
     //   .clickable(clickabilityCheckBox.isChecked())
        .add());
        System.out.println("test on map ready");

        setUpTracker(googleMap);
        checkPermission();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 123) {
            checkPermission();
        }
    }

    private void checkPermission(){
        System.out.println("checking permission");
        boolean fineLocation = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED;
        boolean coarseLocation = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED;

        if (fineLocation && coarseLocation) {
            String[] permissions  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
            ActivityCompat.requestPermissions(this, permissions, 123);
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(this, "Permission needed", Toast.LENGTH_LONG).show();
            return;
        }
        boolean isAlarmRunning = pref.getBoolean("alarm",false);
        if(!isAlarmRunning) {
            setAlarm(this);
            new Timer().scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    duration += 1;
                }
            }, 0, 1000);
            pref.edit().putBoolean("alarm",true).apply();
        }
    }

    private void setUpTracker(GoogleMap googleMap){

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        boolean fineLocation = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED;
        boolean coarseLocation = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED;

        if (fineLocation && coarseLocation) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(this, "Permission needed", Toast.LENGTH_LONG).show();
            return;
        }
        googleMap.setMyLocationEnabled(true);
    }

    private static void setAlarm(Activity activity) {
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @SuppressLint("MissingPermission")
            @Override
            public void onReceive(Context context, Intent intent) {
                System.out.println("onreceiive");
                context.unregisterReceiver(this);
                SharedPreferences pref = activity.getSharedPreferences(activity.getString(R.string.app_name),activity.MODE_PRIVATE);
                int prevCounter = 0;
                prevCounter = pref.getInt("counter",0);
                prevCounter++;
                pref.edit().putInt("counter",prevCounter).apply();
                System.out.println("on receive pre location. counter: "+prevCounter);
                boolean isUserCyclingStatus = pref.getBoolean(isUserCycling, false);
                LocationServices.getFusedLocationProviderClient(activity).getLastLocation().addOnSuccessListener(activity, location -> {
                    if(location != null) {
                        Set<String> userLocs = new HashSet<>(pref.getStringSet(userLocation, new HashSet<>()));
                        double lat = location.getLatitude();
                        double lon = location.getLongitude();
                        String latlngUser = lat + ";" + lon + ";" + (userLocs.size()+1);
                        System.out.println("test loc: "+latlngUser);
                        userLocs.add(latlngUser);
                        pref.edit().putStringSet(userLocation, userLocs).apply();
                    } else {
                        System.out.println("got null location");
                    }
                });
                if(isUserCyclingStatus) {
                    setAlarm(activity);
                }
            }


        };

        activity.registerReceiver(receiver, new IntentFilter(userLocation));

        PendingIntent pintent = PendingIntent.getBroadcast(activity, 0, new Intent(userLocation),0 );
        AlarmManager manager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 1000*2, pintent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
    }
}
