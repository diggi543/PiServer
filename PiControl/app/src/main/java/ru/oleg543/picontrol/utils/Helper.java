package ru.oleg543.picontrol.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import ru.oleg543.picontrol.R;
import ru.oleg543.picontrol.activites.ControlActivity;
import ru.oleg543.picontrol.network.ClientSocket;
import ru.oleg543.picontrol.network.ResponseData;

public class Helper implements ClientSocket.Callback {
    private static final String TAG = "oleg543@picontrol";
    private static final boolean DEBUG = true;
    private static Context sContext;
    private static Helper mThis;
    private static TextView mInfoTextView;
    private static ClientSocket sClientSocket;

    public Helper(Context pContext) {
        sContext = pContext;
        mThis = this;
    }

    public static void putInfoTextView(TextView pTextView) {
        mInfoTextView = pTextView;
    }

    public static void getInfo() {
        sClientSocket = new ClientSocket(mThis);
        sClientSocket.send("getInfo");
    }

    private static void tryToConnect(View pView) {
        Helper.dlog("Try to connect");
        EditText mEtUsername = pView.getRootView().findViewById(R.id.et_username);
        EditText mEtPassword = pView.getRootView().findViewById(R.id.et_password);
        String username = mEtUsername.getText().toString();
        String password = mEtPassword.getText().toString();
        if (username.isEmpty()) {
            mEtUsername.setError("Cannot been empty!");
            mEtUsername.requestFocus();
        } else if (password.isEmpty()) {
            mEtPassword.setError("Cannot been empty!");
            mEtPassword.requestFocus();
        } else {
            LoadingDialog.show();
            sClientSocket = new ClientSocket(mThis);
            sClientSocket.auth(username, password);
        }
    }

    private static void tryToTurnOnLight() {
        Helper.dlog("Try to turn on light");
        sClientSocket = new ClientSocket(mThis);
        sClientSocket.send("turnOnLight");
    }

    private static void tryToTurnOffLight() {
        Helper.dlog("Try to turn off light");
        sClientSocket = new ClientSocket(mThis);
        sClientSocket.send("turnOffLight");
    }

    private static void tryToChangeMode(String mode, boolean state) {
        sClientSocket = new ClientSocket(mThis);
        sClientSocket.send("change_mode_" + mode + "_" + state);
    }

    public static void log(Object object) {
        Log.d(TAG, object.toString());
    }

    public static void dlog(Object object) {
        if (DEBUG) {
            Log.d(TAG, object.toString());
        }
    }

    public static void onAuthResult(boolean pSuccess) {
        if (!pSuccess) {
            Toast.makeText(sContext, "Invalid pair username/password", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onFail(final Throwable pResult) {
        LoadingDialog.hide();

        if (pResult != null) {
            Toast.makeText(sContext, pResult.toString(), Toast.LENGTH_LONG).show();
//            new Dialog(sContext, "Error", pResult.toString()).show();
            pResult.printStackTrace();
        }
    }

    @Override
    public void onSuccess(final ResponseData pResult) {
        LoadingDialog.hide();
        if (pResult == null) {
            return;
        }
        if (pResult.contains("auth")) {
            if (pResult.getBoolean("auth")) {
                Intent intent = new Intent(sContext, ControlActivity.class);
                sContext.startActivity(intent);
            } else {
                Toast.makeText(sContext, "Invalid login/password!", Toast.LENGTH_SHORT).show();
                sClientSocket.close();
            }
        } else if (pResult.contains("error")) {
            Toast.makeText(sContext, pResult.getString("error"), Toast.LENGTH_SHORT).show();
        } else if (pResult.contains("notification")) {
            Toast.makeText(sContext, pResult.getString("notification"), Toast.LENGTH_SHORT).show();
        } else if (pResult.contains("info")) {
            mInfoTextView.setText(pResult.getString("info"));
        }
    }

    public static void onDestroy() {
        sClientSocket.close();
    }

    public static class ClickListener implements View.OnClickListener {

        @Override
        public void onClick(View pView) {
            ToggleButton btn;

            switch (pView.getId()) {
                case R.id.btn_connect:
                    Helper.dlog("Click on btn.connect");
                    new LoadingDialog(sContext);
                    tryToConnect(pView);
                    break;
                case R.id.btn_light:
                    Helper.dlog("Click on light btn");
                    btn = (ToggleButton) pView;
                    if (btn.isChecked()) {
                        tryToTurnOnLight();
                    } else {
                        tryToTurnOffLight();
                    }
                    break;
                case R.id.btn_mode_light:
                    Helper.dlog("Click on enable/disable light mode btn");
                    btn = (ToggleButton) pView;
                    tryToChangeMode("light", btn.isChecked());
                    break;
                case R.id.btn_mode_moution:
                    Helper.dlog("Click on enable/disable moution mode btn");
                    btn = (ToggleButton) pView;
                    tryToChangeMode("moution", btn.isChecked());
                    break;
                case R.id.btn_mode_sound:
                    Helper.dlog("Click on enable/disable sound mode btn");
                    btn = (ToggleButton) pView;
                    tryToChangeMode("sound", btn.isChecked());
                    break;
            }
        }
    }
}
