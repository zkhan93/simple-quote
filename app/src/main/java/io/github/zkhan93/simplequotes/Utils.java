package io.github.zkhan93.simplequotes;

import java.util.Locale;

import static io.github.zkhan93.simplequotes.Constants.KEY_format;

public class Utils {
    public static String buildKey(int id, String key){
        return String.format(Locale.US, KEY_format, id, key);
    }
}
