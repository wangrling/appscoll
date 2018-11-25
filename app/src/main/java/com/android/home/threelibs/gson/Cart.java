package com.android.home.threelibs.gson;

import androidx.annotation.NonNull;
import com.android.home.stepsensor.Card;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.List;

public class Cart {

    public final List<LineItem> lineItems;

    @SerializedName("buyer")
    private final String buyerName;
    private final String creditCard;

    public Cart(List<LineItem> lineItems, String buyerName, String creditCard) {
        this.lineItems = lineItems;
        this.buyerName = buyerName;
        this.creditCard = creditCard;
    }

    public List<LineItem> getLineItems() {
        return lineItems;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public String getCreditCard() {
        return creditCard;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder itemsText = new StringBuilder();
        boolean first = true;
        if (lineItems != null) {
            try {
                // 获取lineItems的类型
                Class<?> fieldType = Cart.class.getField("lineItems").getType();

                System.out.println("LineItems CLASS: " + getSimpleTypeName(fieldType));
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }

            for (LineItem item : lineItems) {
                if (first) {
                    first = false;
                } else {
                    itemsText.append("; ");
                }
                itemsText.append(item);
            }
        }

        return "[BUYER: " + buyerName + "; CC: " + creditCard + "; "
                + "LINE_ITEMS: " + itemsText.toString() + "]";
    }

    public static String getSimpleTypeName(Type type) {
        if (type == null)
            return "null";

        if (type instanceof Class) {
            return ((Class)type).getSimpleName();

        } else if (type instanceof ParameterizedType) {
            /**
             * ParameterizedType represents a parameterized type such as
             * Collection<String>;.
             */
            ParameterizedType pType = (ParameterizedType) type;
            StringBuilder sb = new StringBuilder(getSimpleTypeName(pType.getRawType()));

            sb.append('<');
            boolean first = true;

            for (Type argumentType : pType.getActualTypeArguments()) {
                if (first) {
                    first = false;
                } else {
                    sb.append(',');
                }
                sb.append(getSimpleTypeName(argumentType));
            }
            sb.append('>');

            return sb.toString();
        } else if (type instanceof WildcardType) {
            return "?";
        }

        return type.toString();
    }
}
