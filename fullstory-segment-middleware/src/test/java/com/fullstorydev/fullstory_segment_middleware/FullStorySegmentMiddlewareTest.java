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

    @Test
    public void getSuffixedProps_String_ReturnsStr() {
        String input = "this is a string";
        String output = fullStorySegmentMiddleware.getSuffixStringFromSimpleObject(input);
        Assert.assertEquals("_str", output);
    }

    @Test
    public void getSuffixedProps_Char_ReturnsStr() {
        char input = 'a';
        String output = fullStorySegmentMiddleware.getSuffixStringFromSimpleObject(input);
        Assert.assertEquals("_str", output);
    }

    @Test
    public void getSuffixedProps_Int_ReturnsInt() {
        int input = 1;
        String output = fullStorySegmentMiddleware.getSuffixStringFromSimpleObject(input);
        Assert.assertEquals("_int", output);
    }

    @Test
    public void getSuffixedProps_Short_ReturnsInt() {
        short input = 1;
        String output = fullStorySegmentMiddleware.getSuffixStringFromSimpleObject(input);
        Assert.assertEquals("_int", output);
    }

    @Test
    public void getSuffixedProps_Long_ReturnsInt() {
        long input = 1;
        String output = fullStorySegmentMiddleware.getSuffixStringFromSimpleObject(input);
        Assert.assertEquals("_int", output);
    }

    @Test
    public void getSuffixedProps_Double_ReturnsReal() {
        double input = 1.3;
        String output = fullStorySegmentMiddleware.getSuffixStringFromSimpleObject(input);
        Assert.assertEquals("_real", output);
    }

    @Test
    public void getSuffixedProps_Float_ReturnsReal() {
        float input = 1.3f;
        String output = fullStorySegmentMiddleware.getSuffixStringFromSimpleObject(input);
        Assert.assertEquals("_real", output);
    }

    @Test
    public void getSuffixedProps_Boolean_ReturnsBool() {
        boolean input = true;
        String output = fullStorySegmentMiddleware.getSuffixStringFromSimpleObject(input);
        Assert.assertEquals("_bool", output);
    }

    @Test
    public void getSuffixedProps_Date_ReturnsDate() {
        Date input = new Date();
        String output = fullStorySegmentMiddleware.getSuffixStringFromSimpleObject(input);
        Assert.assertEquals("_date", output);
    }

    @Test
    public void getSuffixedProps_Object_ReturnsEmpty() {
        Object input = new Object();
        String output = fullStorySegmentMiddleware.getSuffixStringFromSimpleObject(input);
        Assert.assertEquals("", output);
    }

}
