package it.bz.beacon.api.db.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.bz.beacon.api.model.GroupUpdate;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Table(name = "\"Group\"")
public class Group extends AuditModel {
    private static final long serialVersionUID = -3399839430389394314L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    @Type(type = "org.hibernate.type.StringType")
    private String name;

    @Type(type = "org.hibernate.type.StringType")
    private String kontaktIoApiKey;

    public static Group create(GroupUpdate groupUpdate) {
        Group group = new Group();
        group.setName(groupUpdate.getName());
        group.setKontaktIoApiKey(groupUpdate.getKontaktIoApiKey());

        return group;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKontaktIoApiKey() {
        return kontaktIoApiKey;
    }

    public void setKontaktIoApiKey(String kontaktIoApiKey) {
        this.kontaktIoApiKey = kontaktIoApiKey;
    }

    @JsonIgnore
    public void applyUpdate(GroupUpdate groupUpdate) {
        name = groupUpdate.getName();
        kontaktIoApiKey = groupUpdate.getKontaktIoApiKey();
    }
}
