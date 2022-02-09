package io.github.zkhan93.simplequotes.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import io.github.zkhan93.simplequotes.Constants;
import io.github.zkhan93.simplequotes.R;
import io.github.zkhan93.simplequotes.Utils;

import static io.github.zkhan93.simplequotes.Constants.DEFAULT_BG_COLOR;
import static io.github.zkhan93.simplequotes.Constants.DEFAULT_COLOR;
import static io.github.zkhan93.simplequotes.Constants.DEFAULT_FONT_SIZE;
import static io.github.zkhan93.simplequotes.Constants.QUOTE_TYPE_FIXED;
import static io.github.zkhan93.simplequotes.Constants.QUOTE_TYPE_LINK;


public class AppState {

    private static final String TAG = AppState.class.getSimpleName();
    private TextView textQuote;
    private TextView textAuthor;

    private SeekBar seekbarQuoteSize;
    private SeekBar seekbarAuthorSize;
    private SwitchMaterial switchQuoteSource;
    private SwitchMaterial switchShowAuthor;
    private SwitchMaterial switchShowBg;
    private TextInputEditText editQuotesLink;
    private TextInputEditText editQuoteContent;
    private TextInputEditText editQuoteAuthor;
    private CardView card;

    private int widgetId;
    private Context context;
    private Quote sampleQuote;

    public AppState(int widgetId, Context context,
                    CardView card,
                    TextView textQuote,
                    TextView textAuthor,
                    SeekBar seekbarQuoteSize,
                    SeekBar seekbarAuthorSize,
                    SwitchMaterial switchQuoteSource,
                    SwitchMaterial switchShowAuthor,
                    SwitchMaterial switchShowBg,
                    TextInputEditText editQuotesLink,
                    TextInputEditText editQuoteContent,
                    TextInputEditText editQuoteAuthor) {
        this.widgetId = widgetId;
        this.context = context;
        this.sampleQuote = new Quote(context.getString(R.string.sample_quote),
                context.getString(R.string.sample_author));
        this.card = card;

        this.textQuote = textQuote;
        this.textAuthor = textAuthor;

        this.seekbarQuoteSize = seekbarQuoteSize;
        this.seekbarAuthorSize = seekbarAuthorSize;
        this.switchQuoteSource = switchQuoteSource;
        this.switchShowAuthor = switchShowAuthor;
        this.switchShowBg = switchShowBg;
        this.editQuotesLink = editQuotesLink;
        this.editQuoteContent = editQuoteContent;
        this.editQuoteAuthor = editQuoteAuthor;
    }

    private String getString(@NonNull TextInputEditText refEditText) {
        Editable content = refEditText.getText();
        if (content != null) {
            return content.toString();
        }
        return "";
    }

    public String getQuoteLink() {
        return this.getString(this.editQuotesLink);
    }

    public void setQuoteLink(String quoteLink) {
        this.editQuotesLink.setText(quoteLink);
    }

    public String getQuote() {
        return this.textQuote.getText().toString();
    }

    public void setQuote(String quote) {
        this.textQuote.setText(quote);
        this.editQuoteContent.setText(quote);

    }

    public String getAuthor() {
        return this.textAuthor.getText().toString();
    }

    public void setAuthor(String author) {
        this.textAuthor.setText(author);
        this.editQuoteAuthor.setText(author);
    }

    public float getContentTextSize() {
        return this.seekbarQuoteSize.getProgress();
    }

    public void setContentTextSize(float size) {
        this.textQuote.setTextSize(size);
        this.seekbarQuoteSize.setProgress((int) size);
    }

    public float getAuthorTextSize() {
        return this.seekbarAuthorSize.getProgress();
    }

    public void setAuthorTextSize(float size) {
        this.textAuthor.setTextSize(size);
        this.seekbarAuthorSize.setProgress((int) size);
    }

    public int getContentColor() {
        return this.textQuote.getCurrentTextColor();
    }

    public void setContentColor(int color) {
        this.textQuote.setTextColor(color);
        this.textAuthor.setTextColor(color);
    }

    public int getBgColor() {
        return this.card.getCardBackgroundColor().getDefaultColor();
    }

    public void setBgColor(int color) {
        this.card.setCardBackgroundColor(color);
    }

    public boolean isShowAuthor() {
        return this.switchShowAuthor.isChecked();
    }

    public void setShowAuthor(boolean show) {
        this.switchShowAuthor.setChecked(show);
    }

    public boolean isShowBackground() {
        return this.switchShowBg.isChecked();
    }

    public void setShowBackground(boolean show) {
        this.switchShowBg.setChecked(show);
    }

    public int getQuoteSource() {
        return this.switchQuoteSource.isChecked() ? QUOTE_TYPE_FIXED : QUOTE_TYPE_LINK;
    }

    public void setQuoteSource(int state) {
        if (state == QUOTE_TYPE_LINK) {
            this.switchQuoteSource.setChecked(false);
        } else if (state == QUOTE_TYPE_FIXED) {
            this.switchQuoteSource.setChecked(true);
        }
    }


