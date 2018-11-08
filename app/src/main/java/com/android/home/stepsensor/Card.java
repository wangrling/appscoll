package com.android.home.stepsensor;

import com.android.home.R;

/**
 * A Card contains a description and has a visual state. Optionally a card also contains a title,
 * progress indicator and zero or more actions.
 * It is constructed through the {@link Builder}.
 */


public class Card {


    public static final int ACTION_POSITIVE = 1;
    public static final int ACTION_NEGATIVE = 2;
    // 中性。
    public static final int ACTION_NEUTRAL = 3;

    public static final int PROGRESS_TYPE_NO_PROGRESS = 0;
    public static final int PROGRESS_TYPE_NORMAL = 1;
    public static final int PROGRESS_TYPE_INDETERMINATE = 2;
    public static final int PROGRESS_TYPE_LABEL = 3;

    private OnCardClickListener mClickListener;

    // The card model contains a reference to its desired layout (for extensibility), title,
    // description, zero to many action buttons, and zero or 1 progress indicators.
    private int mLayoutId = R.layout.card;

    /**
     * Creates a new Card.
     */
    public static class Builder {
        private Card mCard;

        protected Builder(OnCardClickListener listener, Card card) {

            mCard = card;
            mCard.mClickListener = listener;
        }
    }
}
