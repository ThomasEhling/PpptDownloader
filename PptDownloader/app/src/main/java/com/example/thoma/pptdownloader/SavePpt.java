package com.example.thoma.pptdownloader;


import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.content.ContentValues.TAG;

public class SavePpt extends AsyncTask<String, Void, Void> {

    private OnTaskCompleted onComplete;

    public interface OnTaskCompleted {
        void onTaskCompleted();
        void onError();
    }

    public void onFinish(OnTaskCompleted onComplete) {
        this.onComplete = onComplete;
    }

    @Override
    protected Void doInBackground(String... sUrl) {
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(sUrl[0]);
            String filename = url.getFile();
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.e("ERROR", "Server didn't return 200. ");
                // handle error
                return null;
            }

            input = connection.getInputStream();
            output = new FileOutputStream("/sdcard/downloadedFiels/" + filename); // where to save file

            byte data[] = new byte[4096];
            int count;
            while ((count = input.read(data)) != -1) {
                if (isCancelled()) {
                    return null;
                }
                output.write(data, 0, count);
            }
            Log.v(TAG, "Download successful");
        } catch (Exception e) {
            // handle error
            Log.e(TAG, "Exception: ", e);
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException e) {
                Log.e(TAG, "IOException: ", e);
            }

            if (connection != null)
                connection.disconnect();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        try {
            onComplete.onTaskCompleted();
        } catch (Exception e) {
            e.printStackTrace();
            onComplete.onError();
        }
    }
}