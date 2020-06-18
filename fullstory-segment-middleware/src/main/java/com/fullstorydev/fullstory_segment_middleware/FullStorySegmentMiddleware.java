package com.fullstorydev.fullstory_segment_middleware;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

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
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
                Map<String, ?> props = getSuffixedProps(trackPayload.properties());
                Log.d("final", " props: "+props);
                if (this.allowlistAllTrackEvents || this.allowlistedEvents.indexOf(trackPayload.event()) != -1) {
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
//        fullStoryProperties.put("fullstoryNowUrl", FS.getCurrentSessionURL(true));
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

    private Map<String,Object> getSuffixedProps (Map<String, Object> props){
        Map<String, Object> mutableProps = new HashMap<>();
        for (Map.Entry<String,Object> entry : props.entrySet()) {
            Object item =  entry.getValue();
            String key =  entry.getKey();

            if (item instanceof Map) {
                // We should only get String keys, parse the map to make sure the keys are all of string type, before recursively calling getSuffixedProps to parse nested maps
                Map<String, Object> tempMap = new HashMap<>();
                for (Map.Entry<?,?> e : ((Map<?,?>) item).entrySet()) {
                    if(e.getKey() instanceof String){
                        tempMap.put(String.valueOf(e.getKey()), e.getValue());
                    }
                }
                mutableProps.put(key.replaceAll("_",""), this.getSuffixedProps(tempMap));
            } else if (item.getClass().isArray()) {
                // if it's of array type (but not iterable)
                ArrayList<Object> list = new ArrayList<>(Arrays.asList(this.getArrayObjectFromArray(item)));
                Map<String, Object> tempMap = this.getMapFromIterable(list, key);
                this.appendObjectToMap(mutableProps, "", tempMap);
            } else if (item instanceof Iterable) {
                ArrayList<Object> list = new ArrayList<>();
                for(Object i: (Iterable<?>) item){
                    list.add(i);
                }
                Map<String, Object> tempMap = this.getMapFromIterable(list, key);
                this.appendObjectToMap(mutableProps, "", tempMap);
            } else {
                // if this is not a dictionary or array, then treat it like simple values, if data falls outside of these, we don't try to infer anything and just send as is to the server
                String suffix = this.getSuffixStringFromSimpleObject(item);
                this.appendObjectToMap(mutableProps, key + suffix, item);
            }

        }


        return mutableProps;
    }

    private void appendObjectToMap (Map<String, Object> map, String key, Object obj){
        // umbrella function to handle 3 possible object input, if not a dict or array then we will treat the object as "simple"
        if (obj.getClass().isArray()) {
            ArrayList<Object> list = new ArrayList<>(Arrays.asList(this.getArrayObjectFromArray(obj)));
            this.appendArrayObjectToMap(map, key, list);
        } else if (obj instanceof Iterable){
            ArrayList<Object> list = new ArrayList<>();
            for(Object i: (Iterable<?>) obj){
                list.add(i);
            }
            this.appendArrayObjectToMap(map, key, list);
        } else if (obj instanceof Map) {
            Map<String, Object> tempMap = new HashMap<>();
            for (Map.Entry<?,?> e : ((Map<?,?>) obj).entrySet()) {
//                if(e.getKey() instanceof String){
                    tempMap.put(String.valueOf(e.getKey()), e.getValue());
//                }
            }
            this.appendMapObjectToMap(map, key, tempMap);
        } else {
            this.appendSimpleObjectToMap(map, key, obj);
        }
    }

    private void appendArrayObjectToMap(Map<String, Object> map, String key, ArrayList<Object> arr) {
        for(Object obj: arr)
            this.appendObjectToMap(map, key, obj);
    }

    private void appendMapObjectToMap(Map<String, Object> map, String key, Map<String,Object> map2){
        // when adding a parsed dict into the result dict, emurate and add each object
        for (String k: map2.keySet()) {
            String nestedKey = "";
            if (key.length() > 0) {
                nestedKey = key + "." + k;
            } else {
                nestedKey = k;
            }
            this.appendObjectToMap(map, nestedKey, map2.get(k));
        }
    }

    private void appendSimpleObjectToMap (Map<String, Object> map, String key, Object obj) {
        // obj is simple, add it into the result dict, check if the key with suffix already exsist, if so then we need to append to the result arrays insead of replacing the object.
        // this creates a mutable array each time an object is added, maybe we should consider making arrays mutable in the dict
        // key is already suffixed, we just need to check if it ends with 's' to know if it's plural
        boolean isPlural = key.endsWith("s");
        String pluralKey = key + "s";
        Object item = map.get(key);
        Object items = map.get(pluralKey);
        // if there is already a key in singular or plural form in the dict, remove exsisting key and concatnating all values and add a new plural key (flatten the arrays)
        if (isPlural) {
            String singularKey = key.substring(0, key.length() - 1);
            ArrayList<Object> arr =  new ArrayList<>();
            // alwasy use ArrayList when pushing to map
            if(item instanceof ArrayList){
                arr.addAll((Collection<?>) item);
            }
            if (map.containsKey(singularKey)){
                arr.add(map.get(singularKey));
            }
            arr.add(obj);
            map.remove(singularKey);
            map.put(key, arr);
        } else if(item != null || items != null){
            // if key exist but not plural
            ArrayList<Object> arr =  new ArrayList<>();
            arr.add(obj);
            arr.add(item);
            if(items != null) arr.add(items);
            map.remove(key);
            map.put(pluralKey, arr);
        } else {
            map.put(key, obj);
        }
    }

    private String getSuffixStringFromSimpleObject(Object item) {
        // default to no suffix;
        String suffix = "";
        if ( item instanceof String ) {
            suffix = "_str";
        } else if ( item instanceof Number ) {
            // defaut to real
            suffix = "_real";
            if ( item instanceof Integer || item instanceof BigInteger) {
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
        for(int i = 0; i < len; ++i){
            resultArr[i] = Array.get(arr, i);
        }
        return resultArr;
    }

    private Map<String, Object> getMapFromIterable(Iterable<Object> arr, String key) {
        Map<String, Object> resultMap = new HashMap<>();

        for (Object item : arr) {
            if (item instanceof Map) {
                // if array of maps, we then loop through all maps, flatten out each key/val into arrays, we will loose the object association but it allows user to search for each key/val in the array in FS (i.e. searching for one product when array of products are sent)
                Map<String, Object> tempMap = this.getSuffixedProps((Map<String, Object>) item);
                this.appendObjectToMap(resultMap, key, tempMap);
            } else if (item.getClass().isArray()) {
                // TODO: Segment spec should not allow nested array properties, ignore for now, but we should handle it eventually
                ArrayList<Object> list = new ArrayList<>(Arrays.asList(this.getArrayObjectFromArray(item)));
                Map<String, Object> tempMap = this.getMapFromIterable(list, key);
                this.appendObjectToMap(resultMap, "", tempMap);
            } else if(item instanceof Iterable){
                Map<String, Object> tempMap =  this.getMapFromIterable((Iterable<Object>)item, key);
                this.appendObjectToMap(resultMap, "", tempMap);
            } else {
                // default to simple object
                // if there are arrays of mixed type, then in the final props we will add approporate values to the same, key but with each type suffix
                String suffix = this.getSuffixStringFromSimpleObject(item) + "s";
                // get the current array form this specified suffix, if any, then append current item
                ArrayList<Object> list = new ArrayList<>();
                if(resultMap.get(key) != null) list.add(resultMap.get(key));
                list.add(item);
                this.appendObjectToMap(resultMap,key + suffix,list);

            }

        }
        return resultMap;
    }
}
