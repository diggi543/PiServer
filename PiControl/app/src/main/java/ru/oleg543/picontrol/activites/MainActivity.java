package ru.oleg543.picontrol.activites;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import ru.oleg543.picontrol.R;
import ru.oleg543.picontrol.utils.Helper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        findViewById(R.id.btn_connect).setOnClickListener(new Helper.ClickListener());
        new Helper(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Helper(this);
    }
}
