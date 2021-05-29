package nodomain.freeyourgadget.gadgetbridge.activities.base;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import nodomain.freeyourgadget.gadgetbridge.R;

public abstract class BaseActivity extends AppCompatActivity {

    public SharedPreferences pref;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
    }
}
