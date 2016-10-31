package com.humbleai.humblenotes;

import java.io.Serializable;

public class SetItem implements Serializable{

    private int id;
    private int parentSetId;
    private int icon;
    private String title;
    private String image;
    private String setType;
    private int itemViewType;
    private int excluded;

    public SetItem(){}

    public SetItem(int parentSetId, int icon, String title, String image) {
        super();
        this.parentSetId = parentSetId;
        this.icon = icon;
        this.title = title;
        this.setType = "user";
        this.itemViewType = 0; //liste
        this.excluded = 0;
        this.image = image;
    }

    //getters & setters

    @Override
    public String toString() {
        return "SetItem [id=" + id + ", parent set id=" + parentSetId + ", icon=" + icon + ", title=" + title + ", settype=" + setType +  ", excluded=" + excluded +   ", image=" + image + "]";
    }

    public int getParentSetId() {
        return parentSetId;
    }

    public int getIcon() {
        return icon;
    }

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public String getSetType() {
        return setType;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setParentSetId(int parentSetId) {
        this.parentSetId = parentSetId;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSetType(String setType) {
        this.setType = setType;
    }

    public void setItemViewType() { this.itemViewType = 1; }

    public int getItemViewType() {
        return itemViewType;
    }

    public void setExcluded(int excluded) { this.excluded = excluded; }

    public int getExcluded() {
        return excluded;
    }

    public int getId() {
        return id;
    }
}