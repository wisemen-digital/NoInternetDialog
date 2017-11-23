package am.appwise.components.ni;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Created by robertlevonyan on 11/22/17.
 */

public class NetworkStatusReceiver extends BroadcastReceiver {
    private ConnectionCallback connectionCallback;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getExtras() != null) {
            NetworkInfo ni = (NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
            if (ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {
                Log.i("app", "Network " + ni.getTypeName() + " connected");
                if (connectionCallback != null) {
                    connectionCallback.hasActiveConnection(true);
                }
            }
        }
        if (intent.getExtras().getBoolean(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)) {
            Log.d("app", "There's no network connectivity");
            if (connectionCallback != null) {
                connectionCallback.hasActiveConnection(false);
            }
        }
    }

    public void setConnectionCallback(ConnectionCallback connectionCallback) {
        this.connectionCallback = connectionCallback;
    }
}
