package com.humbleai.humblenotes;

public class SetList {

    private int id;
    private int icon;
    private String title;
    private String description;
    private String prime;
    private String setType; // stock, user
    private int itemViewType;

    public SetList(){}

    public SetList(int icon, String title, String description) {
        super();
       // if (id > 0) this.id = id;
        this.icon = icon;
        this.title = title;
        this.description = description;
        this.prime = "Title";
        this.setType = "user";
        this.itemViewType = 0; //liste
    }

    //getters & setters

    @Override
    public String toString() {
        return "SetList [id=" + id + ", icon=" + icon + ", title=" + title + ", description=" + description
                + "]";
    }


    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getIcon() {
        return icon;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public void setPrime(String prime) {
        this.prime = prime;
    }

    public String getPrime() {
        return prime;
    }

    public void setSetType(String setType) {
        this.setType = setType;
    }

    public String getSetType() {
        return setType;
    }

    public void setItemViewType() { this.itemViewType = 1; }

    public int getItemViewType() {
        return itemViewType;
    }
}
