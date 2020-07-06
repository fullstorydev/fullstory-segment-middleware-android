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

    static Map<String, Object> ECommerceEventsProductViewed() {
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
        map.put("currency_str","usd");
        map.put("position_int",3);
        map.put("value_real",18.99);
        map.put("url_str","https://www.example.com/product/path");
        map.put("image_url_str","https://www.example.com/product/path.jpg");
        return map;
    }

    static Map<String, Object> ECommerceEventsProductAdded() {
        Map<String, Object> map = new HashMap<>();
        map.put("cart_id_str","skdjsidjsdkdj29j");
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

    static Map<String, Object> ECommerceEventsProductRemoved() {
        Map<String, Object> map = new HashMap<>();
        map.put("cart_id_str","skdjsidjsdkdj29j");
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

    static Map<String, Object> ECommerceEventsCartViewed() {
        Map<String, Object> map = new HashMap<>();
        map.put("cart_id_str","skdjsidjsdkdj29j");

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
        map.put("checkout_id","39f39fj39f3jf93fj9fj39fj3f");
        map.put("order_id","dkfsjidfjsdifsdfksdjfkdsfjsdfkdsf");
        return map;
    }

    static Map<String, Object> ECommerceEventsOrderUpdated() {
        Map<String, Object> map = new HashMap<>();
        map.put("order_id","50314b8e9bcf000000000000");
        map.put("affiliation","Google Store");
        map.put("total",27.50);
        map.put("revenue",25.00);
        map.put("shipping",3);
        map.put("tax",2);
        map.put("discount",2.5);
        map.put("coupon","hasbros");
        map.put("currency","USD");

        ArrayList<Map<String, Object>> products = new ArrayList<>();
        Map<String, Object> p = new HashMap<>();
        p.put("product_id","507f1f77bcf86cd799439011");
        p.put("sku","45790-32");
        p.put("name","Monopoly: 3rd Edition");
        p.put("price",19.0);
        p.put("position",1);
        p.put("category","Games");
        p.put("url","https://www.example.com/product/path-32");
        p.put("image_url","https://www.example.com/product/path.jpg-32");
        products.add(new HashMap<>(p));
        p.clear();
        p.put("product_id","505bd76785ebb509fc183733");
        p.put("sku","46493-32");
        p.put("name","Uno Card Game");
        p.put("price",3.0);
        p.put("position",2);
        p.put("category","Games");
        products.add(new HashMap<>(p));

        map.put("products",products);
        return map;
    }

    static Map<String, Object> ECommerceEventsOrderCompleted() {
        Map<String, Object> map = new HashMap<>();
        map.put("checkout_id","fksdjfsdjfisjf9sdfjsd9f");
        map.put("order_id","50314b8e9bcf000000000000");
        map.put("affiliation","Google Store");
        map.put("total",27.50);
        map.put("subtotal",22.50);
        map.put("revenue",25.00);
        map.put("shipping",3);
        map.put("tax",2);
        map.put("discount",2.5);
        map.put("coupon","hasbros");
        map.put("currency","USD");

        ArrayList<Map<String, Object>> products = new ArrayList<>();
        Map<String, Object> p = new HashMap<>();
        p.put("product_id","507f1f77bcf86cd799439011");
        p.put("sku","45790-32");
        p.put("name","Monopoly: 3rd Edition");
        p.put("price",19.0);
        p.put("position",1);
        p.put("category","Games");
        p.put("url","https://www.example.com/product/path-32");
        p.put("image_url","https://www.example.com/product/path.jpg-32");
        products.add(new HashMap<>(p));
        p.clear();
        p.put("product_id","505bd76785ebb509fc183733");
        p.put("sku","46493-32");
        p.put("name","Uno Card Game");
        p.put("price",3.0);
        p.put("position",2);
        p.put("category","Games");
        products.add(new HashMap<>(p));

        map.put("products",products);
        return map;
    }

    static Map<String, Object> ECommerceEventsOrderRefunded() {
        Map<String, Object> map = new HashMap<>();
        map.put("order_id","50314b8e9bcf000000000000");
        map.put("total",30);
        map.put("currency","USD");

        ArrayList<Map<String, Object>> products = new ArrayList<>();
        Map<String, Object> p = new HashMap<>();
        p.put("product_id","507f1f77bcf86cd799439011");
        p.put("sku","45790-32");
        p.put("name","Monopoly: 3rd Edition");
        p.put("price",19.0);
        p.put("position",1);
        p.put("category","Games");
        p.put("url","https://www.example.com/product/path-32");
        p.put("image_url","https://www.example.com/product/path.jpg-32");
        products.add(new HashMap<>(p));
        p.clear();
        p.put("product_id","505bd76785ebb509fc183733");
        p.put("sku","46493-32");
        p.put("name","Uno Card Game");
        p.put("price",3.0);
        p.put("position",2);
        p.put("category","Games");
        products.add(new HashMap<>(p));

        map.put("products",products);
        return map;
    }

    static Map<String, Object> ECommerceEventsOrderCancelled() {
        Map<String, Object> map = new HashMap<>();
        map.put("order_id","50314b8e9bcf000000000000");
        map.put("affiliation","Google Store");
        map.put("total",27.50);
        map.put("subtotal",22.50);
        map.put("revenue",25.00);
        map.put("shipping",3);
        map.put("tax",2);
        map.put("discount",2.5);
        map.put("coupon","hasbros");
        map.put("currency","USD");

        ArrayList<Map<String, Object>> products = new ArrayList<>();
        Map<String, Object> p = new HashMap<>();
        p.put("product_id","507f1f77bcf86cd799439011");
        p.put("sku","45790-32");
        p.put("name","Monopoly: 3rd Edition");
        p.put("price",19.0);
        p.put("position",1);
        p.put("category","Games");
        p.put("url","https://www.example.com/product/path-32");
        p.put("image_url","https://www.example.com/product/path.jpg-32");
        products.add(new HashMap<>(p));
        p.clear();
        p.put("product_id","505bd76785ebb509fc183733");
        p.put("sku","46493-32");
        p.put("name","Uno Card Game");
        p.put("price",3.0);
        p.put("position",2);
        p.put("category","Games");
        products.add(new HashMap<>(p));

        map.put("products",products);
        return map;
    }

    static Map<String, Object> ECommerceEventsCouponEntered() {
        Map<String, Object> map = new HashMap<>();
        map.put("order_id","50314b8e9bcf000000000000");
        map.put("cart_id","923923929jd29jd92dj9j93fj3");
        map.put("coupon_id","may_deals_2016");
        return map;
    }

    static Map<String, Object> ECommerceEventsCouponApplied() {
        Map<String, Object> map = new HashMap<>();
        map.put("order_id","50314b8e9bcf000000000000");
        map.put("cart_id","923923929jd29jd92dj9j93fj3");
        map.put("coupon_id","may_deals_2016");
        map.put("coupon_name","May Deals 2016");
        map.put("discount",23.32);
        return map;
    }

    static Map<String, Object> ECommerceEventsCouponDenied() {
        Map<String, Object> map = new HashMap<>();
        map.put("order_id","50314b8e9bcf000000000000");
        map.put("cart_id","923923929jd29jd92dj9j93fj3");
        map.put("coupon_id","may_deals_2016");
        map.put("reason","Coupon expired");
        return map;
    }

    static Map<String, Object> ECommerceEventsCouponRemoved() {
        Map<String, Object> map = new HashMap<>();
        map.put("order_id","50314b8e9bcf000000000000");
        map.put("cart_id","923923929jd29jd92dj9j93fj3");
        map.put("coupon_id","may_deals_2016");
        map.put("coupon_name","May Deals 2016");
        map.put("discount",23.32);
        return map;
    }


    static Map<String, Object> ECommerceEventsProductAddedToWishlist() {
        Map<String, Object> map = new HashMap<>();
        map.put("wishlist_id","skdjsidjsdkdj29j");
        map.put("wishlist_name","Loved Games");
        map.put("product_id","507f1f77bcf86cd799439011");
        map.put("sku","G-32");
        map.put("category","Games");
        map.put("name","Monopoly: 3rd Edition");
        map.put("brand","Hasbro");
        map.put("variant","200 pieces");
        map.put("price",18.99);
        map.put("quantity",1);
        map.put("coupon","MAYDEALS");
        map.put("position",3);
        map.put("url","https://www.example.com/product/path");
        map.put("image_url","https://www.example.com/product/path.jpg");
        return map;
    }

    static Map<String, Object> ECommerceEventsProductRemovedFromWishlist() {
        Map<String, Object> map = new HashMap<>();
        map.put("wishlist_id","skdjsidjsdkdj29j");
        map.put("wishlist_name","Loved Games");
        map.put("product_id","507f1f77bcf86cd799439011");
        map.put("sku","G-32");
        map.put("category","Games");
        map.put("name","Monopoly: 3rd Edition");
        map.put("brand","Hasbro");
        map.put("variant","200 pieces");
        map.put("price",18.99);
        map.put("quantity",1);
        map.put("coupon","MAYDEALS");
        map.put("position",3);
        map.put("url","https://www.example.com/product/path");
        map.put("image_url","https://www.example.com/product/path.jpg");
        return map;
    }

    static Map<String, Object> ECommerceEventsWishlistProductAddedToCart() {
        Map<String, Object> map = new HashMap<>();
        map.put("wishlist_id","skdjsidjsdkdj29j");
        map.put("wishlist_name","Loved Games");
        map.put("cart_id","'99j2d92j9dj29dj29d2d'");
        map.put("product_id","507f1f77bcf86cd799439011");
        map.put("sku","G-32");
        map.put("category","Games");
        map.put("name","Monopoly: 3rd Edition");
        map.put("brand","Hasbro");
        map.put("variant","200 pieces");
        map.put("price",18.99);
        map.put("quantity",1);
        map.put("coupon","MAYDEALS");
        map.put("position",3);
        map.put("url","https://www.example.com/product/path");
        map.put("image_url","https://www.example.com/product/path.jpg");
        return map;
    }
    static Map<String, Object> ECommerceEventsProductShared() {
        Map<String, Object> map = new HashMap<>();
        map.put("share_via","email");
        map.put("share_message","Hey, check out this item");
        map.put("recipient","friend@example.com");
        map.put("product_id","507f1f77bcf86cd799439011");
        map.put("sku","G-32");
        map.put("category","Games");
        map.put("name","Monopoly: 3rd Edition");
        map.put("brand","Hasbro");
        map.put("variant","200 pieces");
        map.put("price",18.99);
        map.put("quantity",1);
        map.put("coupon","MAYDEALS");
        map.put("position",3);
        map.put("url","https://www.example.com/product/path");
        map.put("image_url","https://www.example.com/product/path.jpg");
        return map;
    }

    static Map<String, Object> ECommerceEventsProductReviewed() {
        Map<String, Object> map = new HashMap<>();
        map.put("product_id","507f1f77bcf86cd799439011");
        map.put("review_id","kdfjrj39fj39jf3");
        map.put("review_body","I love this product");
        map.put("rating",5);
        return map;
    }

    static Map<String, Object> ECommerceEventsCartShared() {
        Map<String, Object> map = new HashMap<>();
        map.put("share_via","email");
        map.put("share_message","Hey, check out this item");
        map.put("recipient","friend@example.com");
        map.put("cart_id","d92jd29jd92jd29j92d92jd");

        ArrayList<Map<String, Object>> products = new ArrayList<>();
        Map<String, Object> p = new HashMap<>();
        p.put("product_id","507f1f77bcf86cd799439011");
        products.add(new HashMap<>(p));
        p.clear();
        p.put("product_id","505bd76785ebb509fc183733");
        products.add(new HashMap<>(p));

        map.put("products",products);
        return map;
    }
}
