package com.suyogbauskar.calmora.POJOs;

public class User {
    private String name, gender;
    private int ageGroup;

    public User() {
    }

    public User(String name, String gender, int ageGroup) {
        this.name = name;
        this.gender = gender;
        this.ageGroup = ageGroup;
    }

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public int getAgeGroup() {
        return ageGroup;
    }
}
