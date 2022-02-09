package io.github.zkhan93.simplequotes;

import android.content.SharedPreferences;
import android.util.Log;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.core.util.PatternsCompat;

class QuoteSource {
    public static String TAG = QuoteSource.class.getSimpleName();
    String url;
    long lastDownloadedAt;
    private final SharedPreferences sharedPreferences;

    public QuoteSource(@NonNull String sourceUrl, SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
        if (PatternsCompat.WEB_URL.matcher(sourceUrl).matches()) {
            this.url = sourceUrl;
            lastDownloadedAt = sharedPreferences.getLong(String.format("%s_last_fetched",
                    sourceUrl), -1);
        }
    }

    private boolean isQuoteOld() {
        // TODO: take the interval from the user as configuration
        long millisecInADay = 24 * 60 * 60 * 1000;
        long timeNow = Calendar.getInstance().getTimeInMillis();
        return (timeNow - lastDownloadedAt) >= millisecInADay;
    }

    public void update() {
        if (this.isQuoteOld())
            new Downloader(result -> {
                if (result == null) {
                    Log.d(TAG, String.format("quotes downloaded from %s was empty", url));
                    return;
                }
                sharedPreferences.edit()
                        .putString(url, result)
                        .putLong(String.format("%s_last_fetched", url),
                                Calendar.getInstance().getTimeInMillis())
                        .apply();

                Log.d(TAG, String.format("quotes downloaded from %s", url));
            }).execute(url);
    }

    public String getContent(){
        return sharedPreferences.getString(url, null);
    }
}
