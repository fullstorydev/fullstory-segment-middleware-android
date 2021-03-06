package com.fullstorydev.fullstory_segment_middleware;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.fullstory.FS;
import com.segment.analytics.Middleware;
import com.segment.analytics.integrations.AliasPayload;
import com.segment.analytics.integrations.BasePayload;
import com.segment.analytics.integrations.GroupPayload;
import com.segment.analytics.integrations.IdentifyPayload;
import com.segment.analytics.integrations.ScreenPayload;
import com.segment.analytics.integrations.TrackPayload;
import com.segment.analytics.Properties;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.verification.VerificationModeFactory;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;


@RunWith(PowerMockRunner.class)
@PrepareForTest({FS.class, Log.class})
public class FullStorySegmentMiddlewareTest {
    @Mock
    Context mockContext;
    @Mock
    SharedPreferences mockPrefs;
    @Mock
    Middleware.Chain mockChain;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);

        when(mockContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mockPrefs);

        PowerMockito.mockStatic(FS.class);
        when(FS.getCurrentSessionURL(anyBoolean())).thenReturn("mock FullStory Session URL");
        when(FS.getCurrentSessionURL()).thenReturn("mock FullStory Session URL");
    }

    @Test
    public void constructor_WithAllowListedEvent_AssertAllowList() {
        List<String> allowList = new ArrayList<>();
        allowList.add("Product Added");
        FullStorySegmentMiddleware fullStorySegmentMiddleware = new FullStorySegmentMiddleware(mockContext, "SegmentWriteMockKey", allowList);

        Assert.assertEquals("Testing allowlistedEvents",allowList, fullStorySegmentMiddleware.allowlistedEvents);
        // TODO: assert logging for SharedPreferences listener code
    }

    @Test
    public void constructor_NoAllowListedEvent_AssertDefaultValues() {
        FullStorySegmentMiddleware fullStorySegmentMiddleware = new FullStorySegmentMiddleware(mockContext, "SegmentWriteMockKey");

        Assert.assertTrue("Testing enableFSSessionURLInEvents", fullStorySegmentMiddleware.enableFSSessionURLInEvents);
        Assert.assertFalse("Testing enableGroupTraitsAsUserVars", fullStorySegmentMiddleware.enableGroupTraitsAsUserVars);
        Assert.assertFalse("Testing enableSendScreenAsEvents", fullStorySegmentMiddleware.enableSendScreenAsEvents);
        Assert.assertFalse("Testing allowlistAllTrackEvents", fullStorySegmentMiddleware.allowlistAllTrackEvents);
        Assert.assertTrue("Testing allowlistedEvents", fullStorySegmentMiddleware.allowlistedEvents.isEmpty());
        // TODO: assert logging for null allowlist warning
    }

    @Test
    public void intercept_GroupPayloadChain_DisableGroupTraitsAsUserVars_ChainProceedCalled() {
        FullStorySegmentMiddleware fullStorySegmentMiddleware = new FullStorySegmentMiddleware(mockContext, "FakeSegmentWriteKey");
        fullStorySegmentMiddleware.enableGroupTraitsAsUserVars = false;
        fullStorySegmentMiddleware.enableFSSessionURLInEvents = false;

        GroupPayload groupPayload = new GroupPayload.Builder().userId("userId").groupId("groupId").build();
        when(mockChain.payload()).thenReturn(groupPayload);
        fullStorySegmentMiddleware.intercept(mockChain);

        // verify that we did not block chain.proceed, nor modified the payload
        verify(mockChain, times(1)).proceed(groupPayload);
    }

    @Test
    public void intercept_GroupPayloadChain_DisableGroupTraitsAsUserVars_FSSetUserVarsCalled() {
        FullStorySegmentMiddleware fullStorySegmentMiddleware = new FullStorySegmentMiddleware(mockContext, "FakeSegmentWriteKey");
        fullStorySegmentMiddleware.enableGroupTraitsAsUserVars = false;
        fullStorySegmentMiddleware.enableFSSessionURLInEvents = false;

        GroupPayload groupPayload = new GroupPayload.Builder().userId("userId").groupId("groupId").build();
        when(mockChain.payload()).thenReturn(groupPayload);
        fullStorySegmentMiddleware.intercept(mockChain);

        // verify that static method setUserVars is called with the correct vars
        Map<String, String> userVars = new HashMap<>();
        userVars.put("groupID_str", groupPayload.groupId());
        verifyStatic(FS.class, VerificationModeFactory.times(1));
        // IMPORTANT:  Call the static method you want to verify.
        // see Mokito document here: https://github.com/powermock/powermock/wiki/mockito#a-full-example-for-mocking-stubbing--verifying-static-method
        FS.setUserVars(userVars);
    }

    @Test
    public void intercept_GroupPayloadChain_EnableGroupTraitsAsUserVars_ChainProceedCalled() {
        FullStorySegmentMiddleware fullStorySegmentMiddleware = new FullStorySegmentMiddleware(mockContext, "SegmentWriteMockKey");
        fullStorySegmentMiddleware.enableGroupTraitsAsUserVars = true;
        fullStorySegmentMiddleware.enableFSSessionURLInEvents = false;

        Map<String, String> map = new HashMap<>();
        map.put("industry", "retail");
        GroupPayload groupPayload = new GroupPayload.Builder()
                .userId("userId")
                .groupId("groupId")
                .traits(map)
                .build();
        when(mockChain.payload()).thenReturn(groupPayload);
        fullStorySegmentMiddleware.intercept(mockChain);

        // verify that we did not block chain.proceed, nor modified the payload
        verify(mockChain, times(1)).proceed(groupPayload);
    }

    @Test
    public void intercept_GroupPayloadChain_EnableGroupTraitsAsUserVars_FSSetUserVarsCalled() {
        FullStorySegmentMiddleware fullStorySegmentMiddleware = new FullStorySegmentMiddleware(mockContext, "SegmentWriteMockKey");
        fullStorySegmentMiddleware.enableGroupTraitsAsUserVars = true;
        fullStorySegmentMiddleware.enableFSSessionURLInEvents = false;

        Map<String, String> map = new HashMap<>();
        map.put("industry", "retail");
        GroupPayload groupPayload = new GroupPayload.Builder()
                .userId("userId")
                .groupId("groupId")
                .traits(map)
                .build();
        when(mockChain.payload()).thenReturn(groupPayload);
        fullStorySegmentMiddleware.intercept(mockChain);

        Map<String, String> suffixedMap = new HashMap<>();
        suffixedMap.put("groupID_str", groupPayload.groupId());
        suffixedMap.put("industry_str", "retail");

        verifyStatic(FS.class, VerificationModeFactory.times(1));
        // IMPORTANT:  Call the static method you want to verify.
        // see Mokito document here: https://github.com/powermock/powermock/wiki/mockito#a-full-example-for-mocking-stubbing--verifying-static-method
        FS.setUserVars(suffixedMap);
    }

    @Test
    public void intercept_IdentifyPayloadChain_NoTraits_FSIdentifyCalled() {
        FullStorySegmentMiddleware fullStorySegmentMiddleware = new FullStorySegmentMiddleware(mockContext, "SegmentWriteMockKey");
        fullStorySegmentMiddleware.enableFSSessionURLInEvents = false;

        IdentifyPayload identifyPayload = new IdentifyPayload.Builder()
                .userId("userId")
                .build();
        when(mockChain.payload()).thenReturn(identifyPayload);
        fullStorySegmentMiddleware.intercept(mockChain);

        verifyStatic(FS.class, VerificationModeFactory.times(1));
        // IMPORTANT:  Call the static method you want to verify.
        // see Mokito document here: https://github.com/powermock/powermock/wiki/mockito#a-full-example-for-mocking-stubbing--verifying-static-method
        FS.identify(identifyPayload.userId(), new HashMap<>());
    }

    @Test
    public void intercept_IdentifyPayloadChain_UserTraits_ChainProceedCalled() {
        FullStorySegmentMiddleware fullStorySegmentMiddleware = new FullStorySegmentMiddleware(mockContext, "SegmentWriteMockKey");
        fullStorySegmentMiddleware.enableFSSessionURLInEvents = false;

        IdentifyPayload identifyPayload = new IdentifyPayload.Builder()
                .userId("userId")
                .build();
        when(mockChain.payload()).thenReturn(identifyPayload);
        fullStorySegmentMiddleware.intercept(mockChain);

        // verify that we did not block chain.proceed, nor modified the payload
        verify(mockChain, times(1)).proceed(identifyPayload);
    }

    @Test
    public void intercept_ScreenPayloadChain_EnableSendScreenAsEvents_FSEventCalled() {
        FullStorySegmentMiddleware fullStorySegmentMiddleware = new FullStorySegmentMiddleware(mockContext, "SegmentWriteMockKey");
        fullStorySegmentMiddleware.enableSendScreenAsEvents = true;
        fullStorySegmentMiddleware.enableFSSessionURLInEvents = false;

        ScreenPayload screenPayload = new ScreenPayload.Builder()
                .userId("userId")
                .name("MainActivity")
                .build();
        when(mockChain.payload()).thenReturn(screenPayload);
        fullStorySegmentMiddleware.intercept(mockChain);

        verifyStatic(FS.class, VerificationModeFactory.times(1));
        // IMPORTANT:  Call the static method you want to verify.
        // see Mokito document here: https://github.com/powermock/powermock/wiki/mockito#a-full-example-for-mocking-stubbing--verifying-static-method
        FS.event("Segment Screen: " + screenPayload.name(), screenPayload.properties());
    }

    @Test
    public void intercept_ScreenPayloadChain_EnableSendScreenAsEvents_ChainProceedCalled() {
        FullStorySegmentMiddleware fullStorySegmentMiddleware = new FullStorySegmentMiddleware(mockContext, "SegmentWriteMockKey");
        fullStorySegmentMiddleware.enableSendScreenAsEvents = true;
        fullStorySegmentMiddleware.enableFSSessionURLInEvents = false;

        ScreenPayload screenPayload = new ScreenPayload.Builder()
                .userId("userId")
                .name("MainActivity")
                .build();
        when(mockChain.payload()).thenReturn(screenPayload);
        fullStorySegmentMiddleware.intercept(mockChain);

        // verify that we did not block chain.proceed, nor modified the payload
        verify(mockChain, times(1)).proceed(screenPayload);
    }

    @Test
    public void intercept_ScreenPayloadChain_DisableSendScreenAsEvents_FSEventNotCalled() {
        FullStorySegmentMiddleware fullStorySegmentMiddleware = new FullStorySegmentMiddleware(mockContext, "SegmentWriteMockKey");
        fullStorySegmentMiddleware.enableSendScreenAsEvents = false;
        fullStorySegmentMiddleware.enableFSSessionURLInEvents = false;

        ScreenPayload screenPayload = new ScreenPayload.Builder()
                .userId("userId")
                .name("MainActivity")
                .build();
        when(mockChain.payload()).thenReturn(screenPayload);
        fullStorySegmentMiddleware.intercept(mockChain);

        verifyStatic(FS.class, VerificationModeFactory.times(0));
        // IMPORTANT:  Call the static method you want to verify.
        // see Mokito document here: https://github.com/powermock/powermock/wiki/mockito#a-full-example-for-mocking-stubbing--verifying-static-method
        FS.event(any(), any());
    }

    @Test
    public void intercept_ScreenPayloadChain_DisableSendScreenAsEvents_ChainProceedCalled() {
        FullStorySegmentMiddleware fullStorySegmentMiddleware = new FullStorySegmentMiddleware(mockContext, "SegmentWriteMockKey");
        fullStorySegmentMiddleware.enableSendScreenAsEvents = false;
        fullStorySegmentMiddleware.enableFSSessionURLInEvents = false;

        ScreenPayload screenPayload = new ScreenPayload.Builder()
                .userId("userId")
                .name("MainActivity")
                .build();
        when(mockChain.payload()).thenReturn(screenPayload);
        fullStorySegmentMiddleware.intercept(mockChain);

        // verify that we did not block chain.proceed, nor modified the payload
        verify(mockChain, times(1)).proceed(screenPayload);
    }

    @Test
    public void intercept_TrackPayloadChain_DisallowlistAllTrackEvents_NoAllowlistedEvents_FSEventNotCalled() {
        FullStorySegmentMiddleware fullStorySegmentMiddleware = new FullStorySegmentMiddleware(mockContext, "SegmentWriteMockKey");
        fullStorySegmentMiddleware.allowlistAllTrackEvents = false;
        fullStorySegmentMiddleware.enableFSSessionURLInEvents = false;

        TrackPayload trackPayload = new TrackPayload.Builder().userId("userId").event("event").build();
        when(mockChain.payload()).thenReturn(trackPayload);
        fullStorySegmentMiddleware.intercept(mockChain);

        verifyStatic(FS.class, VerificationModeFactory.times(0));
        // IMPORTANT:  Call the static method you want to verify.
        // see Mokito document here: https://github.com/powermock/powermock/wiki/mockito#a-full-example-for-mocking-stubbing--verifying-static-method
        FS.event(any(),any());
    }

    @Test
    public void intercept_TrackPayloadChain_DisllowlistAllTrackEvents_NoAllowlistedEvents_ChainProceedCalled() {
        FullStorySegmentMiddleware fullStorySegmentMiddleware = new FullStorySegmentMiddleware(mockContext, "SegmentWriteMockKey");
        fullStorySegmentMiddleware.allowlistAllTrackEvents = false;
        fullStorySegmentMiddleware.enableFSSessionURLInEvents = false;

        ScreenPayload screenPayload = new ScreenPayload.Builder()
                .userId("userId")
                .name("MainActivity")
                .build();
        when(mockChain.payload()).thenReturn(screenPayload);
        fullStorySegmentMiddleware.intercept(mockChain);

        // verify that we did not block chain.proceed, nor modified the payload
        verify(mockChain, times(1)).proceed(screenPayload);
    }

    @Test
    public void intercept_TrackPayloadChain_DisallowlistAllTrackEvents_EventAllowlisted_FSEventCalled() {
        ArrayList<String> allowList = new ArrayList<>();
        allowList.add("Product Added");
        FullStorySegmentMiddleware fullStorySegmentMiddleware = new FullStorySegmentMiddleware(mockContext, "SegmentWriteMockKey", allowList);
        fullStorySegmentMiddleware.allowlistAllTrackEvents = false;
        fullStorySegmentMiddleware.enableFSSessionURLInEvents = false;

        TrackPayload trackPayload = new TrackPayload.Builder().userId("userId").event("Product Added").build();
        when(mockChain.payload()).thenReturn(trackPayload);
        fullStorySegmentMiddleware.intercept(mockChain);

        verifyStatic(FS.class, VerificationModeFactory.times(1));
        // IMPORTANT:  Call the static method you want to verify.
        // see Mokito document here: https://github.com/powermock/powermock/wiki/mockito#a-full-example-for-mocking-stubbing--verifying-static-method
        FS.event(trackPayload.event(), trackPayload.properties());
    }

    @Test
    public void intercept_TrackPayloadChain_DisllowlistAllTrackEvents_AllowlistedEvents_ChainProceedCalled() {
        ArrayList<String> allowList = new ArrayList<>();
        allowList.add("Product Added");
        FullStorySegmentMiddleware fullStorySegmentMiddleware = new FullStorySegmentMiddleware(mockContext, "SegmentWriteMockKey", allowList);
        fullStorySegmentMiddleware.allowlistAllTrackEvents = false;
        fullStorySegmentMiddleware.enableFSSessionURLInEvents = false;

        TrackPayload trackPayload = new TrackPayload.Builder().userId("userId").event("Product Added").build();
        when(mockChain.payload()).thenReturn(trackPayload);
        fullStorySegmentMiddleware.intercept(mockChain);

        // verify that we did not block chain.proceed, nor modified the payload
        verify(mockChain, times(1)).proceed(trackPayload);
    }

    @Test
    public void intercept_TrackPayloadChain_AllowlistAllTrackEvents_FSEventCalled() {
        FullStorySegmentMiddleware fullStorySegmentMiddleware = new FullStorySegmentMiddleware(mockContext, "SegmentWriteMockKey");
        fullStorySegmentMiddleware.allowlistAllTrackEvents = true;
        fullStorySegmentMiddleware.enableFSSessionURLInEvents = false;
        TrackPayload trackPayload = new TrackPayload.Builder().userId("userId").event("event").build();
        when(mockChain.payload()).thenReturn(trackPayload);
        fullStorySegmentMiddleware.intercept(mockChain);

        verifyStatic(FS.class, VerificationModeFactory.times(1));
        // IMPORTANT:  Call the static method you want to verify.
        // see Mokito document here: https://github.com/powermock/powermock/wiki/mockito#a-full-example-for-mocking-stubbing--verifying-static-method
        FS.event(trackPayload.event(), trackPayload.properties());
    }

    @Test
    public void intercept_TrackPayloadChain_AllowlistAllTrackEvents_ChainProceedCalled() {
        FullStorySegmentMiddleware fullStorySegmentMiddleware = new FullStorySegmentMiddleware(mockContext, "SegmentWriteMockKey");
        fullStorySegmentMiddleware.allowlistAllTrackEvents = true;
        fullStorySegmentMiddleware.enableFSSessionURLInEvents = false;

        TrackPayload trackPayload = new TrackPayload.Builder().userId("userId").event("event").build();
        when(mockChain.payload()).thenReturn(trackPayload);
        fullStorySegmentMiddleware.intercept(mockChain);

        // verify that we did not block chain.proceed, nor modified the payload
        verify(mockChain, times(1)).proceed(trackPayload);
    }

    @Test
    public void intercept_AliasPayload_DefaultCase() {
        FullStorySegmentMiddleware fullStorySegmentMiddleware = new FullStorySegmentMiddleware(mockContext, "SegmentWriteMockKey");
        fullStorySegmentMiddleware.allowlistAllTrackEvents = true;
        fullStorySegmentMiddleware.enableFSSessionURLInEvents = false;

        AliasPayload aliasPayload = new AliasPayload.Builder().previousId("previousId").userId("userId").build();
        when(mockChain.payload()).thenReturn(aliasPayload);
        fullStorySegmentMiddleware.intercept(mockChain);

        // verify that we did not block chain.proceed, nor modified the payload
        verify(mockChain, times(1)).proceed(aliasPayload);
    }

    @Test
    public void intercept_EnableFSSessionURLInEvents_VerifyChainProceed() {
        FullStorySegmentMiddleware fullStorySegmentMiddleware = new FullStorySegmentMiddleware(mockContext, "SegmentWriteMockKey");
        fullStorySegmentMiddleware.enableFSSessionURLInEvents = true;

        TrackPayload inputTrackPayload = new TrackPayload.Builder().userId("userId").event("event").build();
        when(mockChain.payload()).thenReturn(inputTrackPayload);
        fullStorySegmentMiddleware.intercept(mockChain);

        Map<String, Object> newContext = new HashMap<>(inputTrackPayload.context());
        newContext.put("fullstoryUrl", FS.getCurrentSessionURL());
        Map<String, Object> newProperties = new HashMap<>(inputTrackPayload.properties());
        newProperties.put("fullstoryUrl", FS.getCurrentSessionURL());

        TrackPayload newTrackPayload = inputTrackPayload.toBuilder()
                .context(newContext)
                .properties(newProperties)
                .build();

        // verify that we did not block chain.proceed, nor modified the payload
        verify(mockChain, times(1)).proceed(newTrackPayload);
    }

    @Test
    public void addFSUrlToContext_AddsURLToContext() {
        FullStorySegmentMiddleware fullStorySegmentMiddleware = new FullStorySegmentMiddleware(mockContext, "SegmentWriteMockKey");
        Map<String, Object> input = new HashMap<>();
        fullStorySegmentMiddleware.addFSUrlToContext(input);

        Map<String, Object> expect = new HashMap<>();
        expect.put("fullstoryUrl", FS.getCurrentSessionURL());

        Assert.assertEquals(expect, input);
    }

    @Test
    public void getNewPayloadWithFSURL_TrackPayload_ReturnsTrackPayload() {
        FullStorySegmentMiddleware fullStorySegmentMiddleware = new FullStorySegmentMiddleware(mockContext, "SegmentWriteMockKey");
        Map<String, Object> context = new HashMap<>();
        TrackPayload input = new TrackPayload.Builder().userId("userId").event("event").build();
        BasePayload output = fullStorySegmentMiddleware.getNewPayloadWithFSURL(input,context);

        Assert.assertEquals(output.type(), BasePayload.Type.track);
    }

    @Test
    public void getNewPayloadWithFSURL_TrackPayload_ReturnsTrackPayloadWithFSURL() {
        FullStorySegmentMiddleware fullStorySegmentMiddleware = new FullStorySegmentMiddleware(mockContext, "SegmentWriteMockKey");
        Map<String, Object> context = new HashMap<>();
        TrackPayload input = new TrackPayload.Builder().userId("userId").event("event").build();
        TrackPayload output = (TrackPayload) fullStorySegmentMiddleware.getNewPayloadWithFSURL(input,context);

        Properties expect = new Properties();
        expect.put("fullstoryUrl", FS.getCurrentSessionURL());

        Assert.assertEquals(output.properties(), expect);
    }


    @Test
    public void getNewPayloadWithFSURL_ScreenPayload_ReturnsScreenPayload() {
        FullStorySegmentMiddleware fullStorySegmentMiddleware = new FullStorySegmentMiddleware(mockContext, "SegmentWriteMockKey");
        Map<String, Object> context = new HashMap<>();
        ScreenPayload input = new ScreenPayload.Builder().userId("userId").name("MainActivity").build();
        BasePayload output = fullStorySegmentMiddleware.getNewPayloadWithFSURL(input,context);

        Assert.assertEquals(output.type(), BasePayload.Type.screen);
    }

    @Test
    public void getNewPayloadWithFSURL_ScreenPayload_ReturnsScreenPayloadWithFSURL() {
        FullStorySegmentMiddleware fullStorySegmentMiddleware = new FullStorySegmentMiddleware(mockContext, "SegmentWriteMockKey");
        Map<String, Object> context = new HashMap<>();
        ScreenPayload input = new ScreenPayload.Builder().userId("userId").name("MainActivity").build();
        ScreenPayload output = (ScreenPayload) fullStorySegmentMiddleware.getNewPayloadWithFSURL(input,context);

        Properties expect = new Properties();
        expect.put("fullstoryUrl", FS.getCurrentSessionURL());

        Assert.assertEquals(output.properties(), expect);
    }
}
