package com.wordsbaking.cordova.tts;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.UtteranceProgressListener;

import java.util.HashMap;
import java.util.Locale;
import java.util.*;

import org.apache.cordova.PluginResult;
import org.apache.cordova.PluginResult.Status;

import android.content.Intent;
import android.content.Context;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.speech.tts.Voice;

/*
    Cordova Text-to-Speech Plugin
    https://github.com/vilic/cordova-plugin-tts

    by VILIC VANE
    https://github.com/vilic

    MIT License
*/

public class TTS extends CordovaPlugin implements OnInitListener {

    public static final String ERR_INVALID_OPTIONS = "ERR_INVALID_OPTIONS";
    public static final String ERR_NOT_INITIALIZED = "ERR_NOT_INITIALIZED";
    public static final String ERR_ERROR_INITIALIZING = "ERR_ERROR_INITIALIZING";
    public static final String ERR_UNKNOWN = "ERR_UNKNOWN";

    boolean ttsInitialized = false;
    TextToSpeech tts = null;
    Context context = null;

    @Override
    public void initialize(CordovaInterface cordova, final CordovaWebView webView) {
        context = cordova.getActivity().getApplicationContext();
        tts = new TextToSpeech(cordova.getActivity().getApplicationContext(), this, "com.google.android.tts");
        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String s) {
                // do nothing
            }

            @Override
            public void onDone(String callbackId) {
                if (!callbackId.equals("")) {
                    CallbackContext context = new CallbackContext(callbackId, webView);
                    context.success();
                }
            }

            @Override
            public void onError(String callbackId) {
                if (!callbackId.equals("")) {
                    CallbackContext context = new CallbackContext(callbackId, webView);
                    context.error(ERR_UNKNOWN);
                }
            }
        });
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext)
            throws JSONException {
        if (action.equals("speak")) {
            speak(args, callbackContext);
        } else if (action.equals("stop")) {
            stop(args, callbackContext);
        } else if (action.equals("checkLanguage")) {
            checkLanguage(args, callbackContext);
        } else if (action.equals("openInstallTts")) {
            callInstallTtsActivity(args, callbackContext);
        } else {
            return false;
        }
        return true;
    }

    @Override
    public void onInit(int status) {
        if (status != TextToSpeech.SUCCESS) {
            tts = null;
        } else {
            // warm up the tts engine with an empty string
            HashMap<String, String> ttsParams = new HashMap<String, String>();
            ttsParams.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "");
            tts.setLanguage(new Locale("en", "US"));
            tts.speak("", TextToSpeech.QUEUE_FLUSH, ttsParams);

            ttsInitialized = true;
        }
    }

    private void stop(JSONArray args, CallbackContext callbackContext)
      throws JSONException, NullPointerException {
        tts.stop();
    }

    private void callInstallTtsActivity(JSONArray args, CallbackContext callbackContext)
      throws JSONException, NullPointerException {

        PackageManager pm = context.getPackageManager();
        Intent installIntent = new Intent();
        installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
        ResolveInfo resolveInfo = pm.resolveActivity( installIntent, PackageManager.MATCH_DEFAULT_ONLY );

        if( resolveInfo == null ) {
           // Not able to find the activity which should be started for this intent
        } else {
          installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
          context.startActivity(installIntent);
        }
    }


    private void checkLanguage(JSONArray args, CallbackContext callbackContext)
      throws JSONException, NullPointerException {
        Set<Locale> supportedLanguages = tts.getAvailableLanguages();
        String languages = "";
        if(supportedLanguages!= null) {
            for (Locale lang : supportedLanguages) {
                languages = languages + "," + lang;
            }
        }
        if (languages != "") {
            languages = languages.substring(1);
        }

        final PluginResult result = new PluginResult(PluginResult.Status.OK, languages);
        callbackContext.sendPluginResult(result);
    }

    private void speak(JSONArray args, CallbackContext callbackContext)
            throws JSONException, NullPointerException {
        JSONObject params = args.getJSONObject(0);

        if (params == null) {
            callbackContext.error(ERR_INVALID_OPTIONS);
            return;
        }

        String text;
        String locale;
        double rate;
        String voiceType;

        if (params.isNull("text")) {
            callbackContext.error(ERR_INVALID_OPTIONS);
            return;
        } else {
            text = params.getString("text");
        }

        if (params.isNull("locale")) {
            locale = "en-US";
        } else {
            locale = params.getString("locale");
        }

        if (params.isNull("rate")) {
            rate = 1.0;
        } else {
            rate = params.getDouble("rate");
        }

        if (tts == null) {
            callbackContext.error(ERR_ERROR_INITIALIZING);
            return;
        }

        if (!ttsInitialized) {
            callbackContext.error(ERR_NOT_INITIALIZED);
            return;
        }

        HashMap<String, String> ttsParams = new HashMap<String, String>();
        ttsParams.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, callbackContext.getCallbackId());

        String[] localeArgs = locale.split("-");
        tts.setLanguage(new Locale(localeArgs[0], localeArgs[1]));

        voiceType = params.getString("voiceType");

        if (params.isNull("voiceType")) {
            voiceType = "Male";
        } else {
            voiceType = params.getString("voiceType");
        }

        if (voiceType.compareTo("Male") == 0) {
            Voice voiceObj = new Voice("en-us-x-sfg#male_1-local",
                                       Locale.getDefault(), 1, 1, false, null);
            tts.setVoice(voiceObj);
        }
        else {
            Voice voiceObj = new Voice("en-us-x-sfg#female_1-local",
                                       Locale.getDefault(), 1, 1, false, null);
            tts.setVoice(voiceObj);
        }
        
        if (Build.VERSION.SDK_INT >= 27) {
            tts.setSpeechRate((float) rate * 0.7f);
        } else {
            tts.setSpeechRate((float) rate);
        }

        tts.speak(text, TextToSpeech.QUEUE_FLUSH, ttsParams);
    }
}
