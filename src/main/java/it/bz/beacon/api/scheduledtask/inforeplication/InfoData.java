package it.bz.beacon.api.scheduledtask.inforeplication;

import it.bz.beacon.api.scheduledtask.inforeplication.error.FieldError;
import it.bz.beacon.api.scheduledtask.inforeplication.error.GeneralError;
import it.bz.beacon.api.scheduledtask.inforeplication.error.ParseError;

import java.util.ArrayList;
import java.util.List;

public class InfoData {

    private String name;
    private String website;
    private String address;
    private String location;
    private String cap;
    private double latitude;
    private double longitude;
    private String floor;
    private List<ParseError> errors = new ArrayList<>();

    InfoData(List<Object> row) {
        List<String> fields = new ArrayList<>();
        for (Object entry : row) {
            try {
                fields.add((String) entry);
            } catch (ClassCastException e) {
                this.errors.add(new GeneralError("Row contains unparseable values"));
                return;
            }
        }

        for (int i = 1; i < fields.size(); i++) {
            String field = fields.get(i);
            switch (i) {
                case 1:
                    setName(field);
                    break;
                case 5:
                    setWebsite(field);
                    break;
                case 6:
                    setAddress(field);
                    break;
                case 7:
                    setLocation(field);
                    break;
                case 8:
                    setCap(field);
                    break;
                case 9:
                    setLatitude(field);
                    break;
                case 10:
                    setLongitude(field);
                    break;
                case 11:
                    setFloor(field);
                    break;
            }
        }
    }

    private void setName(String name) {
        this.name = name;
    }

    private void setWebsite(String website) {
        if (website != null && website.trim().length() > 0
                && !website.matches("^(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]")) {
            this.errors.add(new FieldError("URL is not valid", "website", website));
        } else {
            this.website = website;
        }
    }

    private void setAddress(String address) {
        this.address = address;
    }

    private void setLocation(String location) {
        this.location = location;
    }

    private void setCap(String cap) {
        if (cap != null && cap.trim().length() > 1 && (cap.length() < 4 || cap.length() > 7)) {
            this.errors.add(new FieldError("CAP length has to be between 4 and 6","cap", cap));
        } else {
            this.cap = cap;
        }
    }

    private void setLatitude(String latitude) {
        try {
            if (latitude != null && latitude.trim().length() > 0) {
                this.latitude = Double.parseDouble(latitude);
            }
        } catch (NumberFormatException e) {
            this.errors.add(new FieldError("Latitude must be between -90 and +90 degrees", "latitude", latitude));
        }
    }

    private void setLongitude(String longitude) {
        try {
            if (longitude != null && longitude.trim().length() > 0) {
                this.longitude = Double.parseDouble(longitude);
            }
        } catch (NumberFormatException e) {
            this.errors.add(new FieldError("Longitude must be between -180 and +180 degrees","longitude", longitude));
        }
    }

    private void setFloor(String floor) {
        this.floor = floor;
    }

    public String getName() {
        return name;
    }

    public String getWebsite() {
        return website;
    }

    public String getAddress() {
        return address;
    }

    public String getLocation() {
        return location;
    }

    public String getCap() {
        return cap;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getFloor() {
        return floor;
    }

    public boolean isValid() {
        return this.errors.size() == 0;
    }

    public List<ParseError> getErrors() {
        return errors;
    }
}