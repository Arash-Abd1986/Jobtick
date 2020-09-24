package com.jobtick.callBack;

import java.util.ArrayList;

public interface FragmentCallBack {
    void onDestoryCallBack();
    void onValidDetailsDataFilled(String title, String description, ArrayList<String> musthave,boolean online,String location,double latitude,double longitude);
}
