package com.cardee.data_source.remote.api.cars.response.entity;

import com.cardee.data_source.remote.api.common.entity.BaseCarEntity;
import com.cardee.data_source.remote.api.common.entity.CarRuleEntity;
import com.cardee.data_source.remote.api.common.entity.DeliveryRatesEntity;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CarDetailsEntity extends BaseCarEntity {

    @Expose
    @SerializedName("longitude")
    private Float longitude;
    @Expose
    @SerializedName("latitude")
    private Float latitude;
    @Expose
    @SerializedName("address")
    private String address;
    @Expose
    @SerializedName("town")
    private String town;
    @Expose
    @SerializedName("req_min_age")
    private Integer requiredMinAge;
    @Expose
    @SerializedName("req_dr_exp")
    private Integer requiredDrivingExp;
    @Expose
    @SerializedName("car_rules")
    private CarRuleEntity[] rules;
    @Expose
    @SerializedName("car_other_rules")
    private String carOtherRules;
    @Expose
    @SerializedName("is_req_security_deposit")
    private Boolean requiredSecurityDeposit;
    @Expose
    @SerializedName("security_deposit_description")
    private String securityDepositDescription;
    @Expose
    @SerializedName("compensation_excess")
    private String compensationAccess;
    @Expose
    @SerializedName("compensation_other_guidelines")
    private String compensationOtherGuidelines;
    @Expose
    @SerializedName("add_ons")
    private String addOns;
    @Expose
    @SerializedName("additional_terms")
    private String additionalTerms;
    @Expose
    @SerializedName("delivery_rates")
    private DeliveryRatesEntity deliveryRates;

    public CarDetailsEntity() {

    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }

    public Float getLongitude() {
        return longitude;
    }

    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public Integer getRequiredMinAge() {
        return requiredMinAge;
    }

    public void setRequiredMinAge(Integer requiredMinAge) {
        this.requiredMinAge = requiredMinAge;
    }

    public Integer getRequiredDrivingExp() {
        return requiredDrivingExp;
    }

    public void setRequiredDrivingExp(Integer requiredDrivingExp) {
        this.requiredDrivingExp = requiredDrivingExp;
    }

    public String getCarOtherRules() {
        return carOtherRules;
    }

    public void setCarOtherRules(String carOtherRules) {
        this.carOtherRules = carOtherRules;
    }

    public Boolean getRequiredSecurityDeposit() {
        return requiredSecurityDeposit;
    }

    public void setRequiredSecurityDeposit(Boolean requiredSecurityDeposit) {
        this.requiredSecurityDeposit = requiredSecurityDeposit;
    }

    public String getSecurityDepositDescription() {
        return securityDepositDescription;
    }

    public void setSecurityDepositDescription(String securityDepositDescription) {
        this.securityDepositDescription = securityDepositDescription;
    }

    public String getCompensationAccess() {
        return compensationAccess;
    }

    public void setCompensationAccess(String compensationAccess) {
        this.compensationAccess = compensationAccess;
    }

    public String getCompensationOtherGuidelines() {
        return compensationOtherGuidelines;
    }

    public void setCompensationOtherGuidelines(String compensationOtherGuidelines) {
        this.compensationOtherGuidelines = compensationOtherGuidelines;
    }

    public String getAddOns() {
        return addOns;
    }

    public void setAddOns(String addOns) {
        this.addOns = addOns;
    }

    public CarRuleEntity[] getRules() {
        return rules;
    }

    public void setRules(CarRuleEntity[] rules) {
        this.rules = rules;
    }

    public String getAdditionalTerms() {
        return additionalTerms;
    }

    public void setAdditionalTerms(String additionalTerms) {
        this.additionalTerms = additionalTerms;
    }

    public DeliveryRatesEntity getDeliveryRates() {
        return deliveryRates;
    }

    public void setDeliveryRates(DeliveryRatesEntity deliveryRates) {
        this.deliveryRates = deliveryRates;
    }
}
