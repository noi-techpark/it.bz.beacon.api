package it.bz.beacon.api.db.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.bz.beacon.api.model.GroupAssignment;
import it.bz.beacon.api.model.enumeration.UserRole;

import javax.persistence.*;

@Entity
@Table(name = "user_role", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "group_id"})})
public class UserRoleGroup extends AuditModel {
    private static final long serialVersionUID = -164448835727657264L;

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "role_id", nullable = false)
    private UserRole role;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false, updatable = false)
    private Group group;

    @JsonIgnore
    @ManyToOne
    private User user;

    public static UserRoleGroup create(GroupAssignment groupAssignment) {
        UserRoleGroup userRoleGroup = new UserRoleGroup();
        userRoleGroup.setRole(groupAssignment.getUserRole());

        return userRoleGroup;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
