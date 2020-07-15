package com.fullstorydev.fullstory_segment_middleware;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class FSSuffixedProperties {

    Map<String, Object> suffixedProperties;

    FSSuffixedProperties(Map<String, Object> props){
        // transform props to comply with FS custom events requirement
        // TODO: Segment should not allow with circular dependency, but we should check anyway
        this.suffixedProperties = new HashMap<>();
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
                    this.addSimpleObject(key + suffix, item);
                }
            }
        }
        pluralizeAllArrayKeys();
    }

    void addSimpleObject (String key, Object obj) {
        // add one obj into the result map, check if the key with suffix already exists, if so add to the result arrays.
        // key is already suffixed, and always in singular form
        Object item = this.suffixedProperties.get(key);
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
            this.suffixedProperties.put(key, arr);
        } else {
            this.suffixedProperties.put(key, obj);
        }
    }

    String getSuffixStringFromSimpleObject(Object item) {
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

    Object[] getArrayObjectFromArray(Object arr) {
        if (arr instanceof Object[]) return (Object[]) arr;

        int len = Array.getLength(arr);
        Object[] resultArr = new Object[len];
        for (int i = 0; i < len; ++i) {
            resultArr[i] = Array.get(arr, i);
        }
        return resultArr;
    }

    void pluralizeAllArrayKeys() {
        Set<String> keySet = new HashSet<>(this.suffixedProperties.keySet());
        for (String key :keySet) {
            if (this.suffixedProperties.get(key) instanceof Collection) {
                this.suffixedProperties.put(key + 's', this.suffixedProperties.get(key));
                this.suffixedProperties.remove(key);
            }
        }
    }

    public Map<String, Object> getSuffixedProperties(){
        return this.suffixedProperties;
    }
}
