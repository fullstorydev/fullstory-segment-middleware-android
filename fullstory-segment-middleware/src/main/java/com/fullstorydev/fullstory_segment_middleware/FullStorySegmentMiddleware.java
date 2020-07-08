package com.fullstorydev.fullstory_segment_middleware;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

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

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class FullStorySegmentMiddleware implements Middleware {

    public boolean enableGroupTraitsAsUserVars = false;
    public boolean enableFSSessionURLInEvents = true;
    public boolean enableSendScreenAsEvents = false;
    public boolean allowlistAllTrackEvents = false;
    private ArrayList<String> allowlistedEvents;


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
                userVars.put("groupID", groupPayload.groupId());
                // optionally enable group traits to be passed into user vars
                if (enableGroupTraitsAsUserVars && !isNullOrEmpty(groupPayload.traits())) {
                    userVars.putAll(groupPayload.traits());
                }
                FS.setUserVars(userVars);
                break;

            case identify:
                IdentifyPayload identifyPayload = (IdentifyPayload) payload;
                FS.identify(identifyPayload.userId(), identifyPayload.traits());
                break;

            case screen:
                ScreenPayload screenPayload = (ScreenPayload) payload;
                if (this.enableSendScreenAsEvents) {
                    FS.event("Segment Screen: " + screenPayload.name(), screenPayload.properties());
                }
                break;

            case track:
                TrackPayload trackPayload = (TrackPayload) payload;
                if (this.allowlistAllTrackEvents || this.allowlistedEvents.indexOf(trackPayload.event()) != -1) {
                    Map<String, Object> props = getSuffixedProps(trackPayload.properties());
                    FS.event(trackPayload.event(), props);
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
            newPayload = getNewPayloadWithFSURL(payload, context);
        }

        if (newPayload == null) newPayload = payload.toBuilder().context(context).build();
        chain.proceed(newPayload);
    }

    private BasePayload getNewPayloadWithFSURL(BasePayload payload, Map<String, Object> context) {
        // properties obj is immutable so we need to create a new one
        ValueMap properties = payload.getValueMap("properties");
        Map<String, Object> fullStoryProperties = new HashMap<>();
        if (!isNullOrEmpty(properties)) fullStoryProperties.putAll(properties);

        fullStoryProperties.put("fullstoryUrl", FS.getCurrentSessionURL());
        // now URL API available post FullStory plugin v1.3.0
        // fullStoryProperties.put("fullstoryNowUrl", FS.getCurrentSessionURL(true));
        context.put("fullstoryUrl", FS.getCurrentSessionURL());

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

    private Map<String, Object> getSuffixedProps (Map<String, Object> props) {
        // transform props to comply with FS custom events requirement
        // TODO: Segment should not allow with circular dependency, but we should check anyway
        Map<String, Object> mutableProps = new HashMap<>();
        Stack<Map<String,Object>> stack = new Stack<>();

        stack.push(props);

        while (!stack.empty()) {
            Map<String,Object> map = stack.pop();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String key = entry.getKey();
                Object item = entry.getValue();

                // if item is a nested map, concatenate keys and push back to stack
                // Example - input: {root: {key: val}}, output: {root.key: val}
                if (item instanceof Map) {
                    // we are unsure of the child map's type, cast to wildcard and then parse
                    Map<?,?> itemMap = (Map<?,?>) item;
                    for (Map.Entry<?, ?> e : itemMap.entrySet()) {
                        String concatenatedKey =  key + '.' + e.getKey();
                        Map<String,Object> m = new HashMap<>();
                        m.put(concatenatedKey, e.getValue());
                        stack.push(m);
                    }
                } else if (item instanceof Iterable) {
                    for (Object obj: (Iterable<?>) item) {
                        Map<String,Object> m = new HashMap<>();
                        m.put(key, obj);
                        stack.push(m);
                    }
                } else if (item != null && item.getClass().isArray()) {
                    // if it's of array type (but not iterable)
                    // To comply with FS requirements, flatten the array of objects into a map:
                    // each item in array becomes a map, with key, and item
                    // enable search value the array in FS (i.e. searching for one product when array of products are sent)
                    // then push each item with the same key back to stack
                    Object[] objs = this.getArrayObjectFromArray(item);
                    for (Object obj: objs) {
                        Map<String,Object> m = new HashMap<>();
                        m.put(key, obj);
                        stack.push(m);
                    }
                } else {
                    // if this is not a map or array, simply treat as a "primitive" value and send them as-is
                    String suffix = this.getSuffixStringFromSimpleObject(item);
                    this.addSimpleObjectToMap(mutableProps, key + suffix, item);
                }
            }
        }
        pluralizeAllArrayKeysInMap(mutableProps);
        return mutableProps;
    }

    private void addSimpleObjectToMap (Map<String, Object> map, String key, Object obj) {
        // add one obj into the result map, check if the key with suffix already exists, if so add to the result arrays.
        // key is already suffixed, and always in singular form
        Object item = map.get(key);
        // if the same key already exist, check if plural key is already in the map
        if (item != null) {
            // concatenate array and replace item with new ArrayList
            ArrayList<Object> arr = new ArrayList<>();
            arr.add(obj);
            if (item instanceof Collection) {
                arr.addAll((Collection<?>) item);
            } else {
                arr.add(item);
            }
            map.put(key, arr);
        } else {
            map.put(key, obj);
        }
    }

    private String getSuffixStringFromSimpleObject(@NonNull Object item) {
        // default to no suffix;
        String suffix = "";
        if ( item instanceof String || item instanceof Character) {
            suffix = "_str";
        } else if ( item instanceof Number ) {
            // default to real
            suffix = "_real";
            if ( item instanceof Integer || item instanceof BigInteger || item instanceof Long || item instanceof Short) {
                suffix = "_int";
            }
        } else if (item instanceof Boolean) {
            suffix = "_bool";
        } else if (item instanceof Date) {
            suffix = "_date";
        }

        return suffix;
    }

    private Object[] getArrayObjectFromArray(Object arr) {
        if (arr instanceof Object[]) return (Object[]) arr;

        int len = Array.getLength(arr);
        Object[] resultArr = new Object[len];
        for (int i = 0; i < len; ++i) {
            resultArr[i] = Array.get(arr, i);
        }
        return resultArr;
    }

    private void pluralizeAllArrayKeysInMap(Map<String,Object> map) {
        Set<String> keySet = new HashSet<>(map.keySet());
        for (String key :keySet) {
            if (map.get(key) instanceof Collection) {
                map.put(key + 's', map.get(key));
                map.remove(key);
            }
        }
    }
}