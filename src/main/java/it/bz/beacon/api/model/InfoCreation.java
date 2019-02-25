package it.bz.beacon.api.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class InfoCreation {

    @NotEmpty
    @NotNull
    private String beaconId;

    @NotEmpty
    @NotNull
    private String nameDe;
    private String nameIt;
    private String nameEn;

    @NotEmpty
    @NotNull
    private String descriptionDe;
    private String descriptionIt;
    private String descriptionEn;

    @NotEmpty
    @NotNull
    private String websiteDe;
    private String websiteIt;
    private String websiteEn;

    @NotEmpty
    @NotNull
    private String address;

    @NotEmpty
    @NotNull
    private String location;

    @NotEmpty
    @NotNull
    private String cap;

    @NotEmpty
    @NotNull
    private String latitude;
    @NotEmpty
    @NotNull
    private String longitude;

    private String additionalLocationInfo;

    private String contactPersonName;
    private String contactPersonLastName;
    @Email
    private String contactPersonEmail;
    private String contactPersonOrganisation;

    public String getBeaconId() {
        return beaconId;
    }

    public void setBeaconId(String beaconId) {
        this.beaconId = beaconId;
    }

    public String getNameDe() {
        return nameDe;
    }

    public void setNameDe(String nameDe) {
        this.nameDe = nameDe;
    }

    public String getNameIt() {
        return nameIt;
    }

    public void setNameIt(String nameIt) {
        this.nameIt = nameIt;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getDescriptionDe() {
        return descriptionDe;
    }

    public void setDescriptionDe(String descriptionDe) {
        this.descriptionDe = descriptionDe;
    }

    public String getDescriptionIt() {
        return descriptionIt;
    }

    public void setDescriptionIt(String descriptionIt) {
        this.descriptionIt = descriptionIt;
    }

    public String getDescriptionEn() {
        return descriptionEn;
    }

    public void setDescriptionEn(String descriptionEn) {
        this.descriptionEn = descriptionEn;
    }

    public String getWebsiteDe() {
        return websiteDe;
    }

    public void setWebsiteDe(String websiteDe) {
        this.websiteDe = websiteDe;
    }

    public String getWebsiteIt() {
        return websiteIt;
    }

    public void setWebsiteIt(String websiteIt) {
        this.websiteIt = websiteIt;
    }

    public String getWebsiteEn() {
        return websiteEn;
    }

    public void setWebsiteEn(String websiteEn) {
        this.websiteEn = websiteEn;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCap() {
        return cap;
    }

    public void setCap(String cap) {
        this.cap = cap;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAdditionalLocationInfo() {
        return additionalLocationInfo;
    }

    public void setAdditionalLocationInfo(String additionalLocationInfo) {
        this.additionalLocationInfo = additionalLocationInfo;
    }

    public String getContactPersonName() {
        return contactPersonName;
    }

    public void setContactPersonName(String contactPersonName) {
        this.contactPersonName = contactPersonName;
    }

    public String getContactPersonLastName() {
        return contactPersonLastName;
    }

    public void setContactPersonLastName(String contactPersonLastName) {
        this.contactPersonLastName = contactPersonLastName;
    }

    public String getContactPersonEmail() {
        return contactPersonEmail;
    }

    public void setContactPersonEmail(String contactPersonEmail) {
        this.contactPersonEmail = contactPersonEmail;
    }

    public String getContactPersonOrganisation() {
        return contactPersonOrganisation;
    }

    public void setContactPersonOrganisation(String contactPersonOrganisation) {
        this.contactPersonOrganisation = contactPersonOrganisation;
    }
}
