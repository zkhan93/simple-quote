package io.github.zkhan93.simplequotes;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Downloader extends AsyncTask<String, Void, String> {

    interface DownloadCompleteListener {
        void onComplete(String result);
    }

    private DownloadCompleteListener listener;

    public Downloader(DownloadCompleteListener listener) {
        this.listener = listener;

    }

    public static String TAG = Downloader.class.getSimpleName();

    @Override
    protected String doInBackground(String... params) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        URL url;
        String link;
        try {
            link = params[0];
            url = new URL(link);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null)
                buffer.append(line + "\n");

            if (buffer.length() == 0)
                return null;

            return buffer.toString();

        } catch (IOException e) {
            Log.e(TAG, "IO Exception", e);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(TAG, "Error closing stream", e);
                }
            }
        }
    }

    @Override
    protected void onPostExecute(String result) {
        this.listener.onComplete(result);
    }
}
