package it.bz.beacon.api.db.model;

import javax.persistence.*;

@Entity
@Table( name = "OrderData" )
public class OrderData extends AuditModel {

    @Id
    @Column(unique = true)
    private String id;
    private String orderSymbol;

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

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }
}
