package com.example.etrainbooking.UserController;

import java.io.Serializable;

public class User implements Serializable {
    private String Id, Fname,Lname,Email,MobileNo, Nic,Password;

    public User(){

    }

    public User(String id, String fname, String lname, String email, String mobileNo, String nic, String password) {
        Id = id;
        Fname=fname;
        Lname=lname;
        Email = email;
        MobileNo = mobileNo;
        Nic = nic;
        Password = password;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getFname() {
        return Fname;
    }

    public void setFname(String fname) {
        Fname = fname;
    }

    public String getLname() {
        return Lname;
    }

    public void setLname(String lname) {
        Lname = lname;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getMobileNo() {
        return MobileNo;
    }

    public void setMobileNo(String mobileNo) {
        MobileNo = mobileNo;
    }

    public String getNic() {
        return Nic;
    }

    public void setNic(String nic) {
        Nic =nic;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

}
