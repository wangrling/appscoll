package com.android.home.threelibs.testing;

import androidx.test.espresso.matcher.BoundedMatcher;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import android.view.View;
import android.widget.EditText;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.hamcrest.Matchers.is;

/**
 * A custom matcher that checks the hint property of an {@link android.widget.EditText}. It
 * accepts either a {@link String} or a {@link org.hamcrest.Matcher}.
 */

// 检查一个View是否包含相关的hint值。

public class HintMatcher {

    static Matcher<View> withHint(final String subString) {
        return withHint(is(subString));
    }

    static Matcher<View> withHint(final Matcher<String> stringMatcher) {
        checkNotNull(stringMatcher);

        return new BoundedMatcher<View, EditText>(EditText.class) {
            @Override
            protected boolean matchesSafely(EditText view) {

                final CharSequence hint = view.getHint();
                return hint != null && stringMatcher.matches(hint.toString());

            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with hint: ");
                stringMatcher.describeTo(description);
            }
        };
    }
}
