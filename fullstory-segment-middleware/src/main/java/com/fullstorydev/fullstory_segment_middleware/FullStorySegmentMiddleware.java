package com.fullstorydev.fullstory_segment_middleware;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.fullstory.FS;
import com.segment.analytics.Middleware;
import com.segment.analytics.ValueMap;
import com.segment.analytics.integrations.BasePayload;
import com.segment.analytics.integrations.GroupPayload;
import com.segment.analytics.integrations.IdentifyPayload;
import com.segment.analytics.integrations.ScreenPayload;
import com.segment.analytics.integrations.TrackPayload;

import static com.segment.analytics.internal.Utils.getSegmentSharedPreferences;
import static com.segment.analytics.internal.Utils.isNullOrEmpty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class FullStorySegmentMiddleware implements Middleware {

    public boolean enableGroupTraitsAsUserVars = false;
    public boolean enableFSSessionURLInEvents = true;
    public boolean enableSendScreenAsEvents = false;
    public boolean allowlistAllTrackEvents = false;
    ArrayList<String> allowlistedEvents;


    private final String TAG = "FullStoryMiddleware";

    /**
     * Initialize FullStory middleware with application context, segmentTag, and allowlisted events.
     * This middleware will enable Segment APIs to be passed into FullStory session replay, for more information go to: https://help.fullstory.com/hc/en-us/sections/360007387073-Native-Mobile
     *
     * @param context         Application context that's passed in to Segment Analytics builder
     * @param segmentTag      Segment tag if not set this should be the same as segment write key
     * @param allowlistEvents allowlist any events that you would like to be passed to FullStory automatically
     */
    public FullStorySegmentMiddleware(Context context, String segmentTag, ArrayList<String> allowlistEvents) {
        this.allowlistedEvents = allowlistEvents;

        // Analytics reset does not use middleware, so we need to listen to when the userID becomes null in shared preference and call anonymize to logout the user properly
        SharedPreferences sharedPreferences = getSegmentSharedPreferences(context, segmentTag);
        if (isNullOrEmpty(sharedPreferences.getAll())) {
            Log.w(TAG, "Unable to find Segment preferences, can not anonymize user when Analytics.reset() is called!");
        }
        SharedPreferences.OnSharedPreferenceChangeListener listener = (sharedPreferences1, key) -> {
            if (key.equals("traits-" + segmentTag) && sharedPreferences1.getString("userId", null) == null) {
                Log.d(TAG, "Segment User ID has become null, calling FS.anonymize");
                FS.anonymize();
            }
        };
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener);
    }

    /**
     * Initialize FullStory middle ware with application context, segmentTag.
     * With no allowlisted events, no Segment track events will be passed to FullStory session replay.
     * For more information go to: https://help.fullstory.com/hc/en-us/sections/360007387073-Native-Mobile
     *
     * @param context    Application context that's passed in to Segment Analytics builder
     * @param segmentTag Segment tag if not set this should be the same as segment write key
     */
    public FullStorySegmentMiddleware(Context context, String segmentTag) {
        this(context, segmentTag, new ArrayList<>());
        Log.i(TAG, "no events allowlisted, unless enabled: allowlistAllTrackEvents, Segment track events will not be forward to FullStory!");
    }

    @Override
    public void intercept(Chain chain) {
        if (chain == null) return;
        // Get the original payload from chain
        BasePayload payload = chain.payload();

        // create a place holder payload to be created
        BasePayload newPayload = null;

        // available payloads:
        // https://github.com/segmentio/analytics-android/tree/master/analytics/src/main/java/com/segment/analytics/integrations
        switch (payload.type()) {
            case group:
                GroupPayload groupPayload = (GroupPayload) payload;

                // User will always be tied to the newest groupID that you set, group traits will not be included by default!
                // this is because if the user changes it's group which no longer has certain traits, we will not detect when to clear them in FS and user will be tied to old group data
                HashMap<String, Object> userVars = new HashMap<>();
                userVars.put("groupID_str", groupPayload.groupId());
                // optionally enable group traits to be passed into user vars
                if (enableGroupTraitsAsUserVars && !isNullOrEmpty(groupPayload.traits())) {
                    FSSuffixedProperties traits = new FSSuffixedProperties(groupPayload.traits());
                    userVars.putAll(traits.getSuffixedProperties());
                }
                FS.setUserVars(userVars);
                break;

            case identify:
                IdentifyPayload identifyPayload = (IdentifyPayload) payload;
                FSSuffixedProperties traits = new FSSuffixedProperties(identifyPayload.traits());
                FS.identify(identifyPayload.userId(), traits.getSuffixedProperties());
                break;

            case screen:
                ScreenPayload screenPayload = (ScreenPayload) payload;
                if (this.enableSendScreenAsEvents) {
                    FSSuffixedProperties props = new FSSuffixedProperties(screenPayload.properties());
                    FS.event("Segment Screen: " + screenPayload.name(), props.getSuffixedProperties());
                }
                break;

            case track:
                TrackPayload trackPayload = (TrackPayload) payload;
                if (this.allowlistAllTrackEvents || this.allowlistedEvents.indexOf(trackPayload.event()) != -1) {
                    FSSuffixedProperties props = new FSSuffixedProperties(trackPayload.properties());
                    FS.event(trackPayload.event(), props.getSuffixedProperties());
                }
                break;

            default:
                FS.log(FS.LogLevel.INFO, "FullStoryMiddleware: No matching API found, not calling any FS APIs...");
                break;
        }

        // Only override the payload to insert current FullStory URL if:
        // - is Track or Screen event
        // - enableFSSessionURLInEvents is true: enable FS session URL as part of the track/screen event properties
        // if enabled you will receive at your destination events with FS URL added
        Map<String, Object> context = new LinkedHashMap<>(payload.context());
        if (this.enableFSSessionURLInEvents) {
            addFSUrlToContext(context);
            newPayload = getNewPayloadWithFSURL(payload, context);
        }

        if (newPayload == null) newPayload = payload.toBuilder().context(context).build();
        chain.proceed(newPayload);
    }

    void addFSUrlToContext(Map<String, Object> context){
        if(context != null) {
            // now URL API available post FullStory plugin v1.3.0
            // context.put("fullstoryUrl", FS.getCurrentSessionURL(true));
            context.put("fullstoryUrl", FS.getCurrentSessionURL());
        }
    }

    BasePayload getNewPayloadWithFSURL(BasePayload payload, Map<String, Object> context) {
        if(payload == null) return null;
        // properties obj is immutable so we need to create a new one
        ValueMap properties = payload.getValueMap("properties");
        Map<String, Object> fullStoryProperties = new HashMap<>();
        if (!isNullOrEmpty(properties)) fullStoryProperties.putAll(properties);

        fullStoryProperties.put("fullstoryUrl", FS.getCurrentSessionURL());
        // now URL API available post FullStory plugin v1.3.0
        // fullStoryProperties.put("fullstoryNowUrl", FS.getCurrentSessionURL(true));

        if (payload.type() == BasePayload.Type.screen) {
            ScreenPayload screenPayload = (ScreenPayload) payload;
            ScreenPayload.Builder screenPayloadBuilder = screenPayload.toBuilder()
                    .context(context)
                    .properties(fullStoryProperties);
            return screenPayloadBuilder.build();

        } else if (payload.type() == BasePayload.Type.track) {
            TrackPayload trackPayload = (TrackPayload) payload;
            TrackPayload.Builder trackPayloadBuilder = trackPayload.toBuilder()
                    .context(context)
                    .properties(fullStoryProperties);
            return trackPayloadBuilder.build();
        }
        return null;
    }
}
