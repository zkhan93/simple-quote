package io.github.zkhan93.simplequotes;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import io.github.zkhan93.simplequotes.model.AppState;
import io.github.zkhan93.simplequotes.model.Quote;

import static io.github.zkhan93.simplequotes.Constants.COLOR_CHOOSE_BG;
import static io.github.zkhan93.simplequotes.Constants.COLOR_CHOOSE_FG;

public class SimpleQuoteWidgetConfigurationActivity extends AppCompatActivity {
    public static final String TAG = SimpleQuoteWidgetConfigurationActivity.class.getSimpleName();
    private int widgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    private Button btnTestLink, btnBgColor;
    private TextView textQuote, textAuthor;
    private SeekBar seekbarQuoteSize, seekbarAuthorSize;
    private SwitchMaterial switchQuoteSource, switchShowAuthor, switchShowBg;
    private TextInputEditText editQuotesLink, editQuoteContent, editQuoteAuthor;

    private TextSizeChangeListener textSizeChangeListener = new TextSizeChangeListener();
    private AppState state;

    private EditTextWatcher quoteContentWatcher = (quote, i, i1, i2) -> textQuote.setText(quote);
    private EditTextWatcher quoteAuthorWatcher = (quote, i, i1, i2) -> textAuthor.setText(quote);
    private EditTextWatcher quoteSourceLinkWatcher =
            (link, i, i1, i2) -> findViewById(R.id.btn_test_link).setEnabled(!link.toString().isEmpty());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if they press the back button.
        setResult(RESULT_CANCELED);
        // Set the view layout resource to use.
        setContentView(R.layout.activity_simple_quote_configuration);

        seekbarQuoteSize = findViewById(R.id.seekbar_quote_text_size);
        seekbarAuthorSize = findViewById(R.id.seekbar_author_text_size);
        textQuote = findViewById(R.id.text_quote);
        textAuthor = findViewById(R.id.text_author);
        textSizeChangeListener.addViewToUpdate(R.id.seekbar_quote_text_size, textQuote);
        textSizeChangeListener.addViewToUpdate(R.id.seekbar_author_text_size, textAuthor);
        seekbarQuoteSize.setOnSeekBarChangeListener(textSizeChangeListener);
        seekbarAuthorSize.setOnSeekBarChangeListener(textSizeChangeListener);

        editQuotesLink = findViewById(R.id.edit_quote_link);
        editQuoteContent = findViewById(R.id.edit_quotes_content);
        editQuoteAuthor = findViewById(R.id.edit_quote_author);
        switchQuoteSource = findViewById(R.id.switch_quote_source);
        switchShowAuthor = findViewById(R.id.switch_show_author);
        switchShowBg = findViewById(R.id.switch_show_bg);
        Button btnQuoteColor = findViewById(R.id.btn_color_quote);
        btnBgColor = findViewById(R.id.btn_color_bg);
        btnTestLink = findViewById(R.id.btn_test_link);

        widgetId = getWidgetId();
        Log.d(TAG, String.format("configuring widgerID %d", widgetId));

        state = new AppState(widgetId, getApplicationContext(), findViewById(R.id.card),
                textQuote, textAuthor,
                seekbarQuoteSize, seekbarAuthorSize, switchQuoteSource, switchShowAuthor,
                switchShowBg, editQuotesLink, editQuoteContent, editQuoteAuthor);

        btnTestLink.setOnClickListener(view -> testSourceLink());
        btnQuoteColor.setOnClickListener(view -> showChooseColorDialog(COLOR_CHOOSE_FG));
        btnBgColor.setOnClickListener(view -> showChooseColorDialog(COLOR_CHOOSE_BG));
        switchQuoteSource.setOnCheckedChangeListener((compoundButton, isChecked) -> toggleQuoteSourceAction(isChecked));
        switchShowAuthor.setOnCheckedChangeListener((compoundButton, isChecked) -> toggleAuthor(isChecked));
        switchShowBg.setOnCheckedChangeListener(((compoundButton, isChecked) -> toggleBgAction(isChecked)));

