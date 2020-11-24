package com.jobtick.models;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Stack;

public class PreviewCategorySetModel {

    private final Stack<PreviewCategoryModel> stack = new Stack<>();

    public Set<PreviewCategoryModel> getPreviewSet() {
        Stack<PreviewCategoryModel> preStack = new Stack<>();
        preStack.addAll(stack);
        Set<PreviewCategoryModel> previewCategoryModels = new LinkedHashSet<>();
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
