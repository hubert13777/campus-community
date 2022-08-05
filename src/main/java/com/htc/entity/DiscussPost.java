package com.htc.entity;

import java.util.Date;

public class DiscussPost {
    private Integer postId;         //帖子id，主键
    private Integer userId;         //用户id
    private String title;           //标题，最多50字
    private String content;         //内容，最多2000字
    private String type;            //类型，0普通，1置顶
    private String status;          //状态，0正常，1精华，2拉黑
    private Date createTime;        //创建时间
    private Integer commentCount;   //评论数量
    private Double score;           //帖子的分数，即热度
    
    public Integer getPostId() {
        return postId;
    }
    
    public void setPostId(Integer postId) {
        this.postId = postId;
    }
    
    public Integer getUserId() {
        return userId;
    }
    
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Date getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    
    public Integer getCommentCount() {
        return commentCount;
    }
    
    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }
    
    public double getScore() {
        return score;
    }
    
    public void setScore(double score) {
        this.score = score;
    }
    
    @Override
    public String toString() {
        return "DiscussPost{" +
                "postId=" + postId +
                ", userId=" + userId +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", type=" + type +
                ", status=" + status +
                ", createTime=" + createTime +
                ", CommentCount=" + commentCount +
                ", score=" + score +
                '}';
    }
}
