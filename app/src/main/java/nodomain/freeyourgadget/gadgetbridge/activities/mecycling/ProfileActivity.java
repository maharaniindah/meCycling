package nodomain.freeyourgadget.gadgetbridge.activities.mecycling;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.Nullable;

import nodomain.freeyourgadget.gadgetbridge.R;
import nodomain.freeyourgadget.gadgetbridge.activities.base.BaseActivity;

public class ProfileActivity extends BaseActivity {

    private static final String userHeight = "userHeight";
    private static final String userWeight = "userWeight";
    private static final String userGender = "userGender";
    private static final String userBirthYear = "userBirthYear";

    EditText etHeight;
    EditText etWeight;
    RadioButton rbMale;
    RadioButton rbFemale;
    Button btnSave;
    EditText etBirthDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initLayout();
        initListener();
        initData();
    }

    private void initLayout(){
        etHeight =findViewById(R.id.et_profile_height);
        etWeight = findViewById(R.id.et_profile_weight);
        rbMale = findViewById(R.id.rb_profile_male);
        rbFemale = findViewById(R.id.rb_profile_female);
        btnSave = findViewById(R.id.btn_profile_save);
        etBirthDate = findViewById(R.id.et_profile_birthdate);
    }

    private void initData(){
        int height = pref.getInt(userHeight, 0);
        int weight = pref.getInt(userWeight, 0);
        int gender = pref.getInt(userHeight, -1);
        int year = pref.getInt(userBirthYear, 0);

        if(height > 0){
            etHeight.setText(height + "cm");
        }
        if(weight > 0){
            etWeight.setText(weight + "kg");
        }
        if(year > 0){
            etBirthDate.setText(String.valueOf(year));
        }
        if(gender == 1){
            rbMale.setChecked(true);
        }
        if(gender == 0){
            rbFemale.setChecked(true);
        }

    }
    private void initListener(){
        btnSave.setOnClickListener(v->{
            String heightStr = etHeight.getText().toString().trim().replace("cm", "");
            int height = 0;
            if(!heightStr.isEmpty()){
                height = Integer.parseInt(etHeight.getText().toString().trim());
            }

            String weightStr = etWeight.getText().toString().trim().replace("cm", "");
            int weight = 0;
            if(!weightStr.isEmpty()){
                weight = Integer.parseInt(weightStr);
            }

            String yearStr = etBirthDate.getText().toString().trim();
            int year = 0;
            if(!yearStr.isEmpty()){
                year = Integer.parseInt(yearStr);
            }
            boolean isMaleChecked = rbMale.isChecked() && !rbFemale.isChecked();
            boolean isFemaleChecked = !rbMale.isChecked() && rbFemale.isChecked();
            int userGenderMale;

            if(isMaleChecked){
                userGenderMale = 1;
            }else if(isFemaleChecked){
                userGenderMale = 0;
            }else{
                Toast.makeText(this, "isi data jenis kelamin", Toast.LENGTH_LONG).show();
                return;
            }
            pref.edit()
                    .putInt(userHeight, height)
                    .putInt(userWeight, weight)
                    .putInt(userGender, userGenderMale)
                    .putInt(userBirthYear, year)
                    .apply();

            Toast.makeText(this, "Berhasil Simpan Data", Toast.LENGTH_LONG).show();

        });
    }
}
