package ru.oleg543.picontrol.activites;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import ru.oleg543.picontrol.R;
import ru.oleg543.picontrol.utils.Helper;

public class ControlActivity extends AppCompatActivity {
    private ToggleButton mButtonLight;
    private ToggleButton mButtonMoution;
    private ToggleButton mButtonSound;
    private ToggleButton mButtonEnableLight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        init();
        Helper.getInfo();
    }

    private void init() {
        mButtonLight = findViewById(R.id.btn_mode_light);
        mButtonSound = findViewById(R.id.btn_mode_sound);
        mButtonMoution = findViewById(R.id.btn_mode_moution);
        mButtonEnableLight = findViewById(R.id.btn_light);
        mButtonEnableLight.setOnClickListener(new Helper.ClickListener());
        mButtonLight.setOnClickListener(new Helper.ClickListener());
        mButtonMoution.setOnClickListener(new Helper.ClickListener());
        mButtonSound.setOnClickListener(new Helper.ClickListener());
        mButtonSound.setChecked(true);
        mButtonMoution.setChecked(true);
        mButtonLight.setChecked(true);

        Helper.putInfoTextView((TextView) findViewById(R.id.tv_info));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Helper.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Helper(this);
    }
}
