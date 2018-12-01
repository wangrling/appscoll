package com.android.home.threelibs.gson;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.android.home.R;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class ProguardGson extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView textView = new TextView(this);
        textView.setPadding(8, 8, 8, 8);
        setContentView(textView);

        Gson gson = new Gson();
        Cart cart = buildCart();

        StringBuilder sb = new StringBuilder();
        sb.append("Gson.toJson() example: \n");
        sb.append(" Card Object: ").append(cart).append("\n");

        sb.append("\n\nGson.fromJson() example: \n");
        String json = "{buyer:'Happy Camper',creditCard:'4111-1111-1111-1111',"
                + "lineItems:[{name:'nails',priceInMicros:100000,quantity:100,currencyCode:'USD'}]}";
        sb.append("Cart JSON: ").append(json).append("\n");

        sb.append("Card Object: ").append(gson.fromJson(json, Cart.class)).append("\n");

        textView.setText(sb.toString());
        textView.invalidate();
    }

    private Cart buildCart() {
        List<LineItem> lineItems = new ArrayList<LineItem>();
        lineItems.add(new LineItem("hammer", 1, 12000000, "USD"));
        lineItems.add(new LineItem("portdo", 2, 13000000, "CHN"));
        return new Cart(lineItems, "Happy Buyer", "4111-1111");
    }
}
