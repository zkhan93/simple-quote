package io.github.zkhan93.simplequotes;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatDrawableManager;
import androidx.core.graphics.drawable.DrawableCompat;
import io.github.zkhan93.simplequotes.Constants.KEY;
import io.github.zkhan93.simplequotes.model.Quote;

import static android.util.TypedValue.COMPLEX_UNIT_PX;
import static android.util.TypedValue.COMPLEX_UNIT_SP;
import static io.github.zkhan93.simplequotes.Constants.DEFAULT_COLOR;
import static io.github.zkhan93.simplequotes.Constants.DEFAULT_FONT_SIZE;
import static io.github.zkhan93.simplequotes.Constants.QUOTE_TYPE_FIXED;
import static io.github.zkhan93.simplequotes.Constants.QUOTE_TYPE_LINK;

public class SimpleQuotesWidgetProvider extends AppWidgetProvider {
    public static final String TAG = SimpleQuotesWidgetProvider.class.getSimpleName();


    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int appWidgetId : appWidgetIds) {
            Log.d(TAG, "updateWidget widgetId= " + appWidgetId);
            SimpleQuotesWidgetProvider.updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }


    static void updateAppWidget(Context context, AppWidgetManager widgetManager,
                                int widgetId) {
        SharedPreferences sp =
                PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());


        float fontSizeQuote = sp.getFloat(Utils.buildKey(widgetId,
                KEY.font_size_quote), DEFAULT_FONT_SIZE);
        float fontSizeAuthor = sp.getFloat(Utils.buildKey(widgetId,
                KEY.font_size_author), DEFAULT_FONT_SIZE);

        int contentColor = sp.getInt(Utils.buildKey(widgetId,
                KEY.font_color), DEFAULT_COLOR);
        int bgColor = sp.getInt(Utils.buildKey(widgetId,
                KEY.font_color), DEFAULT_COLOR);
        boolean showAuthor = sp.getBoolean(Utils.buildKey(widgetId,
                KEY.show_author), true);

        Log.d(TAG, String.format("fontSizeQuote: %f", fontSizeQuote));
        Log.d(TAG, String.format("fontSizeAuthor: %f", fontSizeAuthor));
        Log.d(TAG, String.format("contentColor: %d", contentColor));
        Log.d(TAG, String.format("bgColor: %d", bgColor));
        Log.d(TAG, String.format("showAuthor: %b", showAuthor));

        Quote quote = getQuote(context, sp, widgetId);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.quotes_widget);
//        TODO: fix background color thing
//        views.setInt(R.id.bg, "setBackgroundTintList", ColorStateList.valueOf(bgColor));
        views.setTextViewText(R.id.quote, quote.getText());
        views.setTextViewText(R.id.author, quote.getAuthor());
        views.setViewVisibility(R.id.author, showAuthor ? View.VISIBLE : View.GONE);

        views.setTextViewTextSize(R.id.quote, COMPLEX_UNIT_SP, fontSizeQuote);
        views.setTextViewTextSize(R.id.author, COMPLEX_UNIT_SP, fontSizeAuthor);

        views.setTextColor(R.id.quote, contentColor);
        views.setTextColor(R.id.author, contentColor);
        views.setOnClickPendingIntent(R.id.quote, getPendingIntent(context, widgetId));
        // Tell the widget manager
        widgetManager.updateAppWidget(widgetId, views);

    }

    static Quote getQuote(Context context, SharedPreferences sp, int appWidgetId) {
        Quote quote = new Quote(context.getString(R.string.sample_quote),
                context.getString(R.string.sample_author));
        int quoteType = sp.getInt(Utils.buildKey(appWidgetId, KEY.quote_type), QUOTE_TYPE_FIXED);

        if (quoteType == QUOTE_TYPE_FIXED) {
            quote.setText(sp.getString(Utils.buildKey(appWidgetId, KEY.quote_content), ""));
            quote.setAuthor(sp.getString(Utils.buildKey(appWidgetId, KEY.quote_author), ""));
        } else if (quoteType == QUOTE_TYPE_LINK) {
            String sourceUrl = sp.getString(Utils.buildKey(appWidgetId, KEY.quote_source), null);
            if (sourceUrl != null && !sourceUrl.isEmpty()) {
                QuoteSource quoteSource = new QuoteSource(sourceUrl, sp);
                quoteSource.update();
                quote = new QuoteParser(quoteSource.getContent()).getRandomQuote();
            }
        }
        return quote;
    }

    static PendingIntent getPendingIntent(Context context, int appWidgetId) {
        Intent intent = new Intent(context, SimpleQuoteWidgetConfigurationActivity.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        return PendingIntent.getActivity(context, appWidgetId, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            sp.edit()
                    .remove(Utils.buildKey(appWidgetId, KEY.font_size_author))
                    .remove(Utils.buildKey(appWidgetId, KEY.font_size_quote))
                    .remove(Utils.buildKey(appWidgetId, KEY.font_color))
                    .remove(Utils.buildKey(appWidgetId, KEY.bg_color))
                    .remove(Utils.buildKey(appWidgetId, KEY.quote_type))
                    .remove(Utils.buildKey(appWidgetId, KEY.quote_source))
                    .remove(Utils.buildKey(appWidgetId, KEY.quote_content))
                    .remove(Utils.buildKey(appWidgetId, KEY.quote_author))
                    .remove(Utils.buildKey(appWidgetId, KEY.show_author))
                    .remove(Utils.buildKey(appWidgetId, KEY.show_bg))
                    .apply();
        }
        super.onDeleted(context, appWidgetIds);
    }
}
