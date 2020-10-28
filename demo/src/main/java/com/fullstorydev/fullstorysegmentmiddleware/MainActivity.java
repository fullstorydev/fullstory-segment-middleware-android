package com.fullstorydev.fullstorysegmentmiddleware;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.fullstorydev.fullstory_segment_middleware.FullStorySegmentMiddleware;
import com.segment.analytics.Analytics;
import com.segment.analytics.Properties;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String PRODUCT_VIEWED = "Product Viewed";
    private String ORDER_COMPLETED = "Order Completed";
    private String VIEWED_CHECKOUT_STEP = "Viewed Checkout Step";
    private String COMPLETED_CHECKOUT_STEP= "Completed Checkout Step";
    List<String> allowListedEvents;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // use the same values as Segment builder requires
        // if you set a segment tag explicitly,
        // use the same tag rather than the write key to init FullStoryMiddleware

        allowListedEvents = Arrays.asList(ORDER_COMPLETED, VIEWED_CHECKOUT_STEP, COMPLETED_CHECKOUT_STEP);
        FullStorySegmentMiddleware fsm = new FullStorySegmentMiddleware(getApplicationContext()
                , BuildConfig.SEGMENT_WRITE_KEY
                , (allowListedEvents));
        // enable to insert FS session URL to Segment event properties and contexts
        // default to true
        fsm.enableFSSessionURLInEvents = true;
        // when calling Segment group, send group traits as userVars
        // default to false
        fsm.enableGroupTraitsAsUserVars = true;
        // when calling Segment screen, sent the screen event as custom events to FS
        // default to false
        fsm.enableSendScreenAsEvents = true;
        // allow all track events as FS custom events
        // alternatively allow list that you would like to track
        // default to false
        fsm.allowlistAllTrackEvents = true;

        Analytics analytics = new Analytics
                .Builder(getApplicationContext(), BuildConfig.SEGMENT_WRITE_KEY)
                .useSourceMiddleware(fsm)
                .build();

        // Set the initialized instance as a globally accessible instance.
        Analytics.setSingletonInstance(analytics);

        setupButtons();
    }

    private void setupButtons() {
        Properties fakeEventProperties = new Properties() {
            {
                put("cart_id_str", "130983678493");
                put("product_id_str", "798ith22928347");
                put("sku_str", "L-100");
                put("category_str", "Clothing");
                put("name_str", "Button Front Cardigan");
                put("brand_str", "Bright & Bold");
                put("variant_str", "Blue");
                put("price_real", 58.99);
                put("quantity_real", 1);
                put("coupon_str", "25OFF");
                put("position_int", 3);
                put("url_str", "https://www.example.com/product/path");
                put("image_url_str", "https://www.example.com/product/path.jpg");
            }
        };

        // simulate Analytics track events with button clicks!
        findViewById(R.id.btn_product_viewed).setOnClickListener(v -> trackActionWithToast(PRODUCT_VIEWED, fakeEventProperties));;
        findViewById(R.id.btn_viewed_checkout_step).setOnClickListener(v ->  trackActionWithToast(VIEWED_CHECKOUT_STEP, fakeEventProperties));;
        findViewById(R.id.btn_completed_checkout_step).setOnClickListener(v ->  trackActionWithToast(COMPLETED_CHECKOUT_STEP, fakeEventProperties));;
        findViewById(R.id.btn_order_completed).setOnClickListener(v ->  trackActionWithToast(ORDER_COMPLETED, fakeEventProperties));
    }

    // your own analytics manager
    private void trackActionWithToast (String eventName, Properties eventProperties) {
        // for demoing purposes, toast to show if an event is allow-listed or not. So to easily verify events forwarding in FullStory playback
        String toastText = eventName;
        if(allowListedEvents.contains(eventName)) toastText += " event is allowed, forwarding to FullStory";
        else toastText += " event is not allowed, not forwarding to FullStory";

        Analytics.with(this).track(eventName, eventProperties);

        Toast.makeText(this, toastText, Toast.LENGTH_LONG).show();
    }

}