package com.duddu.antitampering;

import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;


public class AntiTamperingPlugin extends CordovaPlugin {
    private static String LOG_TAG = "TAMPERING";

    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        checkAndStopExecution();
        super.initialize(cordova, webView);
    }

    private void checkAndStopExecution() {
        Log.i(LOG_TAG, "Tampering check started");
        try {
            AssetsIntegrity.check(cordova.getActivity().getAssets());
            SignatureChecking.validateAppSignature(cordova.getActivity());
            Log.i(LOG_TAG, "Tampering check successful");
        } catch (final Exception e) {
            cordova.getActivity().runOnUiThread(new Runnable() {
                public void run () {
                    e.printStackTrace();
                    throw new TamperingException("Anti-Tampering check failed");
                }
            });
        }
    }

    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {

        if ("verify".equals(action)) {
            cordova.getThreadPool().execute(new Runnable() {
                @Override
                public void run () {
                    PluginResult result;
                    try {
                        JSONObject response = new JSONObject();
                        response.put("assets", AssetsIntegrity.check(cordova.getActivity().getAssets()));
                        result = new PluginResult(PluginResult.Status.OK, response);
                    } catch (Exception e) {
                        result = new PluginResult(PluginResult.Status.ERROR, e.toString());
                    }
                    callbackContext.sendPluginResult(result);
                }
            });
            return true;
        }

        return false;

    }

}
