package ru.oleg543.picontrol.utils;

import android.app.AlertDialog;
import android.content.Context;

public class Dialog {
    AlertDialog mDialog;

    public Dialog(Context pContext, String pTitle, String pMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(pContext);
        builder.setTitle(pTitle).setMessage(pMessage);
        mDialog = builder.create();
    }

    public void show() {
        mDialog.show();
    }
}
