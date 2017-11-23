package am.appwise.components.ni;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by robertlevonyan on 11/21/17.
 */

class WifiReceiver extends BroadcastReceiver {
    private ConnectionListener connectionListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMan.getActiveNetworkInfo();

        if (netInfo != null && netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            if (connectionListener != null) {
                connectionListener.onWifiTurnedOn();
            }
        } else {
            if (connectionListener != null) {
                connectionListener.onWifiTurnedOff();
            }
        }
    }

    public void setConnectionListener(ConnectionListener connectionListener) {
        this.connectionListener = connectionListener;
    }

}
