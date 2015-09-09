package com.scipio.luke.stormy;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by Luke on 7/26/2015.
 */
public class AsyncTaskTest extends AsyncTask<Void, Integer, String> {

    public static final String TAG = AsyncTaskTest.class.getSimpleName();
    private ProgressDialog pd;
    Activity mActivity;
    int progressStatus;

    public AsyncTaskTest(Activity activity) {
        mActivity = activity;
    }


    @Override
    protected void onPreExecute(){
        Log.d(TAG, "On preExecute...");
        super.onPreExecute();

        pd = ProgressDialog.show(mActivity,"Progress","Loading....",true);
    }

    @Override
    protected String doInBackground(Void...arg0) {
        Log.d(TAG,"On doInBackground...");

        while (progressStatus < 100) {
            progressStatus += 1;
            try {
                // Sleep for 20 milliseconds to display the progress slowly
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            publishProgress(progressStatus);
        }
        return "You are at PostExecute";
    }

    @Override
    protected void onProgressUpdate(Integer...a){
        Log.d(TAG,"You are in progress update ... " + a[0]);
        pd.setMessage("Loading.."+a[0]+"%");
    }

    @Override
    protected void onPostExecute(String result) {
        Log.d(TAG,result);
        pd.cancel();
        pd.dismiss();
    }
}

