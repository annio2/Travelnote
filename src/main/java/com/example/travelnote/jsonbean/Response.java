package com.example.travelnote.jsonbean;

/**
 * Created by wuguanglin on 2017/8/11.
 */

/*服务器响应的json为整形result和对象数组response,Response类对应返回的resposne数组中的一个对象*/
public class Response {
    private int advertisementId;
    private String title;//标题
    private String valstarttime;
    private String valendtime;
    private String link;//图片被点击后跳转的链接
    private int status;
    private int power;

    public pictureInfo getPicInfo() {
        return picInfo;
    }

    public void setPicInfo(pictureInfo picInfo) {
        this.picInfo = picInfo;
    }

    //内部类存放大图信息
    private pictureInfo picInfo;
    public class pictureInfo{
        private int id;//图片id
        private int startId;
        private String picType;//
        private String url;//图片路径，需要和HOST拼接后访问

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getStartId() {
            return startId;
        }

        public void setStartId(int startId) {
            this.startId = startId;
        }

        public String getPicType() {
            return picType;
        }

        public void setPicType(String picType) {
            this.picType = picType;
        }

        @Override
        public String toString() {
            return "pictureInfo:id="+id+"startId="+startId+"picType="+picType+"url="+url;
        }
    }
    private String updateTime;
    private int totalNum;//总数量180
    private int totalPage;//总页数18
    private String userIconUrl;//用户头像路径，有些对象没有该字段
    private int userId;//用户id
    private int helperId;
    private int clickNum;//查看/点击数
    private int zanStatus;
    private int isChoosed;
    private int isCollected;//收藏数
    private int linkType;
    private int commentNum;//评论数
    private int zanNum;//点赞数

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getUserIconUrl() {
        return userIconUrl;
    }

    public void setUserIconUrl(String userIconUrl) {
        this.userIconUrl = userIconUrl;
    }

    public int getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(int commentNum) {
        this.commentNum = commentNum;
    }

    public int getZanNum() {
        return zanNum;
    }

    public void setZanNum(int zanNum) {
        this.zanNum = zanNum;
    }

    public int getAdvertisementId() {
        return advertisementId;
    }

    public void setAdvertisementId(int advertisementId) {
        this.advertisementId = advertisementId;
    }

    public String getValstarttime() {
        return valstarttime;
    }

    public void setValstarttime(String valstarttime) {
        this.valstarttime = valstarttime;
    }

    public String getValendtime() {
        return valendtime;
    }

    public void setValendtime(String valendtime) {
        this.valendtime = valendtime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public int getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(int totalNum) {
        this.totalNum = totalNum;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getHelperId() {
        return helperId;
    }

    public void setHelperId(int helperId) {
        this.helperId = helperId;
    }

    public int getClickNum() {
        return clickNum;
    }

    public void setClickNum(int clickNum) {
        this.clickNum = clickNum;
    }

    public int getZanStatus() {
        return zanStatus;
    }

    public void setZanStatus(int zanStatus) {
        this.zanStatus = zanStatus;
    }

    public int getIsChoosed() {
        return isChoosed;
    }

    public void setIsChoosed(int isChoosed) {
        this.isChoosed = isChoosed;
    }

    public int getIsCollected() {
        return isCollected;
    }

    public void setIsCollected(int isCollected) {
        this.isCollected = isCollected;
    }

    public int getLinkType() {
        return linkType;
    }

    public void setLinkType(int linkType) {
        this.linkType = linkType;
    }
}
