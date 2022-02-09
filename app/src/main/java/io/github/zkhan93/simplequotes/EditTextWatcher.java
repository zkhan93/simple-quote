package io.github.zkhan93.simplequotes;

import android.text.Editable;
import android.text.TextWatcher;

interface EditTextWatcher extends TextWatcher{

    @Override
    default void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

    @Override
    default void afterTextChanged(Editable editable) {}
}
