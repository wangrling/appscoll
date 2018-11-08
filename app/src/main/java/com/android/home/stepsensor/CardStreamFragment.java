package com.android.home.stepsensor;

import android.support.v4.app.Fragment;

/**
 * A Fragment that handles a stream of cards.
 * Cards can be shown or hidden. When a card is shown it can also be marked as not-dismissible, see
 * {@link CardStreamLinearLayout#addCard(android.view.View, boolean)}.
 */


public class CardStreamFragment extends Fragment {

    private static final int INITIAL_SIZE = 15;
    private CardStreamLinearLayout mLayout = null;
}
