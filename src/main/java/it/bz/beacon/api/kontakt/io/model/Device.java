package it.bz.beacon.api.kontakt.io.model;

import it.bz.beacon.api.kontakt.io.model.enumeration.Model;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Device {
    private UUID id;
    private String uniqueId;
    private String alias;
    private List<String> tags;
    private DeviceType deviceType;
    private Model model;
    private String product;
    private Specification specification;
    private UUID managerId;
    private UUID ownerId;
    private Access access;
    private Venue venue;
    private List<Share> shares;
    private String firmware;
    private Object metadata;
    private float lat;
    private float lng;
    private float deployedlat;
    private float deployedlng;
    private String orderId;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public DeviceType getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public Specification getSpecification() {
        return specification;
    }

    public void setSpecification(Specification specification) {
        this.specification = specification;
    }

    public UUID getManagerId() {
        return managerId;
    }

    public void setManagerId(UUID managerId) {
        this.managerId = managerId;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }

    public Access getAccess() {
        return access;
    }

    public void setAccess(Access access) {
        this.access = access;
    }

    public Venue getVenue() {
        return venue;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    public List<Share> getShares() {
        return shares;
    }

    public void setShares(List<Share> shares) {
        this.shares = shares;
    }

    public String getFirmware() {
        return firmware;
    }

    public void setFirmware(String firmware) {
        this.firmware = firmware;
    }

    public Object getMetadata() {
        return metadata;
    }

    public void setMetadata(Object metadata) {
        this.metadata = metadata;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getLng() {
        return lng;
    }

    public void setLng(float lng) {
        this.lng = lng;
    }

    public float getDeployedlat() {
        return deployedlat;
    }

    public void setDeployedlat(float deployedlat) {
        this.deployedlat = deployedlat;
    }

    public float getDeployedlng() {
        return deployedlng;
    }

    public void setDeployedlng(float deployedlng) {
        this.deployedlng = deployedlng;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public enum DeviceType {
        BEACON,
        GATEWAY,
        EXTERNAL
    }

    public enum Specification {
        STANDARD,
        TOUGH
    }

    public enum Access {
        OWNER,
        SUPERVISOR,
        EDITOR,
        VIEWER
    }

    public static class Share {

        private String managerMail;
        private Access access;
        private Date expirationDate;

        public String getManagerMail() {
            return managerMail;
        }

        public void setManagerMail(String managerMail) {
            this.managerMail = managerMail;
        }

        public Access getAccess() {
            return access;
        }

        public void setAccess(Access access) {
            this.access = access;
        }

        public Date getExpirationDate() {
            return expirationDate;
        }

        public void setExpirationDate(Date expirationDate) {
            this.expirationDate = expirationDate;
        }
    }
}
