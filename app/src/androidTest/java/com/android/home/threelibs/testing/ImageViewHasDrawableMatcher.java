package com.android.home.threelibs.testing;

import android.view.View;
import android.widget.ImageView;
import androidx.test.espresso.matcher.BoundedMatcher;
import org.hamcrest.Description;

/**
 * A Matcher for Espresso that checks if an ImageView has a drawable applied to it.
 */

public class ImageViewHasDrawableMatcher {

    public static BoundedMatcher<View, ImageView> hasDrawable() {
        return new BoundedMatcher<View, ImageView>(ImageView.class) {
            @Override
            protected boolean matchesSafely(ImageView item) {
                return item.getDrawable() != null;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("has drawable");
            }
        };
    }
}
