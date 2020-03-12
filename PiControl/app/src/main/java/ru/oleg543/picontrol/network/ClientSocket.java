package ru.oleg543.picontrol.network;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

import ru.oleg543.picontrol.utils.Helper;

public class ClientSocket {
    static private int mPort = 3333;
    static private String mAddress = "192.168.0.1";
    static private boolean mAuthorized = false;
    static private Socket mSocket = null;
    static private OutputStream mOutputStream;
    static private Callback mCallback;
    static private FixInputStream mInputStream;

    public ClientSocket(Helper pHelper) {
            mCallback = pHelper;
    }

    public void send(String pCommand) {
        if (pCommand != null) {
            new Command(pCommand).execute();
        }
    }

    public void auth(String pUsername, String pPassword) {
        new Auth(pUsername, pPassword).execute();
    }

    public void close() {
        if (mSocket != null) {
            try {
                mSocket.close();
            } catch (IOException pE) {
                Helper.log(pE);
            }
        }
    }

    static class Command extends AsyncTask<Void, Void, Void> {
        private String mCommand;
        private ResponseData mResponse;

        Command(String pCommand) {
            mCommand = pCommand;
        }

        @Override
        protected Void doInBackground(final Void... pVoids) {
            try {
                Helper.dlog("Send to server command: " + mCommand);
                SendData sendData = new SendData();
                sendData.put("command", mCommand);
                mOutputStream.write(sendData.get());
                mOutputStream.flush();
                Helper.dlog("Wait for mResponse from server...");

                mResponse = new ResponseData(mInputStream.readString());
                if (mResponse.isValid()) {
                    Helper.dlog("Receive mResponse from server: " + mResponse.toString());
                }

            } catch (IOException pE) {
                Helper.log(pE);
            }
            return null;
        }

        @Override
        protected void onPostExecute(final Void pVoid) {
            super.onPostExecute(pVoid);
            mCallback.onSuccess(mResponse);
        }
    }

    static class Auth extends AsyncTask<Void, Void, Void> {
        private String mUsername;
        private String mPassword;
        private Throwable mError = null;
        private ResponseData mResponse;

        Auth(String pUsername, String pPassword) {
            mUsername = pUsername;
            mPassword = pPassword;
        }

        @Override
        protected Void doInBackground(final Void... pVoids) {
            try {
                mSocket = new Socket(InetAddress.getByName(mAddress), mPort);
                mInputStream = new FixInputStream(mSocket.getInputStream());
                mOutputStream = mSocket.getOutputStream();
                SendData sendData = new SendData();

                sendData.put("login", mUsername);
                sendData.put("password", mPassword);
                mOutputStream.write(sendData.get());
                mOutputStream.flush();
                Helper.dlog("Data was sent");

                mResponse = new ResponseData(mInputStream.readString());
                Helper.dlog("Received response: " + mResponse);
                
            } catch (IOException pE) {
                Helper.log(pE);
                mError = pE;
            }
            return null;
        }

        @Override
        protected void onPostExecute(final Void pVoid) {
            super.onPostExecute(pVoid);
            if (mError != null) {
                mCallback.onFail(mError);
            } else {
                mCallback.onSuccess(mResponse);
            }
        }
    }

    public interface Callback {
        public void onFail(Throwable pResult);

        public void onSuccess(ResponseData pResult);
    }
}