        findViewById(R.id.fab).setOnClickListener(view -> fabAction());
//        since state listener in quote type switch does not triggers if initial state happens to
//        be false
        removeQuoteContentWatcher();
//        trigger for initial value
        state.load();
        toggleAuthor(state.isShowAuthor());
        toggleBgAction(state.isShowBackground());
        toggleQuoteSourceAction(false);

    }

    private int getWidgetId() {
        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        int widgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
        if (extras != null) {
            widgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        // If they gave us an intent without the widget id, just bail.
        if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }
        return widgetId;
    }

    private void fabAction() {
        state.save();
        triggerUpdateWidget();

        // Make sure we pass back the original appWidgetId
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        setResult(RESULT_OK, resultValue);

        finish();
    }

    private void triggerUpdateWidget() {
        Intent intent = new Intent(this, SimpleQuotesWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        // Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
        // since it seems the onUpdate() is only fired on that:
        int[] ids = AppWidgetManager.getInstance(getApplication())
                .getAppWidgetIds(new ComponentName(getApplication(),
                        SimpleQuotesWidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        sendBroadcast(intent);
    }

    private void toggleQuoteSourceAction(boolean isChecked) {
        Log.d(TAG, "QuoteSource switch activated");
        if (isChecked) {
            findViewById(R.id.view_quote_src_link).setVisibility(View.GONE);
            findViewById(R.id.view_quote_src_text).setVisibility(View.VISIBLE);
            addQuoteContentWatcher();
        } else {
            removeQuoteContentWatcher();
            findViewById(R.id.view_quote_src_link).setVisibility(View.VISIBLE);
            findViewById(R.id.view_quote_src_text).setVisibility(View.GONE);
        }
    }

    private void toggleAuthor(boolean isChecked) {
        Log.d(TAG, String.format("showAuthor switch activated %b", isChecked));
        if (isChecked) {
            Log.d(TAG, "showing author");
            textAuthor.setVisibility(View.VISIBLE);
            seekbarAuthorSize.setVisibility(View.VISIBLE);
        } else {
            Log.d(TAG, "hiding author");
            textAuthor.setVisibility(View.GONE);
            seekbarAuthorSize.setVisibility(View.GONE);
        }
    }

    private void toggleBgAction(boolean isChecked) {
        Log.d(TAG, "showBg switch activated");
        state.setBgColor(isChecked ? Color.GRAY : Color.TRANSPARENT);
        if (!isChecked) {
            ((CardView) findViewById(R.id.card)).setCardElevation(0);
            btnBgColor.setVisibility(View.GONE);
        } else {
            ((CardView) findViewById(R.id.card)).setCardElevation(getResources().getDimension(R.dimen.cardview_default_elevation));
            btnBgColor.setVisibility(View.VISIBLE);
        }
    }

    private void addQuoteContentWatcher() {
        editQuoteContent.addTextChangedListener(quoteContentWatcher);
        editQuoteAuthor.addTextChangedListener(quoteAuthorWatcher);
        editQuotesLink.removeTextChangedListener(quoteSourceLinkWatcher);
    }

    private void removeQuoteContentWatcher() {
        editQuoteContent.removeTextChangedListener(quoteContentWatcher);
        editQuoteAuthor.removeTextChangedListener(quoteAuthorWatcher);
        editQuotesLink.addTextChangedListener(quoteSourceLinkWatcher);
    }

    private void testSourceLink() {
        String link = state.getQuoteLink();
        if (link == null || link.isEmpty()) {
            Log.d(TAG, "Empty link");
            return;
        }

        new Downloader(result -> {
            if (result == null) {
                Log.d(TAG, "Link verification failed");
                Toast.makeText(getApplicationContext(), "Link verification failed",
                        Toast.LENGTH_LONG).show();
                return;
            }
            Quote quote = new QuoteParser(result).getRandomQuote();
            state.setQuote(quote.getText());
            state.setAuthor(quote.getAuthor());
            Log.d(TAG, "Link verification success");
            Toast.makeText(getApplicationContext(), "Link verification success",
                    Toast.LENGTH_LONG).show();
        }).execute(link);
    }

    private void showChooseColorDialog(int chooseFor) {
        int initialColor = chooseFor == COLOR_CHOOSE_BG ? state.getBgColor() :
                state.getContentColor();
        String title = chooseFor == COLOR_CHOOSE_BG ? "Choose background color" : "Choose text " +
                "color";
        ColorPickerDialogBuilder
                .with(this)
                .showAlphaSlider(chooseFor == COLOR_CHOOSE_FG)
                .setTitle(title)
                .initialColor(initialColor)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setPositiveButton(android.R.string.ok, (dialog, selectedColor, allColors) -> {
                    if (chooseFor == COLOR_CHOOSE_FG)
                        state.setContentColor(selectedColor);
                    else
                        state.setBgColor(selectedColor);
                })
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                })
                .build()
                .show();
    }


}
