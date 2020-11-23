package com.jobtick.models;

import androidx.collection.ArraySet;

import java.util.Set;

public class TaskCategoryPreview {

    private Set<TaskCategory> categorySet = new ArraySet<>();

    public Set<TaskCategory> getCategorySet() {
        return categorySet;
    }

    public void setCategorySet(Set<TaskCategory> categorySet) {
        this.categorySet = categorySet;
    }

    public void deleteItem(TaskCategory category){
        categorySet.remove(category);
    }

    public void addItem(TaskCategory category){
        categorySet.add(category);
    }
}
