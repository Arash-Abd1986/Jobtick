package com.jobtick.models;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Stack;

public class PreviewTaskSetModel {

    private final Stack<PreviewTaskModel> stack = new Stack<>();

    public Set<PreviewTaskModel> getPreviewSet() {
        Stack<PreviewTaskModel> preStack = new Stack<>();
        preStack.addAll(stack);
        Set<PreviewTaskModel> previewTaskModels = new LinkedHashSet<>();
        while (!stack.empty()) {
            previewTaskModels.add(stack.pop());
        }
        stack.addAll(preStack);
        return previewTaskModels;
    }

    public void deleteItem(PreviewTaskModel previewTaskModel) {
        stack.remove(previewTaskModel);
    }

    public void addItem(PreviewTaskModel previewTaskModel) {
        stack.push(previewTaskModel);
    }
}
