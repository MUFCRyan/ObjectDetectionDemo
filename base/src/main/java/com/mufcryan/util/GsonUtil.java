package com.mufcryan.util;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

public class GsonUtil {


    private static Gson gson;

    static {
        gson = new Gson();
    }


    private GsonUtil() {
    }


    /**
     * 将object对象转成json字符串
     *
     * @param object
     * @return
     */
    public static String getJsonString(Object object) {
        if(object==null){
            return null;
        }
        String gsonString = null;
        try {
            gsonString = gson.toJson(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return gsonString;
    }


    /**
     * 将gsonString转成泛型bean
     *
     * @param gsonString
     * @param cls
     * @return
     */
    public static <T> T parseObject(String gsonString, Class<?> cls) {
        T t = null;
        try {
            t = (T) gson.fromJson(gsonString, cls);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }

    public static <T> T getFromObj(Class<?> cls, Object obj) {
        if (obj != null) {
            try {
                String str = gson.toJson(obj);
                Object bean = gson.fromJson(str, cls);
                if (bean != null) {
                    return (T) bean;
                }
            } catch (Exception var5) {
                var5.printStackTrace();
            }
            return null;
        } else {
            return null;
        }
    }
    public static <T> T getFromJsonElement(Class<?> cls, JsonElement obj) {
        if (obj != null) {
            try {
                Object bean = gson.fromJson(obj, cls);
                if (bean != null) {
                    return (T) bean;
                }
            } catch (Exception var5) {
                var5.printStackTrace();
            }
            return null;
        } else {
            return null;
        }
    }
    /**
     * 转成list
     * 泛型在编译期类型被擦除导致报错
     *
     * @param gsonString
     * @return
     */
    public static <T> List<T> gsonToList(String gsonString) {
        List<T> list = new ArrayList<>();
        try {
            list = gson.fromJson(gsonString, new TypeToken<ArrayList<T>>() {
            }.getType());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public static <T> List<T> gsonToList(String gsonString, Class<T> t) {
        List<T> list = new ArrayList<>();
        try {
            JsonParser parser = new JsonParser();
            JsonArray jsonarray = parser.parse(gsonString).getAsJsonArray();
            for (JsonElement element : jsonarray
                    ) {
                list.add(gson.fromJson(element, t));
            }
        } catch (Exception e) {
        }

        return list;
    }


    /**
     * 转成list中有map的
     *
     * @param gsonString
     * @return
     */
    public static <T> List<Map<String, T>> gsonToListMaps(String gsonString) {
        List<Map<String, T>> list = null;
        try {
            list = gson.fromJson(gsonString,
                    new TypeToken<List<Map<String, T>>>() {
                    }.getType());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public static <T> T getGsonObj(String gsonString, Type type) {
        T t = null;
        try {
            t = gson.fromJson(gsonString, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }
    /**
     * 转成map的
     *
     * @param gsonString
     * @return
     */
    @Deprecated //有bug，建议用下面的gsonToMaps(String gsonString, TypeToken<Map<K, V>> type)
    public static <T> Map<String, T> gsonToMaps(String gsonString) {
        Map<String, T> map = null;
        try {
            map = gson.fromJson(gsonString, new TypeToken<Map<String, T>>() {
            }.getType());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

    public static <K,V> Map<K, V> gsonToMaps(String gsonString, TypeToken<Map<K, V>> type) {
        Map<K, V> map = null;
        try {
            map = new Gson().fromJson(gsonString, type.getType());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }
    public static String getStringIncludeExpose(Object obj){
        try {
            return new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(obj);
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }
}
