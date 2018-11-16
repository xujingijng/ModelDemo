package com.xjj.freight.utils.photoselect.model;

import java.io.Serializable;

/**
 *  照片实体
 */
public class Photo implements Serializable {
    /*** ID*/
    private int id;
    /*** 路径*/
    private String path;
    /*** 是否是拍照图片*/
    private boolean isCamera;
    /*** 角度*/
    private int degree = -1;

    public Photo(String path) {
        this.path = path;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isCamera() {
        return isCamera;
    }

    public void setIsCamera(boolean isCamera) {
        this.isCamera = isCamera;
    }

    public int getDegree() {
        return degree;
    }

    public void setDegree(int degree) {
        this.degree = degree;
    }
}
