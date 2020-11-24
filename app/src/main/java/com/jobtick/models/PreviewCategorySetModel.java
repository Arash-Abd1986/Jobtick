package com.jobtick.models;

import androidx.collection.ArraySet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public class PreviewCategorySetModel {

    private final Stack<PreviewCategoryModel> stack = new Stack<>();

    public Set<PreviewCategoryModel> getPreviewSet() {
        Stack<PreviewCategoryModel> preStack = new Stack<>();
        preStack.addAll(stack);
        Set<PreviewCategoryModel> previewCategoryModels = new HashSet<>();
        while (!stack.empty()){
            previewCategoryModels.add(stack.pop());
        }
        stack.addAll(preStack);
        return previewCategoryModels;
    }

    public void deleteItem(PreviewCategoryModel previewCategoryModel){
        stack.remove(previewCategoryModel);
    }

    public void addItem(PreviewCategoryModel previewCategoryModel){
        stack.push(previewCategoryModel);
    }
}
