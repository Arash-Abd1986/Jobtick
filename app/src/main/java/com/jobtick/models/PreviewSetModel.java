package com.jobtick.models;

import androidx.collection.ArraySet;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public class PreviewSetModel {

    private final Stack<PreviewModel> stack = new Stack<>();

    public Set<PreviewModel> getPreviewSet() {
        Set<PreviewModel> previewModels = new ArraySet<>();
        while (!stack.empty()){
            previewModels.add(stack.peek());
        }
        return previewModels;
    }

    public void setPreviewSet(Set<PreviewModel> previewSet) {
        List<PreviewModel> models = new ArrayList<>(previewSet);
        for (int i = previewSet.size() - 1; i >= 0; i--) {
            addItem(models.get(i));
        }
    }

    public void deleteItem(PreviewModel previewModel){
        stack.remove(previewModel);
    }

    public void addItem(PreviewModel previewModel){
        stack.push(previewModel);
    }
}
