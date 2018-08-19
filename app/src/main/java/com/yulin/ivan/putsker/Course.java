package com.yulin.ivan.putsker;

/**
 * Created by tyizchak on 8/18/2018.
 */

public class Course {

    private String courseName;
    private int imageSrcID;
    //groups array

    public Course(String courseName, int imageSrcID) {
        this.courseName = courseName;
        this.imageSrcID = imageSrcID;
    }


    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public int getImageSrc() {
        return imageSrcID;
    }

    public void setImageSrc(int imageSrcID) {
        this.imageSrcID = imageSrcID;
    }
}
