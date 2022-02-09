package io.github.zkhan93.simplequotes;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import io.github.zkhan93.simplequotes.model.Quote;

class QuoteParser {
    public static final String TAG = QuoteParser.class.getSimpleName();
    private static final Random random = new Random();
    private final List<Quote> quotes;

    public QuoteParser(@NonNull  String content) {
        quotes = new ArrayList<>();
        this.parse(content);

    }
    public void parse(@NonNull  String content){
        JSONArray jsonQuotes;
        try {
            jsonQuotes = new JSONArray(content);
            JSONObject jsonQuote;
            for (int i = 0; i < jsonQuotes.length(); i++) {
                jsonQuote = jsonQuotes.getJSONObject(i);
                try {
                    quotes.add(new Quote(jsonQuote.getString("quote"),
                                         jsonQuote.getString("author")));
                }catch (JSONException e){
                    Log.e(TAG, String.format("Exception building a quote from %s", jsonQuote.toString()), e);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Exception in parsing quotes from preferences", e);
        }
    }


    public Quote getDefaultQuote() {
        return new Quote("Knowledge is the root of all good.", "- Imam Ali");
    }


    Quote getRandomQuote() {
        if(quotes.size() > 0) {
            Quote quote = quotes.get(random.nextInt(quotes.toArray().length));
            Log.d(TAG, String.format("Quote selected: %s", quote));
            return quote;
        }else{
            return getDefaultQuote();
        }
    }

}
