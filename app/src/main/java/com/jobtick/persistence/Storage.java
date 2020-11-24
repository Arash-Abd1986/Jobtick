package com.jobtick.persistence;

import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.io.Serializable;


public class Storage <T extends Serializable> {

    private final Class<T> typeParameterClass;
    protected final SharedPreferences sharedPreferences;
    protected final Gson gson;
    private final String storageKey;

    private final String KEY_SUFFIX = "_storage";


    public Storage(Class<T> typeParameterClass, SharedPreferences sharedPreferences, Gson gson) {
        this(typeParameterClass, sharedPreferences, gson, null);
    }

    public Storage(Class<T> typeParameterClass, SharedPreferences sharedPreferences, Gson gson, String storageKey) {
        this.typeParameterClass = typeParameterClass;
        this.sharedPreferences = sharedPreferences;
        this.gson = gson;
        this.storageKey = createKey(typeParameterClass, storageKey);
    }

    public T retrieve() {
        String json = sharedPreferences.getString(storageKey, null);
        return gson.fromJson(json, typeParameterClass);
    }

    public void store(T object) {
        sharedPreferences.edit().putString(storageKey, gson.toJson(object, typeParameterClass)).commit();
    }

    public void clear() {
        sharedPreferences.edit().remove(storageKey).commit();
    }

    private String createKey(Class className, String storageKey) {
        return (storageKey != null && !storageKey.isEmpty()) ? storageKey : (className.getName() + KEY_SUFFIX);
    }
}
