package com.fullstorydev.fullstory_segment_middleware;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@RunWith(MockitoJUnitRunner.class)
public class FSSuffixedPropertiesTest {

    FSSuffixedProperties fsSuffixedProperties;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        fsSuffixedProperties = new FSSuffixedProperties(new HashMap<>());
    }


    // constructor: convert input properties into FullStory custom events compatible suffixed properties
    @Test
    public void constructor_MapContainArrayOfMaps_ReturnsFlattenedMap() {
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
        fsSuffixedProperties = new FSSuffixedProperties(input);
        Assert.assertEquals(output, fsSuffixedProperties.suffixedProperties);
    }

    @Test
    public void constructor_MapContainInt_ReturnsMapWithSuffixedKey() {
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
        fsSuffixedProperties = new FSSuffixedProperties(input);
        Assert.assertEquals(output, fsSuffixedProperties.suffixedProperties);
    }

    @Test
    public void constructor_MapContainString_ReturnsMapWithSuffixedKey() {
        Map<String, Object>input = new HashMap<>();
        String i = "a string";
        input.put("string", i);
        input.put("literal", "another string");

        Map<String, Object> output = new HashMap<String, Object>() {{
            put("string_str", i);
            put("literal_str", "another string");
        }};
        fsSuffixedProperties = new FSSuffixedProperties(input);
        Assert.assertEquals(output, fsSuffixedProperties.suffixedProperties);
    }

    @Test
    public void constructor_MapContainReal_ReturnsMapWithSuffixedKey() {
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
        fsSuffixedProperties = new FSSuffixedProperties(input);
        Assert.assertEquals(output, fsSuffixedProperties.suffixedProperties);
    }
    
    // getSuffixStringFromSimpleObject: return suffix string for an object
    @Test
    public void getSuffixStringFromSimpleObject_String_ReturnsStr() {
        String input = "this is a string";
        String output = fsSuffixedProperties.getSuffixStringFromSimpleObject(input);
        Assert.assertEquals("_str", output);
    }

    @Test
    public void getSuffixStringFromSimpleObject_Char_ReturnsStr() {
        char input = 'a';
        String output = fsSuffixedProperties.getSuffixStringFromSimpleObject(input);
        Assert.assertEquals("_str", output);
    }

    @Test
    public void getSuffixStringFromSimpleObject_Int_ReturnsInt() {
        int input = 1;
        String output = fsSuffixedProperties.getSuffixStringFromSimpleObject(input);
        Assert.assertEquals("_int", output);
    }

    @Test
    public void getSuffixStringFromSimpleObject_Short_ReturnsInt() {
        short input = 1;
        String output = fsSuffixedProperties.getSuffixStringFromSimpleObject(input);
        Assert.assertEquals("_int", output);
    }

    @Test
    public void getSuffixStringFromSimpleObject_Long_ReturnsInt() {
        long input = 1;
        String output = fsSuffixedProperties.getSuffixStringFromSimpleObject(input);
        Assert.assertEquals("_int", output);
    }

    @Test
    public void getSuffixStringFromSimpleObject_Double_ReturnsReal() {
        double input = 1.3;
        String output = fsSuffixedProperties.getSuffixStringFromSimpleObject(input);
        Assert.assertEquals("_real", output);
    }

    @Test
    public void getSuffixStringFromSimpleObject_Float_ReturnsReal() {
        float input = 1.3f;
        String output = fsSuffixedProperties.getSuffixStringFromSimpleObject(input);
        Assert.assertEquals("_real", output);
    }

    @Test
    public void getSuffixStringFromSimpleObject_Boolean_ReturnsBool() {
        String output = fsSuffixedProperties.getSuffixStringFromSimpleObject(true);
        Assert.assertEquals("_bool", output);
    }

    @Test
    public void getSuffixStringFromSimpleObject_Date_ReturnsDate() {
        Date input = new Date();
        String output = fsSuffixedProperties.getSuffixStringFromSimpleObject(input);
        Assert.assertEquals("_date", output);
    }

    @Test
    public void getSuffixStringFromSimpleObject_Object_ReturnsEmpty() {
        // if we can not parse the object as any above types, then return empty suffix and send object as-is
        Object input = new Object();
        String output = fsSuffixedProperties.getSuffixStringFromSimpleObject(input);
        Assert.assertEquals("", output);
    }

    // getArrayObjectFromArray: convert primitive type array into object array, return object array as-is
    @Test
    public void getArrayObjectFromArray_ObjectArray_ReturnsObjectArray() {
        // Array of any objects
        Object[] input = new Object[]{new HashMap<String, Object>(), new ArrayList<>(), null};
        Object[] output = fsSuffixedProperties.getArrayObjectFromArray(input);
        Assert.assertArrayEquals(input, output);
    }

    @Test
    public void getArrayObjectFromArray_IntArray_ReturnsObjectArray() {
        // Array of Primitive values
        int[] input = new int[]{1, 2, 3};
        Object[] output = fsSuffixedProperties.getArrayObjectFromArray(input);
        Assert.assertArrayEquals(new Object[]{1, 2, 3}, output);
    }

    @Test
    public void getArrayObjectFromArray_StringArray_ReturnsObjectArray() {
        String[] input = new String[]{"str1", "str2", "str3"};
        Object[] output = fsSuffixedProperties.getArrayObjectFromArray(input);
        Assert.assertArrayEquals(new Object[]{"str1", "str2", "str3"}, output);
    }

    // addSimpleObjectToMap: add input key/value pair to existing map
    // when input properties contain array of objects, suffixed key becomes the same for all objects in array, flatten them into ArrayList
    @Test
    public void addSimpleObjectToMap_AddString_WithNewKey() {
        String key = "input.key_str";
        String val = "val";
        fsSuffixedProperties.addSimpleObject(key, val);
        Map<String, Object> output = new HashMap<>();
        output.put(key, "val");
        Assert.assertEquals(output, fsSuffixedProperties.suffixedProperties);
    }

    @Test
    public void addSimpleObjectToMap_AddString_WithExistingObject() {
        String key = "input.key_str";
        Object obj = "val1";
        Map<String, Object> map = new HashMap<>();
        map.put(key, "val2");
        fsSuffixedProperties.suffixedProperties = map;
        fsSuffixedProperties.addSimpleObject(key, obj);
        Map<String, Object> output = new HashMap<>();
        output.put(key, new ArrayList<>(Arrays.asList("val1","val2")));
        Assert.assertEquals(output, fsSuffixedProperties.suffixedProperties);
    }

    @Test
    public void addSimpleObjectToMap_AddString_WithExistingArray() {
        String key = "key_str";
        Object obj = "val1";
        Map<String, Object> map = new HashMap<>();
        map.put(key, new ArrayList<>(Arrays.asList("val2","val3")));
        fsSuffixedProperties.suffixedProperties = map;
        fsSuffixedProperties.addSimpleObject(key, obj);

        // new object get's prepended into array, map should become the following order
        Map<String, Object> output = new HashMap<>();
        output.put(key,  new ArrayList<>(Arrays.asList("val1","val2","val3")));
        Assert.assertEquals(output, fsSuffixedProperties.suffixedProperties);
    }

    // pluralizeAllArrayKeysInMap: enumerating through input map, suffix key with "s" when the value is ArrayList
    @Test
    public void pluralizeAllArrayKeysInMap_NoArrayInMap() {
        Map<String, Object> input = new HashMap<>();
        input.put("key_str","value_string");
        Map<String, Object> output = new HashMap<>(input);

        fsSuffixedProperties.suffixedProperties = input;
        fsSuffixedProperties.pluralizeAllArrayKeys();
        Assert.assertEquals(output, fsSuffixedProperties.suffixedProperties);
    }

    @Test
    public void pluralizeAllArrayKeysInMap_HasArrayInMap() {
        Map<String, Object> input = new HashMap<>();
        input.put("key_str", new ArrayList<>(Arrays.asList("val1","val2")));
        Map<String, Object> output = new HashMap<>();
        output.put("key_strs", new ArrayList<>(Arrays.asList("val1","val2")));
        fsSuffixedProperties.suffixedProperties = input;
        fsSuffixedProperties.pluralizeAllArrayKeys();
        Assert.assertEquals(output, fsSuffixedProperties.suffixedProperties);
    }

    // Test for constructor with Segment E Commerce Events
    @Test
    public void constructor_ECommerceEventsProductsSearched() {
        Map<String,Object> input = SegmentSpecUtils.ECommerceEventsProductsSearched();
        fsSuffixedProperties = new FSSuffixedProperties(input);
        Assert.assertEquals(FSPropertiesUtils.ECommerceEventsProductsSearched(), fsSuffixedProperties.suffixedProperties);
    }

    @Test
    public void constructor_ECommerceEventsProductListViewed() {
        Map<String,Object> input = SegmentSpecUtils.ECommerceEventsProductListViewed();
        fsSuffixedProperties = new FSSuffixedProperties(input);
        Assert.assertEquals(FSPropertiesUtils.ECommerceEventsProductListViewed(), fsSuffixedProperties.suffixedProperties);
    }

    @Test
    public void constructor_ECommerceEventsProductListFiltered() {
        Map<String,Object> input = SegmentSpecUtils.ECommerceEventsProductListFiltered();
        fsSuffixedProperties = new FSSuffixedProperties(input);
        Assert.assertEquals(FSPropertiesUtils.ECommerceEventsProductListFiltered(), fsSuffixedProperties.suffixedProperties);
    }

    @Test
    public void constructor_ECommerceEventsPromotionViewed() {
        Map<String,Object> input = SegmentSpecUtils.ECommerceEventsPromotionViewed();
        fsSuffixedProperties = new FSSuffixedProperties(input);
        Assert.assertEquals(FSPropertiesUtils.ECommerceEventsPromotionViewed(), fsSuffixedProperties.suffixedProperties);
    }

    @Test
    public void constructor_ECommerceEventsPromotionClicked() {
        Map<String,Object> input = SegmentSpecUtils.ECommerceEventsPromotionClicked();
        fsSuffixedProperties = new FSSuffixedProperties(input);
        Assert.assertEquals(FSPropertiesUtils.ECommerceEventsPromotionClicked(), fsSuffixedProperties.suffixedProperties);
    }

    @Test
    public void constructor_ECommerceEventsProductClicked() {
        Map<String,Object> input = SegmentSpecUtils.ECommerceEventsProductClicked();
        fsSuffixedProperties = new FSSuffixedProperties(input);
        Assert.assertEquals(FSPropertiesUtils.ECommerceEventsProductClicked(), fsSuffixedProperties.suffixedProperties);
    }

    @Test
    public void constructor_ECommerceEventsProductViewed() {
        Map<String,Object> input = SegmentSpecUtils.ECommerceEventsProductViewed();
        fsSuffixedProperties = new FSSuffixedProperties(input);
        Assert.assertEquals(FSPropertiesUtils.ECommerceEventsProductViewed(), fsSuffixedProperties.suffixedProperties);
    }

    @Test
    public void constructor_ECommerceEventsProductAdded() {
        Map<String,Object> input = SegmentSpecUtils.ECommerceEventsProductAdded();
        fsSuffixedProperties = new FSSuffixedProperties(input);
        Assert.assertEquals(FSPropertiesUtils.ECommerceEventsProductAdded(), fsSuffixedProperties.suffixedProperties);
    }

    @Test
    public void constructor_ECommerceEventsProductRemoved() {
        Map<String,Object> input = SegmentSpecUtils.ECommerceEventsProductRemoved();
        fsSuffixedProperties = new FSSuffixedProperties(input);
        Assert.assertEquals(FSPropertiesUtils.ECommerceEventsProductRemoved(), fsSuffixedProperties.suffixedProperties);
    }

    @Test
    public void constructor_ECommerceEventsCartViewed() {
        Map<String,Object> input = SegmentSpecUtils.ECommerceEventsCartViewed();
        fsSuffixedProperties = new FSSuffixedProperties(input);
        Assert.assertEquals(FSPropertiesUtils.ECommerceEventsCartViewed(), fsSuffixedProperties.suffixedProperties);
    }

    @Test
    public void constructor_ECommerceEventsCheckoutStarted() {
        Map<String,Object> input = SegmentSpecUtils.ECommerceEventsCheckoutStarted();
        fsSuffixedProperties = new FSSuffixedProperties(input);
        Assert.assertEquals(FSPropertiesUtils.ECommerceEventsCheckoutStarted(), fsSuffixedProperties.suffixedProperties);
    }

    @Test
    public void constructor_ECommerceEventsCheckoutStepViewed() {
        Map<String,Object> input = SegmentSpecUtils.ECommerceEventsCheckoutStepViewed();
        fsSuffixedProperties = new FSSuffixedProperties(input);
        Assert.assertEquals(FSPropertiesUtils.ECommerceEventsCheckoutStepViewed(), fsSuffixedProperties.suffixedProperties);
    }

    @Test
    public void constructor_ECommerceEventsCheckoutStepCompleted() {
        Map<String,Object> input = SegmentSpecUtils.ECommerceEventsCheckoutStepCompleted();
        fsSuffixedProperties = new FSSuffixedProperties(input);
        Assert.assertEquals(FSPropertiesUtils.ECommerceEventsCheckoutStepCompleted(), fsSuffixedProperties.suffixedProperties);
    }

    @Test
    public void constructor_ECommerceEventsCheckoutPaymentInfoEntered() {
        Map<String,Object> input = SegmentSpecUtils.ECommerceEventsCheckoutPaymentInfoEntered();
        fsSuffixedProperties = new FSSuffixedProperties(input);
        Assert.assertEquals(FSPropertiesUtils.ECommerceEventsCheckoutPaymentInfoEntered(), fsSuffixedProperties.suffixedProperties);
    }

    @Test
    public void constructor_ECommerceEventsOrderUpdated() {
        Map<String,Object> input = SegmentSpecUtils.ECommerceEventsOrderUpdated();
        fsSuffixedProperties = new FSSuffixedProperties(input);
        Assert.assertEquals(FSPropertiesUtils.ECommerceEventsOrderUpdated(), fsSuffixedProperties.suffixedProperties);
    }

    @Test
    public void constructor_ECommerceEventsOrderCompleted() {
        Map<String,Object> input = SegmentSpecUtils.ECommerceEventsOrderCompleted();
        fsSuffixedProperties = new FSSuffixedProperties(input);
        Assert.assertEquals(FSPropertiesUtils.ECommerceEventsOrderCompleted(), fsSuffixedProperties.suffixedProperties);
    }

    @Test
    public void constructor_ECommerceEventsOrderRefunded() {
        Map<String,Object> input = SegmentSpecUtils.ECommerceEventsOrderRefunded();
        fsSuffixedProperties = new FSSuffixedProperties(input);
        Assert.assertEquals(FSPropertiesUtils.ECommerceEventsOrderRefunded(), fsSuffixedProperties.suffixedProperties);
    }

    @Test
    public void constructor_ECommerceEventsOrderCancelled() {
        Map<String,Object> input = SegmentSpecUtils.ECommerceEventsOrderCancelled();
        fsSuffixedProperties = new FSSuffixedProperties(input);
        Assert.assertEquals(FSPropertiesUtils.ECommerceEventsOrderCancelled(), fsSuffixedProperties.suffixedProperties);
    }

    @Test
    public void constructor_ECommerceEventsCouponEntered() {
        Map<String,Object> input = SegmentSpecUtils.ECommerceEventsCouponEntered();
        fsSuffixedProperties = new FSSuffixedProperties(input);
        Assert.assertEquals(FSPropertiesUtils.ECommerceEventsCouponEntered(), fsSuffixedProperties.suffixedProperties);
    }

    @Test
    public void constructor_ECommerceEventsCouponApplied() {
        Map<String,Object> input = SegmentSpecUtils.ECommerceEventsCouponApplied();
        fsSuffixedProperties = new FSSuffixedProperties(input);
        Assert.assertEquals(FSPropertiesUtils.ECommerceEventsCouponApplied(), fsSuffixedProperties.suffixedProperties);
    }

    @Test
    public void constructor_ECommerceEventsCouponDenied() {
        Map<String,Object> input = SegmentSpecUtils.ECommerceEventsCouponDenied();
        fsSuffixedProperties = new FSSuffixedProperties(input);
        Assert.assertEquals(FSPropertiesUtils.ECommerceEventsCouponDenied(), fsSuffixedProperties.suffixedProperties);
    }

    @Test
    public void constructor_ECommerceEventsCouponRemoved() {
        Map<String,Object> input = SegmentSpecUtils.ECommerceEventsCouponRemoved();
        fsSuffixedProperties = new FSSuffixedProperties(input);
        Assert.assertEquals(FSPropertiesUtils.ECommerceEventsCouponRemoved(), fsSuffixedProperties.suffixedProperties);
    }

    @Test
    public void constructor_ECommerceEventsProductAddedToWishlist() {
        Map<String,Object> input = SegmentSpecUtils.ECommerceEventsProductAddedToWishlist();
        fsSuffixedProperties = new FSSuffixedProperties(input);
        Assert.assertEquals(FSPropertiesUtils.ECommerceEventsProductAddedToWishlist(), fsSuffixedProperties.suffixedProperties);
    }

    @Test
    public void constructor_ECommerceEventsProductRemovedFromWishlist() {
        Map<String,Object> input = SegmentSpecUtils.ECommerceEventsProductRemovedFromWishlist();
        fsSuffixedProperties = new FSSuffixedProperties(input);
        Assert.assertEquals(FSPropertiesUtils.ECommerceEventsProductRemovedFromWishlist(), fsSuffixedProperties.suffixedProperties);
    }

    @Test
    public void constructor_ECommerceEventsWishlistProductAddedToCart() {
        Map<String,Object> input = SegmentSpecUtils.ECommerceEventsWishlistProductAddedToCart();
        fsSuffixedProperties = new FSSuffixedProperties(input);
        Assert.assertEquals(FSPropertiesUtils.ECommerceEventsWishlistProductAddedToCart(), fsSuffixedProperties.suffixedProperties);
    }

    @Test
    public void constructor_ECommerceEventsProductShared() {
        Map<String,Object> input = SegmentSpecUtils.ECommerceEventsProductShared();
        fsSuffixedProperties = new FSSuffixedProperties(input);
        Assert.assertEquals(FSPropertiesUtils.ECommerceEventsProductShared(), fsSuffixedProperties.suffixedProperties);
    }

    @Test
    public void constructor_ECommerceEventsProductReviewed() {
        Map<String,Object> input = SegmentSpecUtils.ECommerceEventsProductReviewed();
        fsSuffixedProperties = new FSSuffixedProperties(input);
        Assert.assertEquals(FSPropertiesUtils.ECommerceEventsProductReviewed(), fsSuffixedProperties.suffixedProperties);
    }

    @Test
    public void constructor_ECommerceEventsCartShared() {
        Map<String,Object> input = SegmentSpecUtils.ECommerceEventsCartShared();
        fsSuffixedProperties = new FSSuffixedProperties(input);
        Assert.assertEquals(FSPropertiesUtils.ECommerceEventsCartShared(), fsSuffixedProperties.suffixedProperties);
    }
}
