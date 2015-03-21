package com.example.yh.data;

/**
 * Created by yh on 2015/2/12.
 */
public class Comment {
    private String ownerId;
    private String id;
    private String ownerName;
    private String ownerSex;
    private String ownerAttrs;
    private String time;
    private String content;
    private String infoId;
    private String callBackCommentId;
    private int callBackFloor;
    private String callBackCommentContent;

    public Comment(String ownerId, String id, String ownerName, String ownerSex, String ownerAttrs, String time,String content,String infoId,String callBackCommentId,int callBackFloor,String callBackCommentContent) {
        this.ownerId = ownerId;
        this.id = id;
        this.ownerName = ownerName;
        this.ownerSex = ownerSex;
        this.ownerAttrs = ownerAttrs;
        this.time = time;
        this.content = content;
        this.infoId = infoId;
        this.callBackCommentId = callBackCommentId;
        this.callBackFloor = callBackFloor;
        this.callBackCommentContent = callBackCommentContent;
    }

    public String getCallBackCommentContent() {
        return callBackCommentContent;
    }

    public void setCallBackCommentContent(String callBackCommentContent) {
        this.callBackCommentContent = callBackCommentContent;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerSex() {
        return ownerSex;
    }

    public void setOwnerSex(String ownerSex) {
        this.ownerSex = ownerSex;
    }

    public String getOwnerAttrs() {
        return ownerAttrs;
    }

    public void setOwnerAttrs(String ownerAttrs) {
        this.ownerAttrs = ownerAttrs;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getInfoId() {
        return infoId;
    }

    public void setInfoId(String infoId) {
        this.infoId = infoId;
    }

    public String getCallBackCommentId() {
        return callBackCommentId;
    }

    public void setCallBackCommentId(String callBackCommentId) {
        this.callBackCommentId = callBackCommentId;
    }

    public int getCallBackFloor() {
        return callBackFloor;
    }

    public void setCallBackFloor(int callBackFloor) {
        this.callBackFloor = callBackFloor;
    }
}
