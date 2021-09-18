package com.borntocode.covidshield.dto;

public class User {
    public User(String firstName, String lastName, String emailAddress, String userPassword, String vaccinationDetails, String userImage) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.userPassword = userPassword;
        this.vaccinationDetails = vaccinationDetails;
        this.userImage = userImage;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getVaccinationDetails() {
        return vaccinationDetails;
    }

    public void setVaccinationDetails(String vaccinationDetails) {
        this.vaccinationDetails = vaccinationDetails;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String firstName,lastName,emailAddress,userPassword,vaccinationDetails,userImage;
}
