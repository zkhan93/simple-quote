package io.github.zkhan93.simplequotes;

public interface Constants {
    int DEFAULT_FONT_SIZE = 24;

    int COLOR_CHOOSE_BG = 1;
    int COLOR_CHOOSE_FG = 2;

    int DEFAULT_COLOR = R.color.cardview_light_background;
    int DEFAULT_BG_COLOR = R.color.cardview_dark_background;

    int QUOTE_TYPE_FIXED = 1;
    int QUOTE_TYPE_LINK = 2;

    String KEY_format = "%d_%s";
    interface KEY {

        String font_size_author = "font_size_author";
        String font_size_quote = "font_size_quote";

        String font_color = "font_color";
        String bg_color = "bg_color";

        String quote_type = "quote_type";

        String quote_source = "quote_source";
        String quote_content = "quote_content";
        String quote_author = "quote_author";

        String show_author = "show_author";
        String show_bg = "show_bg";
    }
}
