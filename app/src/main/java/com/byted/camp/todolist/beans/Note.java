package com.byted.camp.todolist.beans;

import java.util.Date;

/**
 * Created on 2019/1/23.
 *
 * @author xuyingyi@bytedance.com (Yingyi Xu)
 */
public class Note {

    public final long id;
    private Date time;
    private State place;
    private String issue;
    private int rank;

    public Note(long id) {
        this.id = id;
    }

    public Date getDate() {
        return time;
    }

    public void setDate(Date date) {
        this.time = date;
    }

    public State getState() {
        return place;
    }

    public void setState(State state) {
        this.place = state;
    }

    public String getContent() {
        return issue;
    }

    public void setContent(String content) {
        this.issue = content;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int level) {
        this.rank = level;
    }
}
