package com.jobtick.models;

import androidx.collection.ArraySet;

import java.util.Set;

public class PreviewModel<T> {

    private Set<T> PreviewSet = new ArraySet<>();

    public Set<T> getPreviewSet() {
        return PreviewSet;
    }

    public void setPreviewSet(Set<T> previewSet) {
        this.PreviewSet = previewSet;
    }

    public void deleteItem(T previewModel){
        PreviewSet.remove(previewModel);
    }

    public void addItem(T previewModel){
        PreviewSet.add(previewModel);
    }
}
