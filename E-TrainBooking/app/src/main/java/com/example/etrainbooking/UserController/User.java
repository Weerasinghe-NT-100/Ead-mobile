package com.example.etrainbooking.UserController;

import java.io.Serializable;

public class User implements Serializable {
    private String nic, username, password, role, contactNo, name, age, address;
    private boolean isActive;

    public User() {

    }

    public User(String nic, String username, String password, String role, String contactNo, String name, String age, String address, boolean isActive) {
        this.nic = nic;
        this.username = username;
        this.password = password;
        this.role = role;
        this.contactNo = contactNo;
        this.name = name;
        this.age = age;
        this.address = address;
        this.isActive = isActive;
    }

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    public String getUserName() {
        return username;
    }

    public void setUserName(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }
}
