package com.jobtick.android.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import timber.log.Timber;

public class CancellationReasonModel {
    String TAG = CancellationReasonModel.class.getName();
    @SerializedName("worker")
    @Expose
    private ArrayList<String> worker = null;
    @SerializedName("poster")
    @Expose
    private ArrayList<String> poster = null;
    TaskModel taskModel;
    /**
     * No args constructor for use in serialization
     *
     */
    public CancellationReasonModel() {
    }


    public CancellationReasonModel(TaskModel taskModel){
        this.taskModel = taskModel;
    }
    /**
     *
     * @param worker
     * @param poster
     */
    public CancellationReasonModel(ArrayList<String> worker, ArrayList<String> poster) {
        super();
        this.worker = worker;
        this.poster = poster;
    }

    public ArrayList<String> getWorker() {
        return worker;
    }

    public void setWorker(ArrayList<String> worker) {
        this.worker = worker;
    }

    public ArrayList<String> getPoster() {
        return poster;
    }

    public void setPoster(ArrayList<String> poster) {
        this.poster = poster;
    }

    public CancellationReasonModel getJsonTOModel(JSONObject jsonObject){
        CancellationReasonModel cancellationReasonModel = new CancellationReasonModel();
        try{
            if(jsonObject.has("worker") && !jsonObject.isNull("worker")){
                ArrayList<String> worker_list = new ArrayList<>();
                JSONArray jsonArray_worker = jsonObject.getJSONArray("worker");
                for(int i = 0; jsonArray_worker.length() > i; i++){
                    String worker = jsonArray_worker.getString(i).replace("{user}",taskModel.getPoster().getName());
                    worker_list.add(worker);
                }
                cancellationReasonModel.setWorker(worker_list);
            }
            if(jsonObject.has("poster") && !jsonObject.isNull("poster")){
                ArrayList<String> poster_list = new ArrayList<>();
                JSONArray jsonArray_poster = jsonObject.getJSONArray("poster");
                for(int i = 0; jsonArray_poster.length() > i; i++){
                    String poster = jsonArray_poster.getString(i).replace("{user}",taskModel.getWorker().getName());
                    poster_list.add(poster);
                }
                cancellationReasonModel.setPoster(poster_list);
            }
        }catch (JSONException e){
            Timber.e(e.toString());
            e.printStackTrace();
        }
        return cancellationReasonModel;
    }
}
