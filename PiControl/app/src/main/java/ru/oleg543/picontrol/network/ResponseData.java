package ru.oleg543.picontrol.network;

import org.json.JSONException;
import org.json.JSONObject;

import ru.oleg543.picontrol.utils.Helper;

public class ResponseData {
    private JSONObject mResponseData;

    public ResponseData(String pData) {
        try {
            mResponseData = new JSONObject(pData);
        } catch (JSONException pE) {
            Helper.log(pE);
            try {
                mResponseData = new JSONObject("{}");
            } catch (JSONException pE1) {
                Helper.log(pE1);
            }
        }
    }

    public boolean isValid() {
        return mResponseData != null;
    }

    public boolean contains(String key) {
        return mResponseData.has(key);
    }

    public boolean getBoolean(String pKey) {
        return mResponseData.optBoolean(pKey);
    }

    public String getString(String pKey) {
        return mResponseData.optString(pKey);
    }

    @Override
    public String toString() {
        return mResponseData.toString();
    }
}
