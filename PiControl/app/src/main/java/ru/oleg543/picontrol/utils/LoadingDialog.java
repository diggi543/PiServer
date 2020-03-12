package ru.oleg543.picontrol.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import ru.oleg543.picontrol.R;

public class LoadingDialog {
    private static AlertDialog mDialog;

    public LoadingDialog(Context pContext) {
        AlertDialog.Builder builder = new AlertDialog.Builder(pContext);
        View view = LayoutInflater.from(pContext).inflate(R.layout.dialog_loading, null);
        builder.setView(view).setCancelable(false);
        mDialog = builder.create();
    }

    public static void show() {
        mDialog.show();
        mDialog.getWindow().setLayout(200, 200);
    }

    public static void hide() {
        mDialog.cancel();
    }
}
