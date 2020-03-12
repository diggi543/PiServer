package ru.oleg543.picontrol.network;

import org.json.JSONException;
import org.json.JSONObject;

import ru.oleg543.picontrol.utils.Helper;

public class SendData {
    private JSONObject mSendData;

    public SendData() {
        mSendData = new JSONObject();
    }

    public void put(String key, Object value) {
        try {
            mSendData.putOpt(key, value);
        } catch (JSONException pE) {
            Helper.log(pE);
        }
    }

    public byte[] get() {
        return mSendData.toString().getBytes();
    }
}
