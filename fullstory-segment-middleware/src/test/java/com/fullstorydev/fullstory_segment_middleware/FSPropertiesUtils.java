package com.fullstorydev.fullstory_segment_middleware;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FSPropertiesUtils {
    static Map<String, Object> ECommerceEventsProductsSearched() {
        Map<String, Object> map = new HashMap<>();
        map.put("query_str","blue roses");
        return map;
    }

    static Map<String, Object> ECommerceEventsProductListViewed() {
        Map<String, Object> map = new HashMap<>();
        map.put("list_id_str","hot_deals_1");
        map.put("category_str","Deals");

        map.putAll(getGameProducts());

        return map;
    }

    static Map<String, Object> ECommerceEventsProductListFiltered() {
        Map<String, Object> map = new HashMap<>();
        map.put("list_id_str","todays_deals_may_11_2019");
        map.put("filters.type_strs",new ArrayList<>(Arrays.asList("department","price")));
        map.put("filters.value_strs",new ArrayList<>(Arrays.asList("beauty","under-$25")));
        map.put("sorts.type_str","price");
        map.put("sorts.value_str","desc");


        ArrayList<Object> arr = new ArrayList<>();

        arr.add("507f1f77bcf86cd798439011");
        arr.add("505bd76785ebb509fc283733");
        map.put("products.product_id_strs", arr.clone());

        arr.clear();
        arr.add("45360-32");
        arr.add("46573-32");
        map.put("products.sku_strs", arr.clone());

        arr.clear();
        arr.add("Special Facial Soap");
        arr.add("Fancy Hairbrush");
        map.put("products.name_strs", arr.clone());

        arr.clear();
        arr.add(12.60);
        arr.add(7.60);
        map.put("products.price_reals", arr.clone());

        arr.clear();
        arr.add(1);
        arr.add(2);
        map.put("products.position_ints", arr.clone());

        arr.clear();
        arr.add("Beauty");
        arr.add("Beauty");
        map.put("products.category_strs", arr.clone());

        arr.clear();
        arr.add("");
        map.put("products.url_str", "https://www.example.com/product/path");
        map.put("products.image_url_str", "https://www.example.com/product/path.jpg");

        return map;
    }

    static Map<String, Object> ECommerceEventsPromotionViewed() {
        Map<String, Object> map = new HashMap<>();
        map.put("promotion_id_str","promo_1");
        map.put("creative_str","top_banner_2");
        map.put("name_str","75% store-wide shoe sale");
        map.put("position_str","home_banner_top");
        return map;
    }

    static Map<String, Object> ECommerceEventsPromotionClicked() {
        Map<String, Object> map = new HashMap<>();
        map.put("promotion_id_str","promo_1");
        map.put("creative_str","top_banner_2");
        map.put("name_str","75% store-wide shoe sale");
        map.put("position_str","home_banner_top");
        return map;
    }

    static Map<String, Object> ECommerceEventsProductClicked() {
        return getMonopolyProduct();
    }

    static Map<String, Object> ECommerceEventsProductViewed() {
        Map<String, Object> map = new HashMap<>();
        map.put("currency_str","usd");
        map.put("value_real",18.99);
        map.putAll(getMonopolyProduct());
        return map;
    }

    static Map<String, Object> ECommerceEventsProductAdded() {
        Map<String, Object> map = new HashMap<>();
        map.put("cart_id_str","skdjsidjsdkdj29j");
        map.putAll(getMonopolyProduct());
        return map;
    }

    static Map<String, Object> ECommerceEventsProductRemoved() {
        Map<String, Object> map = new HashMap<>();
        map.put("cart_id_str","skdjsidjsdkdj29j");
        map.putAll(getMonopolyProduct());
        return map;
    }

    static Map<String, Object> ECommerceEventsCartViewed() {
        Map<String, Object> map = new HashMap<>();
        map.put("cart_id_str","skdjsidjsdkdj29j");

        map.putAll(getGameProducts());

        return map;
    }

    static Map<String, Object> ECommerceEventsCheckoutStarted() {
        Map<String, Object> map = new HashMap<>();
        map.put("order_id_str","50314b8e9bcf000000000000");
        map.put("affiliation_str","Google Store");
        map.put("value_int",30);
        map.put("revenue_real",25.00);
        map.put("shipping_int",3);
        map.put("tax_int",2);
        map.put("discount_real",2.5);
        map.put("coupon_str","hasbros");
        map.put("currency_str","USD");

        map.putAll(getGameProducts());

        return map;
    }

    static Map<String, Object> ECommerceEventsCheckoutStepViewed() {
        Map<String, Object> map = new HashMap<>();
        map.put("checkout_id_str","50314b8e9bcf000000000000");
        map.put("step_int",2);
        map.put("shipping_method_str","Fedex");
        map.put("payment_method_str","Visa");
        return map;
    }

    static Map<String, Object> ECommerceEventsCheckoutStepCompleted() {
        Map<String, Object> map = new HashMap<>();
        map.put("checkout_id_str","50314b8e9bcf000000000000");
        map.put("step_int",2);
        map.put("shipping_method_str","Fedex");
        map.put("payment_method_str","Visa");
        return map;
    }

    static Map<String, Object> ECommerceEventsCheckoutPaymentInfoEntered() {
        Map<String, Object> map = new HashMap<>();
        map.put("checkout_id_str","39f39fj39f3jf93fj9fj39fj3f");
        map.put("order_id_str","dkfsjidfjsdifsdfksdjfkdsfjsdfkdsf");
        return map;
    }

    static Map<String, Object> ECommerceEventsOrderUpdated() {
        Map<String, Object> map = new HashMap<>();
        map.put("order_id_str","50314b8e9bcf000000000000");
        map.put("affiliation_str","Google Store");
        map.put("total_real",27.50);
        map.put("revenue_real",25.00);
        map.put("shipping_int",3);
        map.put("tax_int",2);
        map.put("discount_real",2.5);
        map.put("coupon_str","hasbros");
        map.put("currency_str","USD");

        map.putAll(getGameProducts());

        return map;
    }

    static Map<String, Object> ECommerceEventsOrderCompleted() {
        Map<String, Object> map = new HashMap<>();
        map.put("checkout_id_str","fksdjfsdjfisjf9sdfjsd9f");
        map.put("order_id_str","50314b8e9bcf000000000000");
        map.put("affiliation_str","Google Store");
        map.put("total_real",27.50);
        map.put("subtotal_real",22.50);
        map.put("revenue_real",25.00);
        map.put("shipping_int",3);
        map.put("tax_int",2);
        map.put("discount_real",2.5);
        map.put("coupon_str","hasbros");
        map.put("currency_str","USD");

        map.putAll(getGameProducts());

        return map;
    }

    static Map<String, Object> ECommerceEventsOrderRefunded() {
        Map<String, Object> map = new HashMap<>();
        map.put("order_id_str","50314b8e9bcf000000000000");
        map.put("total_int",30);
        map.put("currency_str","USD");

        map.putAll(getGameProducts());

        return map;
    }

    static Map<String, Object> ECommerceEventsOrderCancelled() {
        Map<String, Object> map = new HashMap<>();
        map.put("order_id_str","50314b8e9bcf000000000000");
        map.put("affiliation_str","Google Store");
        map.put("total_real",27.50);
        map.put("subtotal_real",22.50);
        map.put("revenue_real",25.00);
        map.put("shipping_int",3);
        map.put("tax_int",2);
        map.put("discount_real",2.5);
        map.put("coupon_str","hasbros");
        map.put("currency_str","USD");

        map.putAll(getGameProducts());

        return map;
    }

    static Map<String, Object> ECommerceEventsCouponEntered() {
        Map<String, Object> map = new HashMap<>();
        map.put("order_id_str","50314b8e9bcf000000000000");
        map.put("cart_id_str","923923929jd29jd92dj9j93fj3");
        map.put("coupon_id_str","may_deals_2016");
        return map;
    }

    static Map<String, Object> ECommerceEventsCouponApplied() {
        Map<String, Object> map = new HashMap<>();
        map.put("order_id_str","50314b8e9bcf000000000000");
        map.put("cart_id_str","923923929jd29jd92dj9j93fj3");
        map.put("coupon_id_str","may_deals_2016");
        map.put("coupon_name_str","May Deals 2016");
        map.put("discount_real",23.32);
        return map;
    }

    static Map<String, Object> ECommerceEventsCouponDenied() {
        Map<String, Object> map = new HashMap<>();
        map.put("order_id_str","50314b8e9bcf000000000000");
        map.put("cart_id_str","923923929jd29jd92dj9j93fj3");
        map.put("coupon_id_str","may_deals_2016");
        map.put("reason_str","Coupon expired");
        return map;
    }

    static Map<String, Object> ECommerceEventsCouponRemoved() {
        Map<String, Object> map = new HashMap<>();
        map.put("order_id_str","50314b8e9bcf000000000000");
        map.put("cart_id_str","923923929jd29jd92dj9j93fj3");
        map.put("coupon_id_str","may_deals_2016");
        map.put("coupon_name_str","May Deals 2016");
        map.put("discount_real",23.32);
        return map;
    }


    static Map<String, Object> ECommerceEventsProductAddedToWishlist() {
        Map<String, Object> map = new HashMap<>();
        map.put("wishlist_id_str","skdjsidjsdkdj29j");
        map.put("wishlist_name_str","Loved Games");
        map.putAll(getMonopolyProduct());
        return map;
    }

    static Map<String, Object> ECommerceEventsProductRemovedFromWishlist() {
        Map<String, Object> map = new HashMap<>();
        map.put("wishlist_id_str","skdjsidjsdkdj29j");
        map.put("wishlist_name_str","Loved Games");
        map.putAll(getMonopolyProduct());
        return map;
    }

    static Map<String, Object> ECommerceEventsWishlistProductAddedToCart() {
        Map<String, Object> map = new HashMap<>();
        map.put("wishlist_id_str","skdjsidjsdkdj29j");
        map.put("wishlist_name_str","Loved Games");
        map.put("cart_id_str","'99j2d92j9dj29dj29d2d'");
        map.putAll(getMonopolyProduct());
        return map;
    }
    static Map<String, Object> ECommerceEventsProductShared() {
        Map<String, Object> map = new HashMap<>();
        map.put("share_via_str","email");
        map.put("share_message_str","Hey, check out this item");
        map.put("recipient_str","friend@example.com");
        map.putAll(getMonopolyProduct());
        return map;
    }

    static Map<String, Object> ECommerceEventsProductReviewed() {
        Map<String, Object> map = new HashMap<>();
        map.put("product_id_str","507f1f77bcf86cd799439011");
        map.put("review_id_str","kdfjrj39fj39jf3");
        map.put("review_body_str","I love this product");
        map.put("rating_int",5);
        return map;
    }

    static Map<String, Object> ECommerceEventsCartShared() {
        Map<String, Object> map = new HashMap<>();
        map.put("share_via_str","email");
        map.put("share_message_str","Hey, check out this item");
        map.put("recipient_str","friend@example.com");
        map.put("cart_id_str","d92jd29jd92jd29j92d92jd");

        ArrayList<Object> arr = new ArrayList<>();

        arr.add("507f1f77bcf86cd799439011");
        arr.add("505bd76785ebb509fc183733");
        map.put("products.product_id_strs", arr.clone());

        return map;
    }

    static Map<String, Object> getGameProducts(){
        Map<String, Object> map = new HashMap<>();

        ArrayList<Object> arr = new ArrayList<>();

        arr.add("507f1f77bcf86cd799439011");
        arr.add("505bd76785ebb509fc183733");
        map.put("products.product_id_strs", arr.clone());

        arr.clear();
        arr.add("45790-32");
        arr.add("46493-32");
        map.put("products.sku_strs", arr.clone());

        arr.clear();
        arr.add("Monopoly: 3rd Edition");
        arr.add("Uno Card Game");
        map.put("products.name_strs", arr.clone());

        arr.clear();
        arr.add(19.0);
        arr.add(3.0);
        map.put("products.price_reals", arr.clone());

        arr.clear();
        arr.add(1);
        arr.add(2);
        map.put("products.position_ints", arr.clone());

        arr.clear();
        arr.add("Games");
        arr.add("Games");
        map.put("products.category_strs", arr.clone());

        arr.clear();
        arr.add("");
        map.put("products.url_str", "https://www.example.com/product/path-32");
        map.put("products.image_url_str", "https://www.example.com/product/path.jpg-32");

        return map;
    }

    static Map<String, Object> getMonopolyProduct(){
        Map<String, Object> map = new HashMap<>();
        map.put("product_id_str","507f1f77bcf86cd799439011");
        map.put("sku_str","G-32");
        map.put("category_str","Games");
        map.put("name_str","Monopoly: 3rd Edition");
        map.put("brand_str","Hasbro");
        map.put("variant_str","200 pieces");
        map.put("price_real",18.99);
        map.put("quantity_int",1);
        map.put("coupon_str","MAYDEALS");
        map.put("position_int",3);
        map.put("url_str","https://www.example.com/product/path");
        map.put("image_url_str","https://www.example.com/product/path.jpg");
        return map;
    }

}
