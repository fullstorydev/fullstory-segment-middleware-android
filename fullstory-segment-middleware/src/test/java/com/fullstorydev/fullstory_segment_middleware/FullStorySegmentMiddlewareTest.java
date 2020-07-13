package com.fullstorydev.fullstory_segment_middleware;

import android.content.Context;
import android.content.SharedPreferences;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;

@RunWith(MockitoJUnitRunner.class)
public class FullStorySegmentMiddlewareTest {
    @Mock
    Context mockContext;
    @Mock
    SharedPreferences mockPrefs;

    FullStorySegmentMiddleware fullStorySegmentMiddleware;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        Mockito.when(mockContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mockPrefs);

        fullStorySegmentMiddleware = new FullStorySegmentMiddleware(mockContext, "SegmentWriteMockKey");
    }

    // getSuffixStringFromSimpleObject: return suffix string for an object
    @Test
    public void getSuffixStringFromSimpleObject_String_ReturnsStr() {
        String input = "this is a string";
        String output = fullStorySegmentMiddleware.getSuffixStringFromSimpleObject(input);
        Assert.assertEquals("_str", output);
    }

    @Test
    public void getSuffixStringFromSimpleObject_Char_ReturnsStr() {
        char input = 'a';
        String output = fullStorySegmentMiddleware.getSuffixStringFromSimpleObject(input);
        Assert.assertEquals("_str", output);
    }

    @Test
    public void getSuffixStringFromSimpleObject_Int_ReturnsInt() {
        int input = 1;
        String output = fullStorySegmentMiddleware.getSuffixStringFromSimpleObject(input);
        Assert.assertEquals("_int", output);
    }

    @Test
    public void getSuffixStringFromSimpleObject_Short_ReturnsInt() {
        short input = 1;
        String output = fullStorySegmentMiddleware.getSuffixStringFromSimpleObject(input);
        Assert.assertEquals("_int", output);
    }

    @Test
    public void getSuffixStringFromSimpleObject_Long_ReturnsInt() {
        long input = 1;
        String output = fullStorySegmentMiddleware.getSuffixStringFromSimpleObject(input);
        Assert.assertEquals("_int", output);
    }

    @Test
    public void getSuffixStringFromSimpleObject_Double_ReturnsReal() {
        double input = 1.3;
        String output = fullStorySegmentMiddleware.getSuffixStringFromSimpleObject(input);
        Assert.assertEquals("_real", output);
    }

    @Test
    public void getSuffixStringFromSimpleObject_Float_ReturnsReal() {
        float input = 1.3f;
        String output = fullStorySegmentMiddleware.getSuffixStringFromSimpleObject(input);
        Assert.assertEquals("_real", output);
    }

    @Test
    public void getSuffixStringFromSimpleObject_Boolean_ReturnsBool() {
        boolean input = true;
        String output = fullStorySegmentMiddleware.getSuffixStringFromSimpleObject(input);
        Assert.assertEquals("_bool", output);
    }

    @Test
    public void getSuffixStringFromSimpleObject_Date_ReturnsDate() {
        Date input = new Date();
        String output = fullStorySegmentMiddleware.getSuffixStringFromSimpleObject(input);
        Assert.assertEquals("_date", output);
    }

    @Test
    public void getSuffixStringFromSimpleObject_Object_ReturnsEmpty() {
        // if we can not parse the object as any above types, then return empty suffix and send object as-is
        Object input = new Object();
        String output = fullStorySegmentMiddleware.getSuffixStringFromSimpleObject(input);
        Assert.assertEquals("", output);
    }

    // getArrayObjectFromArray: convert primitive type array into object array, return object array as-is
    @Test
    public void getArrayObjectFromArray_ObjectArray_ReturnsObjectArray() {
        // Array of any objects
        Object[] input = new Object[]{new HashMap<String, Object>(), new ArrayList<>(), null};
        Object[] output = fullStorySegmentMiddleware.getArrayObjectFromArray(input);
        Assert.assertArrayEquals(input, output);
    }

    @Test
    public void getArrayObjectFromArray_IntArray_ReturnsObjectArray() {
        // Array of Primitive values
        int[] input = new int[]{1, 2, 3};
        Object[] output = fullStorySegmentMiddleware.getArrayObjectFromArray(input);
        Assert.assertArrayEquals(new Object[]{1, 2, 3}, output);
    }

    @Test
    public void getArrayObjectFromArray_StringArray_ReturnsObjectArray() {
        String[] input = new String[]{"str1", "str2", "str3"};
        Object[] output = fullStorySegmentMiddleware.getArrayObjectFromArray(input);
        Assert.assertArrayEquals(new Object[]{"str1", "str2", "str3"}, output);
    }

    // addSimpleObjectToMap: add input key/value pair to existing map
    // when input properties contain array of objects, suffixed key becomes the same for all objects in array, flatten them into ArrayList
    @Test
    public void addSimpleObjectToMap_AddString_WithNewKey() {
        String key = "input.key_str";
        String val = "val";
        Map<String, Object> map = new HashMap<>();
        fullStorySegmentMiddleware.addSimpleObjectToMap(map, key, val);
        Map<String, Object> output = new HashMap<>();
        output.put(key, "val");
        Assert.assertEquals(output, map);
    }

    @Test
    public void addSimpleObjectToMap_AddString_WithExistingObject() {
        String key = "input.key_str";
        Object obj = "val1";
        Map<String, Object> map = new HashMap<>();
        map.put(key, "val2");
        fullStorySegmentMiddleware.addSimpleObjectToMap(map, key, obj);
        Map<String, Object> output = new HashMap<>();
        output.put(key, new ArrayList<>(Arrays.asList("val1","val2")));
        Assert.assertEquals(output, map);
    }

    @Test
    public void addSimpleObjectToMap_AddString_WithExistingArray() {
        String key = "key_str";
        Object obj = "val1";
        Map<String, Object> map = new HashMap<>();
        map.put(key, new ArrayList<>(Arrays.asList("val2","val3")));
        fullStorySegmentMiddleware.addSimpleObjectToMap(map, key, obj);

        // new object get's prepended into array, map should become the following order
        Map<String, Object> output = new HashMap<>();
        output.put(key,  new ArrayList<>(Arrays.asList("val1","val2","val3")));
        Assert.assertEquals(output, map);
    }

    // pluralizeAllArrayKeysInMap: enumerating through input map, suffix key with "s" when the value is ArrayList
    @Test
    public void pluralizeAllArrayKeysInMap_NoArrayInMap() {
        Map<String, Object> input = new HashMap<>();
        input.put("key_str","value_string");
        Map<String, Object> output = new HashMap<>(input);
        fullStorySegmentMiddleware.pluralizeAllArrayKeysInMap(input);
        Assert.assertEquals(output, input);
    }

    @Test
    public void pluralizeAllArrayKeysInMap_HasArrayInMap() {
        Map<String, Object> input = new HashMap<>();
        input.put("key_str", new ArrayList<>(Arrays.asList("val1","val2")));
        Map<String, Object> output = new HashMap<>();
        output.put("key_strs", new ArrayList<>(Arrays.asList("val1","val2")));
        fullStorySegmentMiddleware.pluralizeAllArrayKeysInMap(input);
        Assert.assertEquals(output, input);
    }

    // getSuffixedProps: convert input properties into FullStory custom events compatible suffixed properties
    @Test
    public void getSuffixedProps_MapContainArrayOfMaps_ReturnsFlattenedMap() {
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(new HashMap<String, Object>() {{
            put("key1", "val1");
            put("key2", "val2");
        }});
        arrayList.add(new HashMap<String, Object>() {{
            put("key1", "secondVal1");
            put("key2", "secondVal2");
        }});
        Map<String, Object>input = new HashMap<>();
        input.put("input", arrayList);

        Map<String, Object> output = new HashMap<String, Object>() {{
            put("input.key1_strs", new ArrayList<>(Arrays.asList("val1","secondVal1")));
            put("input.key2_strs", new ArrayList<>(Arrays.asList("val2","secondVal2")));
        }};
        input = fullStorySegmentMiddleware.getSuffixedProps(input);
        Assert.assertEquals(output, input);
    }

    @Test
    public void getSuffixedProps_MapContainInt_ReturnsMapWithSuffixedKey() {
        Map<String, Object>input = new HashMap<>();
        int i = 3;
        Integer j = 4;
        input.put("int", i);
        input.put("Integer", j);
        input.put("literal", 10);

        Map<String, Object> output = new HashMap<String, Object>() {{
            put("int_int", 3);
            put("Integer_int", 4);
            put("literal_int", 10);
        }};
        input = fullStorySegmentMiddleware.getSuffixedProps(input);
        Assert.assertEquals(output, input);
    }

    @Test
    public void getSuffixedProps_MapContainString_ReturnsMapWithSuffixedKey() {
        Map<String, Object>input = new HashMap<>();
        String i = "a string";
        input.put("string", i);
        input.put("literal", "another string");

        Map<String, Object> output = new HashMap<String, Object>() {{
            put("string_str", i);
            put("literal_str", "another string");
        }};
        input = fullStorySegmentMiddleware.getSuffixedProps(input);
        Assert.assertEquals(output, input);
    }

    @Test
    public void getSuffixedProps_MapContainReal_ReturnsMapWithSuffixedKey() {
        Map<String, Object>input = new HashMap<>();
        float i = 3f;
        Float f = 3f;
        double j = 4.5;
        Double d = 4.5;
        input.put("float", i);
        input.put("double", j);
        input.put("Float", f);
        input.put("Double", d);

        Map<String, Object> output = new HashMap<String, Object>() {{
            put("float_real", i);
            put("double_real", j);
            put("Float_real", f);
            put("Double_real", d);
        }};
        input = fullStorySegmentMiddleware.getSuffixedProps(input);
        Assert.assertEquals(output, input);
    }

    // Test for getSuffixedProps with Segment E Commerce Events
    @Test
    public void getSuffixedProps_ECommerceEventsProductsSearched() {
        Map<String,Object> input = SegmentSpecUtils.ECommerceEventsProductsSearched();
        Map<String,Object> output = fullStorySegmentMiddleware.getSuffixedProps(input);
        Assert.assertEquals(FSPropertiesUtils.ECommerceEventsProductsSearched(), output);
    }

    @Test
    public void getSuffixedProps_ECommerceEventsProductListViewed() {
        Map<String,Object> input = SegmentSpecUtils.ECommerceEventsProductListViewed();
        Map<String,Object> output = fullStorySegmentMiddleware.getSuffixedProps(input);
        Assert.assertEquals(FSPropertiesUtils.ECommerceEventsProductListViewed(), output);
    }

    @Test
    public void getSuffixedProps_ECommerceEventsProductListFiltered() {
        Map<String,Object> input = SegmentSpecUtils.ECommerceEventsProductListFiltered();
        Map<String,Object> output = fullStorySegmentMiddleware.getSuffixedProps(input);
        Assert.assertEquals(FSPropertiesUtils.ECommerceEventsProductListFiltered(), output);
    }

    @Test
    public void getSuffixedProps_ECommerceEventsPromotionViewed() {
        Map<String,Object> input = SegmentSpecUtils.ECommerceEventsPromotionViewed();
        Map<String,Object> output = fullStorySegmentMiddleware.getSuffixedProps(input);
        Assert.assertEquals(FSPropertiesUtils.ECommerceEventsPromotionViewed(), output);
    }

    @Test
    public void getSuffixedProps_ECommerceEventsPromotionClicked() {
        Map<String,Object> input = SegmentSpecUtils.ECommerceEventsPromotionClicked();
        Map<String,Object> output = fullStorySegmentMiddleware.getSuffixedProps(input);
        Assert.assertEquals(FSPropertiesUtils.ECommerceEventsPromotionClicked(), output);
    }

    @Test
    public void getSuffixedProps_ECommerceEventsProductClicked() {
        Map<String,Object> input = SegmentSpecUtils.ECommerceEventsProductClicked();
        Map<String,Object> output = fullStorySegmentMiddleware.getSuffixedProps(input);
        Assert.assertEquals(FSPropertiesUtils.ECommerceEventsProductClicked(), output);
    }

    @Test
    public void getSuffixedProps_ECommerceEventsProductViewed() {
        Map<String,Object> input = SegmentSpecUtils.ECommerceEventsProductViewed();
        Map<String,Object> output = fullStorySegmentMiddleware.getSuffixedProps(input);
        Assert.assertEquals(FSPropertiesUtils.ECommerceEventsProductViewed(), output);
    }

    @Test
    public void getSuffixedProps_ECommerceEventsProductAdded() {
        Map<String,Object> input = SegmentSpecUtils.ECommerceEventsProductAdded();
        Map<String,Object> output = fullStorySegmentMiddleware.getSuffixedProps(input);
        Assert.assertEquals(FSPropertiesUtils.ECommerceEventsProductAdded(), output);
    }

    @Test
    public void getSuffixedProps_ECommerceEventsProductRemoved() {
        Map<String,Object> input = SegmentSpecUtils.ECommerceEventsProductRemoved();
        Map<String,Object> output = fullStorySegmentMiddleware.getSuffixedProps(input);
        Assert.assertEquals(FSPropertiesUtils.ECommerceEventsProductRemoved(), output);
    }

    @Test
    public void getSuffixedProps_ECommerceEventsCartViewed() {
        Map<String,Object> input = SegmentSpecUtils.ECommerceEventsCartViewed();
        Map<String,Object> output = fullStorySegmentMiddleware.getSuffixedProps(input);
        Assert.assertEquals(FSPropertiesUtils.ECommerceEventsCartViewed(), output);
    }

    @Test
    public void getSuffixedProps_ECommerceEventsCheckoutStarted() {
        Map<String,Object> input = SegmentSpecUtils.ECommerceEventsCheckoutStarted();
        Map<String,Object> output = fullStorySegmentMiddleware.getSuffixedProps(input);
        Assert.assertEquals(FSPropertiesUtils.ECommerceEventsCheckoutStarted(), output);
    }

    @Test
    public void getSuffixedProps_ECommerceEventsCheckoutStepViewed() {
        Map<String,Object> input = SegmentSpecUtils.ECommerceEventsCheckoutStepViewed();
        Map<String,Object> output = fullStorySegmentMiddleware.getSuffixedProps(input);
        Assert.assertEquals(FSPropertiesUtils.ECommerceEventsCheckoutStepViewed(), output);
    }

    @Test
    public void getSuffixedProps_ECommerceEventsCheckoutStepCompleted() {
        Map<String,Object> input = SegmentSpecUtils.ECommerceEventsCheckoutStepCompleted();
        Map<String,Object> output = fullStorySegmentMiddleware.getSuffixedProps(input);
        Assert.assertEquals(FSPropertiesUtils.ECommerceEventsCheckoutStepCompleted(), output);
    }

    @Test
    public void getSuffixedProps_ECommerceEventsCheckoutPaymentInfoEntered() {
        Map<String,Object> input = SegmentSpecUtils.ECommerceEventsCheckoutPaymentInfoEntered();
        Map<String,Object> output = fullStorySegmentMiddleware.getSuffixedProps(input);
        Assert.assertEquals(FSPropertiesUtils.ECommerceEventsCheckoutPaymentInfoEntered(), output);
    }

    @Test
    public void getSuffixedProps_ECommerceEventsOrderUpdated() {
        Map<String,Object> input = SegmentSpecUtils.ECommerceEventsOrderUpdated();
        Map<String,Object> output = fullStorySegmentMiddleware.getSuffixedProps(input);
        Assert.assertEquals(FSPropertiesUtils.ECommerceEventsOrderUpdated(), output);
    }

    @Test
    public void getSuffixedProps_ECommerceEventsOrderCompleted() {
        Map<String,Object> input = SegmentSpecUtils.ECommerceEventsOrderCompleted();
        Map<String,Object> output = fullStorySegmentMiddleware.getSuffixedProps(input);
        Assert.assertEquals(FSPropertiesUtils.ECommerceEventsOrderCompleted(), output);
    }

    @Test
    public void getSuffixedProps_ECommerceEventsOrderRefunded() {
        Map<String,Object> input = SegmentSpecUtils.ECommerceEventsOrderRefunded();
        Map<String,Object> output = fullStorySegmentMiddleware.getSuffixedProps(input);
        Assert.assertEquals(FSPropertiesUtils.ECommerceEventsOrderRefunded(), output);
    }

    @Test
    public void getSuffixedProps_ECommerceEventsOrderCancelled() {
        Map<String,Object> input = SegmentSpecUtils.ECommerceEventsOrderCancelled();
        Map<String,Object> output = fullStorySegmentMiddleware.getSuffixedProps(input);
        Assert.assertEquals(FSPropertiesUtils.ECommerceEventsOrderCancelled(), output);
    }

    @Test
    public void getSuffixedProps_ECommerceEventsCouponEntered() {
        Map<String,Object> input = SegmentSpecUtils.ECommerceEventsCouponEntered();
        Map<String,Object> output = fullStorySegmentMiddleware.getSuffixedProps(input);
        Assert.assertEquals(FSPropertiesUtils.ECommerceEventsCouponEntered(), output);
    }

    @Test
    public void getSuffixedProps_ECommerceEventsCouponApplied() {
        Map<String,Object> input = SegmentSpecUtils.ECommerceEventsCouponApplied();
        Map<String,Object> output = fullStorySegmentMiddleware.getSuffixedProps(input);
        Assert.assertEquals(FSPropertiesUtils.ECommerceEventsCouponApplied(), output);
    }

    @Test
    public void getSuffixedProps_ECommerceEventsCouponDenied() {
        Map<String,Object> input = SegmentSpecUtils.ECommerceEventsCouponDenied();
        Map<String,Object> output = fullStorySegmentMiddleware.getSuffixedProps(input);
        Assert.assertEquals(FSPropertiesUtils.ECommerceEventsCouponDenied(), output);
    }

    @Test
    public void getSuffixedProps_ECommerceEventsCouponRemoved() {
        Map<String,Object> input = SegmentSpecUtils.ECommerceEventsCouponRemoved();
        Map<String,Object> output = fullStorySegmentMiddleware.getSuffixedProps(input);
        Assert.assertEquals(FSPropertiesUtils.ECommerceEventsCouponRemoved(), output);
    }

    @Test
    public void getSuffixedProps_ECommerceEventsProductAddedToWishlist() {
        Map<String,Object> input = SegmentSpecUtils.ECommerceEventsProductAddedToWishlist();
        Map<String,Object> output = fullStorySegmentMiddleware.getSuffixedProps(input);
        Assert.assertEquals(FSPropertiesUtils.ECommerceEventsProductAddedToWishlist(), output);
    }

    @Test
    public void getSuffixedProps_ECommerceEventsProductRemovedFromWishlist() {
        Map<String,Object> input = SegmentSpecUtils.ECommerceEventsProductRemovedFromWishlist();
        Map<String,Object> output = fullStorySegmentMiddleware.getSuffixedProps(input);
        Assert.assertEquals(FSPropertiesUtils.ECommerceEventsProductRemovedFromWishlist(), output);
    }

    @Test
    public void getSuffixedProps_ECommerceEventsWishlistProductAddedToCart() {
        Map<String,Object> input = SegmentSpecUtils.ECommerceEventsWishlistProductAddedToCart();
        Map<String,Object> output = fullStorySegmentMiddleware.getSuffixedProps(input);
        Assert.assertEquals(FSPropertiesUtils.ECommerceEventsWishlistProductAddedToCart(), output);
    }

    @Test
    public void getSuffixedProps_ECommerceEventsProductShared() {
        Map<String,Object> input = SegmentSpecUtils.ECommerceEventsProductShared();
        Map<String,Object> output = fullStorySegmentMiddleware.getSuffixedProps(input);
        Assert.assertEquals(FSPropertiesUtils.ECommerceEventsProductShared(), output);
    }

    @Test
    public void getSuffixedProps_ECommerceEventsProductReviewed() {
        Map<String,Object> input = SegmentSpecUtils.ECommerceEventsProductReviewed();
        Map<String,Object> output = fullStorySegmentMiddleware.getSuffixedProps(input);
        Assert.assertEquals(FSPropertiesUtils.ECommerceEventsProductReviewed(), output);
    }

    @Test
    public void getSuffixedProps_ECommerceEventsCartShared() {
        Map<String,Object> input = SegmentSpecUtils.ECommerceEventsCartShared();
        Map<String,Object> output = fullStorySegmentMiddleware.getSuffixedProps(input);
        Assert.assertEquals(FSPropertiesUtils.ECommerceEventsCartShared(), output);
    }
}
