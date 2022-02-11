package com.example.projectui;

public class Menu {

    private Integer imageId;
    private String name;
    private MenuSettings settings;

    public Menu(Integer imageId, String name, MenuSettings settings) {
        this.imageId = imageId;
        this.name = name;
        this.settings = settings;
    }

    public Integer getImageId() {
        return imageId;
    }

    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MenuSettings getSettings() {
        return settings;
    }

    public void setSettings(MenuSettings settings) {
        this.settings = settings;
    }

    public String toString(){
        return "Menu name: " + name + "\n" + settings.toString();
    }
}
