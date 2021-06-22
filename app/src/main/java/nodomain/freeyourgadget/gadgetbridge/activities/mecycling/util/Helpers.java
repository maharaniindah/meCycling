package nodomain.freeyourgadget.gadgetbridge.activities.mecycling.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import java.util.Calendar;

import android.os.Build;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.R;

import static android.content.Context.NOTIFICATION_SERVICE;
import static nodomain.freeyourgadget.gadgetbridge.activities.mecycling.ProfileActivity.FEMALE_CODE;
import static nodomain.freeyourgadget.gadgetbridge.activities.mecycling.ProfileActivity.userBirthYear;
import static nodomain.freeyourgadget.gadgetbridge.activities.mecycling.ProfileActivity.userGender;
import static nodomain.freeyourgadget.gadgetbridge.activities.mecycling.ProfileActivity.userHeight;
import static nodomain.freeyourgadget.gadgetbridge.activities.mecycling.ProfileActivity.userWeight;

public class Helpers {
    public static double calculateHaversine(double lat1, double lon1, double lat2, double lon2){
        double lat1r = Math.toRadians(lat1);
        double lon1r = Math.toRadians(lon1);
        double lat2r = Math.toRadians(lat2);
        double lon2r = Math.toRadians(lon2);

        double deltaLat = lat2r - lat1r;
        double deltaLon = lon2r - lon1r;

        int r = 6371;
        double sin2x = Math.pow(Math.sin(deltaLat/2),2);
        double cosLat = Math.cos(lat1r);
        double cosLon = Math.cos(lat2r);
        double sin2y = Math.pow(Math.sin(deltaLon/2),2);

        double akar = Math.sqrt(sin2x + (cosLat*cosLon*sin2x));
        double asin = Math.asin(akar);

        return r * 2 * asin;
    }

    public static double calculateCaloriesNeed(SharedPreferences pref){

        int userWeightData = pref.getInt(userWeight, 0);
        int userHeightData = pref.getInt(userHeight, 0);
        int userGenderCode = pref.getInt(userGender, 0);
        int userAgeData = calculateUserAge(pref);

        double BMR = 0;
        if(userGenderCode == FEMALE_CODE){
            BMR = (447.6 + 9.25 * userWeightData) + (3.1 * userHeightData) - (4.33 * userAgeData);
            return 1.7 * BMR;
        }else{
            BMR = (88.4 + (13.4 * userWeightData)) + (4.8 * userWeightData) - (5.68 * userAgeData);
            return 1.76 * BMR;
        }
    }

    private static int calculateUserAge(SharedPreferences pref){
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        int userBirthYearData = pref.getInt(userBirthYear, 0);
        return thisYear - userBirthYearData;
    }

    public static double calculateMaxHeartRate(SharedPreferences pref){
        return((220-calculateUserAge(pref)) * 0.7);
   }

    public static void showNotification(Context ctx){
        String channelId = "Cycling";
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        long[] vibPattern = {1000,1000};
        NotificationManager notifManager = (NotificationManager) ctx.getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx, channelId)
                .setSound(alarmSound)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setVibrate(vibPattern)
                .setContentTitle("Warning")
                .setContentText("Anda Harus Istirahat")
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(channelId, channelId, importance);
            channel.enableVibration(true);
            channel.setVibrationPattern(vibPattern);
            AudioAttributes attr = new AudioAttributes.Builder()
                    .build();
            channel.setSound(alarmSound, attr);
            notifManager.createNotificationChannel(channel);
        }
        notifManager.notify(0, builder.build());
    }

    public static void showAlertDeviceDisconnected(){
        Toast.makeText(GBApplication.getContext(), "Silakan Koneksikan miband terlebih dahulu", Toast.LENGTH_LONG).show();
    }
}