package it.bz.beacon.api.db.model;

import javax.persistence.*;

@Entity
@Table( name = "OrderData", uniqueConstraints={@UniqueConstraint(columnNames = {"zoneId", "zoneCode"})} )
public class OrderData extends AuditModel {
    private static final long serialVersionUID = 2824484059555129298L;

    @Id
    @Column(unique = true)
    private String id;
    private String orderSymbol;
    private Integer zoneId;
    private String zoneCode;

    @OneToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST})
    private Info info;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderSymbol() {
        return orderSymbol;
    }

    public void setOrderSymbol(String orderSymbol) {
        this.orderSymbol = orderSymbol;
    }

    public int getZoneId() {
        return zoneId;
    }

    public void setZoneId(int zoneId) {
        this.zoneId = zoneId;
    }

    public String getZoneCode() {
        return zoneCode;
    }

    public void setZoneCode(String zoneCode) {
        this.zoneCode = zoneCode;
    }

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }
}
