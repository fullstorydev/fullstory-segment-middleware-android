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

    FullStorySegmentMiddleware fullStorySegmentMiddleware;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        when(mockContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mockPrefs);

        PowerMockito.mockStatic(FS.class);
        when(FS.getCurrentSessionURL(anyBoolean())).thenReturn("mock FullStory Session URL");
        when(FS.getCurrentSessionURL()).thenReturn("mock FullStory Session URL");
    }

    @Test
    public void constructor_WithAllowListedEvent() {
        ArrayList<String> allowList = new ArrayList<>();
        allowList.add("Product Added");
        fullStorySegmentMiddleware = new FullStorySegmentMiddleware(mockContext, "SegmentWriteMockKey", allowList);
        Assert.assertEquals(allowList, fullStorySegmentMiddleware.allowlistedEvents);
        // TODO: assert logging for SharedPreferences listener code
    }

    @Test
    public void constructor_NoAllowListedEvent() {
        fullStorySegmentMiddleware = new FullStorySegmentMiddleware(mockContext, "SegmentWriteMockKey");
        Assert.assertTrue(fullStorySegmentMiddleware.enableFSSessionURLInEvents);
        Assert.assertFalse(fullStorySegmentMiddleware.enableGroupTraitsAsUserVars);
        Assert.assertFalse(fullStorySegmentMiddleware.enableSendScreenAsEvents);
        Assert.assertFalse(fullStorySegmentMiddleware.allowlistAllTrackEvents);
        Assert.assertTrue(fullStorySegmentMiddleware.allowlistedEvents.isEmpty());
        // TODO: assert logging for null allowlist warning
    }

    @Test
    public void intercept_GroupPayloadChain_DisableGroupTraitsAsUserVars_ChainProceedCalled() {
        fullStorySegmentMiddleware = new FullStorySegmentMiddleware(mockContext, "SegmentWriteMockKey");
        fullStorySegmentMiddleware.enableGroupTraitsAsUserVars = false;
        fullStorySegmentMiddleware.enableFSSessionURLInEvents = false;
        GroupPayload groupPayload = new GroupPayload.Builder().userId("userId").groupId("groupId").build();
        when(mockChain.payload()).thenReturn(groupPayload);
        fullStorySegmentMiddleware.intercept(mockChain);

        // chain.proceed is called with the correct payload
        verify(mockChain, times(1)).proceed(groupPayload);
    }

    @Test
    public void intercept_GroupPayloadChain_DisableGroupTraitsAsUserVars_FSSetUserVarsCalled() {
        fullStorySegmentMiddleware = new FullStorySegmentMiddleware(mockContext, "SegmentWriteMockKey");
        fullStorySegmentMiddleware.enableGroupTraitsAsUserVars = false;
        fullStorySegmentMiddleware.enableFSSessionURLInEvents = false;
        GroupPayload groupPayload = new GroupPayload.Builder().userId("userId").groupId("groupId").build();
        when(mockChain.payload()).thenReturn(groupPayload);
        fullStorySegmentMiddleware.intercept(mockChain);

        // FS.event is called with properties
        Map<String, String> userVars = new HashMap<>();
        userVars.put("groupID_str", groupPayload.groupId());
        verifyStatic(FS.class, VerificationModeFactory.times(1));
        FS.setUserVars(userVars);
    }

    @Test
    public void intercept_GroupPayloadChain_EnableGroupTraitsAsUserVars_ChainProceedCalled() {
        fullStorySegmentMiddleware = new FullStorySegmentMiddleware(mockContext, "SegmentWriteMockKey");
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

        // chain.proceed is called with the correct payload
        verify(mockChain, times(1)).proceed(groupPayload);
    }

    @Test
    public void intercept_GroupPayloadChain_EnableGroupTraitsAsUserVars_FSSetUserVarsCalled() {
        fullStorySegmentMiddleware = new FullStorySegmentMiddleware(mockContext, "SegmentWriteMockKey");
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

        // FS.event is called with properties
        Map<String, String> suffixedMap = new HashMap<>();
        suffixedMap.put("groupID_str", groupPayload.groupId());
        suffixedMap.put("industry_str", "retail");
        verifyStatic(FS.class, VerificationModeFactory.times(1));
        FS.setUserVars(suffixedMap);
    }

    @Test
    public void intercept_IdentifyPayloadChain_NoTraits_FSIdentifyCalled() {
        fullStorySegmentMiddleware = new FullStorySegmentMiddleware(mockContext, "SegmentWriteMockKey");
        fullStorySegmentMiddleware.enableFSSessionURLInEvents = false;

        IdentifyPayload identifyPayload = new IdentifyPayload.Builder()
                .userId("userId")
                .build();
        when(mockChain.payload()).thenReturn(identifyPayload);
        fullStorySegmentMiddleware.intercept(mockChain);

        // FS.event is called with properties
        verifyStatic(FS.class, VerificationModeFactory.times(1));
        FS.identify(identifyPayload.userId(), new HashMap<>());
    }

    @Test
    public void intercept_IdentifykPayloadChain_UserTraits_ChainProceedCalled() {
        fullStorySegmentMiddleware = new FullStorySegmentMiddleware(mockContext, "SegmentWriteMockKey");
        fullStorySegmentMiddleware.enableFSSessionURLInEvents = false;

        IdentifyPayload identifyPayload = new IdentifyPayload.Builder()
                .userId("userId")
                .build();
        when(mockChain.payload()).thenReturn(identifyPayload);
        fullStorySegmentMiddleware.intercept(mockChain);

        // chain.proceed is called with the correct payload
        verify(mockChain, times(1)).proceed(identifyPayload);
    }

    @Test
    public void intercept_ScreenPayloadChain_EnableSendScreenAsEvents_FSEventCalled() {
        fullStorySegmentMiddleware = new FullStorySegmentMiddleware(mockContext, "SegmentWriteMockKey");
        fullStorySegmentMiddleware.enableSendScreenAsEvents = true;
        fullStorySegmentMiddleware.enableFSSessionURLInEvents = false;

        ScreenPayload screenPayload = new ScreenPayload.Builder()
                .userId("userId")
                .name("MainActivity")
                .build();
        when(mockChain.payload()).thenReturn(screenPayload);
        fullStorySegmentMiddleware.intercept(mockChain);

        // FS.event is called with properties
        verifyStatic(FS.class, VerificationModeFactory.times(1));
        FS.event("Segment Screen: " + screenPayload.name(), screenPayload.properties());
    }

    @Test
    public void intercept_ScreenPayloadChain_EnableSendScreenAsEvents_ChainProceedCalled() {
        fullStorySegmentMiddleware = new FullStorySegmentMiddleware(mockContext, "SegmentWriteMockKey");
        fullStorySegmentMiddleware.enableSendScreenAsEvents = true;
        fullStorySegmentMiddleware.enableFSSessionURLInEvents = false;

        ScreenPayload screenPayload = new ScreenPayload.Builder()
                .userId("userId")
                .name("MainActivity")
                .build();
        when(mockChain.payload()).thenReturn(screenPayload);
        fullStorySegmentMiddleware.intercept(mockChain);

        // chain.proceed is called with the correct payload
        verify(mockChain, times(1)).proceed(screenPayload);
    }

    @Test
    public void intercept_ScreenPayloadChain_EnableSendScreenAsEvents_FSEventNotCalled() {
        fullStorySegmentMiddleware = new FullStorySegmentMiddleware(mockContext, "SegmentWriteMockKey");
        fullStorySegmentMiddleware.enableSendScreenAsEvents = false;
        fullStorySegmentMiddleware.enableFSSessionURLInEvents = false;

        ScreenPayload screenPayload = new ScreenPayload.Builder()
                .userId("userId")
                .name("MainActivity")
                .build();
        when(mockChain.payload()).thenReturn(screenPayload);
        fullStorySegmentMiddleware.intercept(mockChain);

        // FS.event is called with properties
        verifyStatic(FS.class, VerificationModeFactory.times(0));
        FS.event(any(), any());
    }

    @Test
    public void intercept_ScreenPayloadChain_DisableSendScreenAsEvents_ChainProceedCalled() {
        fullStorySegmentMiddleware = new FullStorySegmentMiddleware(mockContext, "SegmentWriteMockKey");
        fullStorySegmentMiddleware.enableSendScreenAsEvents = false;
        fullStorySegmentMiddleware.enableFSSessionURLInEvents = false;

        ScreenPayload screenPayload = new ScreenPayload.Builder()
                .userId("userId")
                .name("MainActivity")
                .build();
        when(mockChain.payload()).thenReturn(screenPayload);
        fullStorySegmentMiddleware.intercept(mockChain);

        // chain.proceed is called with the correct payload
        verify(mockChain, times(1)).proceed(screenPayload);
    }

    @Test
    public void intercept_TrackPayloadChain_DisallowlistAllTrackEvents_NoAllowlistedEvents_FSEventNotCalled() {
        fullStorySegmentMiddleware = new FullStorySegmentMiddleware(mockContext, "SegmentWriteMockKey");
        fullStorySegmentMiddleware.allowlistAllTrackEvents = false;
        fullStorySegmentMiddleware.enableFSSessionURLInEvents = false;

        TrackPayload trackPayload = new TrackPayload.Builder().userId("userId").event("event").build();
        when(mockChain.payload()).thenReturn(trackPayload);
        fullStorySegmentMiddleware.intercept(mockChain);

        // FS.event is called with properties
        verifyStatic(FS.class, VerificationModeFactory.times(0));
        FS.event(any(),any());
    }

    @Test
    public void intercept_TrackPayloadChain_DisllowlistAllTrackEvents_NoAllowlistedEvents_ChainProceedCalled() {
        fullStorySegmentMiddleware = new FullStorySegmentMiddleware(mockContext, "SegmentWriteMockKey");
        fullStorySegmentMiddleware.allowlistAllTrackEvents = false;
        fullStorySegmentMiddleware.enableFSSessionURLInEvents = false;

        ScreenPayload screenPayload = new ScreenPayload.Builder()
                .userId("userId")
                .name("MainActivity")
                .build();
        when(mockChain.payload()).thenReturn(screenPayload);
        fullStorySegmentMiddleware.intercept(mockChain);

        // chain.proceed is called with the correct payload
        verify(mockChain, times(1)).proceed(screenPayload);
    }

    @Test
    public void intercept_TrackPayloadChain_DisallowlistAllTrackEvents_EventAllowlisted_FSEventCalled() {
        ArrayList<String> allowList = new ArrayList<>();
        allowList.add("Product Added");
        fullStorySegmentMiddleware = new FullStorySegmentMiddleware(mockContext, "SegmentWriteMockKey", allowList);
        fullStorySegmentMiddleware.allowlistAllTrackEvents = false;
        fullStorySegmentMiddleware.enableFSSessionURLInEvents = false;

        TrackPayload trackPayload = new TrackPayload.Builder().userId("userId").event("Product Added").build();
        when(mockChain.payload()).thenReturn(trackPayload);
        fullStorySegmentMiddleware.intercept(mockChain);

        // FS.event is called with properties
        verifyStatic(FS.class, VerificationModeFactory.times(1));
        FS.event(trackPayload.event(), trackPayload.properties());
    }

    @Test
    public void intercept_TrackPayloadChain_DisllowlistAllTrackEvents_AllowlistedEvents_ChainProceedCalled() {
        ArrayList<String> allowList = new ArrayList<>();
        allowList.add("Product Added");
        fullStorySegmentMiddleware = new FullStorySegmentMiddleware(mockContext, "SegmentWriteMockKey", allowList);
        fullStorySegmentMiddleware.allowlistAllTrackEvents = false;
        fullStorySegmentMiddleware.enableFSSessionURLInEvents = false;

        TrackPayload trackPayload = new TrackPayload.Builder().userId("userId").event("Product Added").build();
        when(mockChain.payload()).thenReturn(trackPayload);
        fullStorySegmentMiddleware.intercept(mockChain);

        // chain.proceed is called with the correct payload
        verify(mockChain, times(1)).proceed(trackPayload);
    }

    @Test
    public void intercept_TrackPayloadChain_AllowlistAllTrackEvents_FSEventCalled() {
        fullStorySegmentMiddleware = new FullStorySegmentMiddleware(mockContext, "SegmentWriteMockKey");
        fullStorySegmentMiddleware.allowlistAllTrackEvents = true;
        fullStorySegmentMiddleware.enableFSSessionURLInEvents = false;
        TrackPayload trackPayload = new TrackPayload.Builder().userId("userId").event("event").build();
        when(mockChain.payload()).thenReturn(trackPayload);
        fullStorySegmentMiddleware.intercept(mockChain);

        // FS.event is called with properties
        verifyStatic(FS.class, VerificationModeFactory.times(1));
        FS.event(trackPayload.event(), trackPayload.properties());
    }

    @Test
    public void intercept_TrackPayloadChain_AllowlistAllTrackEvents_ChainProceedCalled() {
        fullStorySegmentMiddleware = new FullStorySegmentMiddleware(mockContext, "SegmentWriteMockKey");
        fullStorySegmentMiddleware.allowlistAllTrackEvents = true;
        fullStorySegmentMiddleware.enableFSSessionURLInEvents = false;

        TrackPayload trackPayload = new TrackPayload.Builder().userId("userId").event("event").build();
        when(mockChain.payload()).thenReturn(trackPayload);
        fullStorySegmentMiddleware.intercept(mockChain);

        // chain.proceed is called with the correct payload
        verify(mockChain, times(1)).proceed(trackPayload);
    }

    @Test
    public void intercept_AliasPayload_DefaultCase() {
        fullStorySegmentMiddleware = new FullStorySegmentMiddleware(mockContext, "SegmentWriteMockKey");
        fullStorySegmentMiddleware.allowlistAllTrackEvents = true;
        fullStorySegmentMiddleware.enableFSSessionURLInEvents = false;

        AliasPayload aliasPayload = new AliasPayload.Builder().previousId("previousId").userId("userId").build();
        when(mockChain.payload()).thenReturn(aliasPayload);
        fullStorySegmentMiddleware.intercept(mockChain);

        // chain.proceed is called with the correct payload
        verify(mockChain, times(1)).proceed(aliasPayload);
    }

    @Test
    public void intercept_EnableFSSessionURLInEvents_VerifyChainProceed() {
        fullStorySegmentMiddleware = new FullStorySegmentMiddleware(mockContext, "SegmentWriteMockKey");
        fullStorySegmentMiddleware.enableFSSessionURLInEvents = true;

        TrackPayload inputTrackPayload = new TrackPayload.Builder().userId("userId").event("event").build();
        when(mockChain.payload()).thenReturn(inputTrackPayload);
        fullStorySegmentMiddleware.intercept(mockChain);

        // chain.proceed is called with the correct payload
        Map<String, Object> newContext = new HashMap<>(inputTrackPayload.context());
        newContext.put("fullstoryUrl", FS.getCurrentSessionURL());
        Map<String, Object> newProperties = new HashMap<>(inputTrackPayload.properties());
        newProperties.put("fullstoryUrl", FS.getCurrentSessionURL());

        TrackPayload newTrackPayload = inputTrackPayload.toBuilder()
                .context(newContext)
                .properties(newProperties)
                .build();

        verify(mockChain, times(1)).proceed(newTrackPayload);
    }

    @Test
    public void addFSUrlToContext_AddsURLToContext() {
        fullStorySegmentMiddleware = new FullStorySegmentMiddleware(mockContext, "SegmentWriteMockKey");
        Map<String, Object> input = new HashMap<>();
        fullStorySegmentMiddleware.addFSUrlToContext(input);

        Map<String, Object> expect = new HashMap<>();
        expect.put("fullstoryUrl", FS.getCurrentSessionURL());
        Assert.assertEquals(expect, input);
    }

    @Test
    public void getNewPayloadWithFSURL_TrackPayload_ReturnsTrackPayload() {
        fullStorySegmentMiddleware = new FullStorySegmentMiddleware(mockContext, "SegmentWriteMockKey");
        Map<String, Object> context = new HashMap<>();
        TrackPayload input = new TrackPayload.Builder().userId("userId").event("event").build();
        BasePayload output = fullStorySegmentMiddleware.getNewPayloadWithFSURL(input,context);

        Assert.assertEquals(output.type(), BasePayload.Type.track);
    }

    @Test
    public void getNewPayloadWithFSURL_TrackPayload_ReturnsTrackPayloadWithFSURL() {
        fullStorySegmentMiddleware = new FullStorySegmentMiddleware(mockContext, "SegmentWriteMockKey");
        Map<String, Object> context = new HashMap<>();
        TrackPayload input = new TrackPayload.Builder().userId("userId").event("event").build();
        TrackPayload output = (TrackPayload) fullStorySegmentMiddleware.getNewPayloadWithFSURL(input,context);

        Properties expect = new Properties();
        expect.put("fullstoryUrl", FS.getCurrentSessionURL());
        Assert.assertEquals(output.properties(), expect);
    }


    @Test
    public void getNewPayloadWithFSURL_ScreenPayload_ReturnsScreenPayload() {
        fullStorySegmentMiddleware = new FullStorySegmentMiddleware(mockContext, "SegmentWriteMockKey");
        Map<String, Object> context = new HashMap<>();
        ScreenPayload input = new ScreenPayload.Builder().userId("userId").name("MainActivity").build();
        BasePayload output = fullStorySegmentMiddleware.getNewPayloadWithFSURL(input,context);

        Assert.assertEquals(output.type(), BasePayload.Type.screen);
    }

    @Test
    public void getNewPayloadWithFSURL_ScreenPayload_ReturnsScreenPayloadWithFSURL() {
        fullStorySegmentMiddleware = new FullStorySegmentMiddleware(mockContext, "SegmentWriteMockKey");
        Map<String, Object> context = new HashMap<>();
        ScreenPayload input = new ScreenPayload.Builder().userId("userId").name("MainActivity").build();
        ScreenPayload output = (ScreenPayload) fullStorySegmentMiddleware.getNewPayloadWithFSURL(input,context);

        Properties expect = new Properties();
        expect.put("fullstoryUrl", FS.getCurrentSessionURL());
        Assert.assertEquals(output.properties(), expect);
    }
}
