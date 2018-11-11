package com.android.home.testing;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.not;

import androidx.test.filters.LargeTest;
import com.android.home.R;
import com.android.home.testing.espresso.CustomMatcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests for {@link com.android.home.testing.espresso.CustomMatcher} showcasing the use of custom
 * matchers (see {@link HintMatcher#withHint(String)}.
 */


@RunWith(AndroidJUnit4.class)
@LargeTest
public class HintMatchersTest {

    private static final String INVALID_STRING_TO_BE_TYPED = "Earl Grey";

    private static final String COFFEE_ENDING = "coffee?";

    private static final String COFFEE_INVALID_ENDING = "tea?";

    // A valid string with a valid ending
    private String mStringWithValidEnding;

    // A valid string from the coffee preparations
    private String mValidStringToBeTyped;

    /**
     * {@link ActivityScenario} will create and launch of the activity for you.
     */
    @Before
    public void launchActivity() {
        ActivityScenario.launch(CustomMatcher.class);
    }

    @Before
    public void initValidStrings() {
        // Produce a string with valid ending.
        mStringWithValidEnding = "Random " + CustomMatcher.VALID_ENDING;

        // Get one of the available coffee preparations.
        mValidStringToBeTyped = CustomMatcher.COFFEE_PREPARATIONS.get(0);
    }

    @Test
    public void hint_isDisplayedInEditText() {
        String hintText = getApplicationContext().getResources().getString(
                R.string.custom_match_hint);

        onView(withId(R.id.editText)).check(matches(HintMatcher.withHint(hintText)));
    }

    /**
     * Same as above but using a {@link org.hamcrest.Matcher} as the argument.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void hint_endsWith() {
        // This check will probably fail if the app is localized and the language is changed. Avoid
        // string literals in code!
        onView(withId(R.id.editText)).check(matches(HintMatcher.withHint(anyOf(
                endsWith(COFFEE_ENDING), endsWith(COFFEE_INVALID_ENDING)))));
    }

    @Test
    public void editText_canBeTypedInto() {
        onView(withId(R.id.editText))
                .perform(typeText(mValidStringToBeTyped), ViewActions.closeSoftKeyboard())
                .check(matches(withText(mValidStringToBeTyped)));
    }

    @Test
    public void validation_resultHasCorrectEnding() {
        // Type a string with a valid ending and click on the button.
        onView(withId(R.id.editText))
                .perform(typeText(mStringWithValidEnding), closeSoftKeyboard());

        onView(withId(R.id.button)).perform(click());

        // Check that the correct sign is diaplyed.
        onView(withId(R.id.inputValidationSuccess)).check(matches(isDisplayed()));
        onView(withId(R.id.inputValidationError)).check(matches(not(isDisplayed())));
    }

    @Test
    public void validation_resultIsIncorrect() {
        // Type a valid string and click on the button.
        onView(withId(R.id.editText))
                .perform(typeText(INVALID_STRING_TO_BE_TYPED), closeSoftKeyboard());

        onView(withId(R.id.button)).perform(click());

        // Check that the correct sign is displayed.
        onView(withId(R.id.inputValidationError)).check(matches(isDisplayed()));
        onView(withId(R.id.inputValidationSuccess)).check(matches(not(isDisplayed())));
    }
}
