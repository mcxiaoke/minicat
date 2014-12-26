package com.mcxiaoke.minicat.task;

import android.content.Context;
import android.os.AsyncTask;
import com.mcxiaoke.minicat.AppContext;


/**
 * @param <Params>
 * @param <Progress>
 * @param <Result>
 * @author mcxiaoke
 * @version 1.1 2012.03.13
 */
public abstract class BetterAsyncTask<Params, Progress, Result> extends
        AsyncTask<Params, Progress, Result> {

    private final String callerId;
    private Exception error;

    public BetterAsyncTask(Context context) {
        super();
        this.callerId = context.getClass().getCanonicalName();
    }

    protected abstract void onPrepare(Context context);

    protected abstract void onPost(Context context, Result result);

    protected abstract void onError(Context context, Exception exception);

    protected abstract Result run(Params... params) throws Exception;

    @Override
    protected final Result doInBackground(Params... params) {
        try {
            return run(params);
        } catch (Exception e) {
            this.error = e;
            return null;
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Context context = getCallingCotext();
        if (context == null) {
            cancel(true);
            return;
        }
        onPrepare(context);
    }

    @Override
    protected final void onPostExecute(Result result) {
        super.onPostExecute(result);
        Context context = getCallingCotext();
        if (context == null) {
            return;
        }

        if (error == null) {
            onPost(context, result);
        } else {
            onError(context, error);
        }
    }

    protected void setError(Exception e) {
        this.error = e;
    }

    protected Context getCallingCotext() {
        Context caller = AppContext.getActiveContext(callerId);
        if (caller == null
                || !this.callerId.equals(caller.getClass().getCanonicalName())) {
            return null;
        }
        return caller;
    }

}
