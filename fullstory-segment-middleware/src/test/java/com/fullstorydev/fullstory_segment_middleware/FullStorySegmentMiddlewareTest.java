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

import java.util.Date;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;

@RunWith(MockitoJUnitRunner.class)
public class FullStorySegmentMiddlewareTest {
    @Mock
    Context mockContext;
    @Mock
    SharedPreferences mockPrefs;
//    @Mock
//    SharedPreferences.Editor mockEditor;

    FullStorySegmentMiddleware fullStorySegmentMiddleware;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        Mockito.when(mockContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mockPrefs);
//        Mockito.when(mockPrefs.getString("SegmentWriteMockKey", null)).thenReturn("SegmentWriteMockKey");

        fullStorySegmentMiddleware = new FullStorySegmentMiddleware(mockContext, "SegmentWriteMockKey");
    }

    // Unit test for getSuffixStringFromSimpleObject
    @Test
    public void getSuffixStringFromSimpleObject_String_ReturnsStr() {
        String input = "this is a string";
        String output = fullStorySegmentMiddleware.getSuffixStringFromSimpleObject(input);
        Assert.assertEquals("_str", output);
    }

//    @Test
//    public void getSuffixStringFromSimpleObject_Char_ReturnsStr() {
//        char input = 'a';
//        String output = fullStorySegmentMiddleware.getSuffixStringFromSimpleObject(input);
//        Assert.assertEquals("_str", output);
//    }

    @Test
    public void getSuffixStringFromSimpleObject_Int_ReturnsInt() {
        int input = 1;
        String output = fullStorySegmentMiddleware.getSuffixStringFromSimpleObject(input);
        Assert.assertEquals("_int", output);
    }

//    @Test
//    public void getSuffixStringFromSimpleObject_Short_ReturnsInt() {
//        short input = 1;
//        String output = fullStorySegmentMiddleware.getSuffixStringFromSimpleObject(input);
//        Assert.assertEquals("_int", output);
//    }
//
//    @Test
//    public void getSuffixStringFromSimpleObject_Long_ReturnsInt() {
//        long input = 1;
//        String output = fullStorySegmentMiddleware.getSuffixStringFromSimpleObject(input);
//        Assert.assertEquals("_int", output);
//    }

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
        Object input = new Object();
        String output = fullStorySegmentMiddleware.getSuffixStringFromSimpleObject(input);
        Assert.assertEquals("", output);
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


}
