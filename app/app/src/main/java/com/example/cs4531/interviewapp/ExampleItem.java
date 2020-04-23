package com.example.cs4531.interviewapp;

public class ExampleItem {

    private String mQuestion;
    private String mType;
    private String mReasonReport;
    private String mUser;

    public ExampleItem(String question, String type, String reasonReport, String user){
        mQuestion = question;
        mType = type;
        mReasonReport = reasonReport;
        mUser = user;
    }

    public String getmQuestion(){
        return mQuestion;
    }

    public String getmType(){
        return mType;
    }

    public String getmReasonReport(){
        return mReasonReport;
    }

    public String getmUser(){
        return mUser;
    }

}
