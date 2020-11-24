package com.jobtick.models;

import androidx.collection.ArraySet;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PreviewTaskSetModel {

    private Set<PreviewTaskModel> models = new ArraySet<>();

    public Set<PreviewTaskModel> getPreviewSet() {
        return models;
    }

    public void deleteItem(PreviewTaskModel PreviewTaskModel){
        models.remove(PreviewTaskModel);
    }

    public void addItem(PreviewTaskModel previewTaskModel){
        
        List<PreviewTaskModel> modelList = new ArrayList<>(models);
        List<PreviewTaskModel> reverseModels = new ArrayList<>();
        
        for (int i = modelList.size() - 1; i >= 0; i--) {
            reverseModels.add(modelList.get(i));
        }
        
        reverseModels.add(previewTaskModel);
        models = new ArraySet<>(reverseModels);
    }
}
