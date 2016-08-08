package ua.gov.dp.econtact.listeners;

import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;

/**
 * Created by Yalantis
 *
 * @author Oleksii Shliama.
 */
public abstract class SimpleTextWatcher implements TextWatcher {

    public void beforeTextChanged(final CharSequence charSequence, final int i,
                                  final int i1, final int i2) {
    }

    public void onTextChanged(@NonNull final CharSequence charSequence, final int i,
                              final int i1, final int i2) {
    }

    public void afterTextChanged(final Editable editable) {
    }
}

