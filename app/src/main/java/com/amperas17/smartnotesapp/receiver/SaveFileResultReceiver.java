package com.amperas17.smartnotesapp.receiver;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

/**
 * Get result from SaveFileService if the file was saved.
 */
public class SaveFileResultReceiver  extends ResultReceiver {
    public static String TAG = "receiverTag";
    private Receiver mReceiver;

    public SaveFileResultReceiver(Handler handler) {
        super(handler);
    }

    public interface Receiver {
        void onReceiveResult(int resultCode, Bundle resultData);
    }

    public void setReceiver(Receiver receiver) {
        mReceiver = receiver;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (mReceiver != null) {
            mReceiver.onReceiveResult(resultCode, resultData);
        }
    }

}