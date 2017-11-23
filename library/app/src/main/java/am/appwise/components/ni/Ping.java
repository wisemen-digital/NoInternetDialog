package am.appwise.components.ni;

import android.content.Context;
import android.os.AsyncTask;

/**
 * Created by robertlevonyan on 11/21/17.
 */

class Ping extends AsyncTask<Context, Void, Boolean> {
    private ConnectionCallback connectionCallback;

    @Override
    protected Boolean doInBackground(Context... ctxs) {
        return NoInternetUtils.isConnectedToInternet(ctxs[0]) && NoInternetUtils.hasActiveInternetConnection(ctxs[0]);
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if (connectionCallback != null) {
            connectionCallback.hasActiveConnection(aBoolean);
        }
    }

    public void setConnectionCallback(ConnectionCallback connectionCallback) {
        this.connectionCallback = connectionCallback;
    }
}