    public void save() {
        Log.d(TAG, "saving app state");
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.context);
        float authorTextSize = this.getAuthorTextSize();
        int contentColor = this.getContentColor();
        float contentTextSize = this.getContentTextSize();
        int bgColor = this.getBgColor();
        int quoteSource = this.getQuoteSource();
        String quoteLink = this.getQuoteLink();
        String quote = this.getQuote();
        String author = this.getAuthor();
        boolean showAuthor = this.isShowAuthor();
        boolean showBg = this.isShowBackground();

        Log.d(TAG, String.format("authorTextSize %f", authorTextSize));
        Log.d(TAG, String.format("contentTextSize %f", contentTextSize));
        Log.d(TAG, String.format("contentColor %d", contentColor));
        Log.d(TAG, String.format("bgColor %d", bgColor));
        Log.d(TAG, String.format("quoteLink %s", quoteLink));
        Log.d(TAG, String.format("quote %s", quote));
        Log.d(TAG, String.format("author %s", author));
        Log.d(TAG, String.format("quoteSource %s", quoteSource));
        Log.d(TAG, String.format("showAuthor %s", showAuthor));
        Log.d(TAG, String.format("showBg %s", showBg));

        sp.edit()
                .putFloat(Utils.buildKey(this.widgetId, Constants.KEY.font_size_author),
                        authorTextSize)
                .putFloat(Utils.buildKey(this.widgetId, Constants.KEY.font_size_quote),
                        contentTextSize)

                .putInt(Utils.buildKey(this.widgetId, Constants.KEY.font_color),
                        contentColor)
                .putInt(Utils.buildKey(this.widgetId, Constants.KEY.bg_color),
                        bgColor)

                .putInt(Utils.buildKey(this.widgetId, Constants.KEY.quote_type), quoteSource)

                .putString(Utils.buildKey(this.widgetId, Constants.KEY.quote_source),
                        quoteLink)
                .putString(Utils.buildKey(this.widgetId, Constants.KEY.quote_content),
                        quote)
                .putString(Utils.buildKey(this.widgetId, Constants.KEY.quote_author),
                        author)

                .putBoolean(Utils.buildKey(this.widgetId, Constants.KEY.show_author),
                        showAuthor)
                .putBoolean(Utils.buildKey(this.widgetId, Constants.KEY.show_bg),
                        showBg)
                .apply();

    }

    public void load() {
        Log.d(TAG, "loading app state");
        SharedPreferences sp =
                PreferenceManager.getDefaultSharedPreferences(this.context);
        float authorTextSize = sp.getFloat(Utils.buildKey(this.widgetId,
                Constants.KEY.font_size_author), DEFAULT_FONT_SIZE);
        this.setAuthorTextSize(authorTextSize);
        float contentTextSize = sp.getFloat(Utils.buildKey(this.widgetId,
                Constants.KEY.font_size_quote), DEFAULT_FONT_SIZE);
        this.setContentTextSize(contentTextSize);

        int contentColor = sp.getInt(Utils.buildKey(this.widgetId,
                Constants.KEY.font_color), DEFAULT_COLOR);
        this.setContentColor(contentColor);
        int bgColor = sp.getInt(Utils.buildKey(this.widgetId,
                Constants.KEY.bg_color), DEFAULT_BG_COLOR);
        this.setBgColor(bgColor);
        String quoteLink = sp.getString(Utils.buildKey(this.widgetId,
                Constants.KEY.quote_source), null);
        this.setQuoteLink(quoteLink);

        String quote = sp.getString(Utils.buildKey(this.widgetId,
                Constants.KEY.quote_content), sampleQuote.getText());
        this.setQuote(quote);
        String author = sp.getString(Utils.buildKey(this.widgetId,
                Constants.KEY.quote_author), sampleQuote.getAuthor());
        this.setAuthor(author);

        int quoteSource = sp.getInt(Utils.buildKey(this.widgetId,
                Constants.KEY.quote_type), QUOTE_TYPE_LINK);
        this.setQuoteSource(quoteSource);

        boolean showAuthor = sp.getBoolean(Utils.buildKey(
                this.widgetId, Constants.KEY.show_author), true);
        this.setShowAuthor(showAuthor);
        boolean showBg = sp.getBoolean(Utils.buildKey(
                this.widgetId, Constants.KEY.show_bg), true);
        this.setShowBackground(showBg);

        Log.d(TAG, String.format("authorTextSize %f", authorTextSize));
        Log.d(TAG, String.format("contentTextSize %f", contentTextSize));
        Log.d(TAG, String.format("contentColor %d", contentColor));
        Log.d(TAG, String.format("bgColor %d", bgColor));
        Log.d(TAG, String.format("quoteLink %s", quoteLink));
        Log.d(TAG, String.format("quote %s", quote));
        Log.d(TAG, String.format("author %s", author));
        Log.d(TAG, String.format("quoteSource %s", quoteSource));
        Log.d(TAG, String.format("showAuthor %s", showAuthor));
        Log.d(TAG, String.format("showBg %s", showBg));

    }
}
