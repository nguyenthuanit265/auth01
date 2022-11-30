package com.auth.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@SuperBuilder
@NoArgsConstructor
@Entity(name = "Group")
@Table(name = "groups")
public class Group extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "groupId")
    private Long groupId;

    @Column(name = "groupName")
    private String groupName;

    @Column(name = "adminId")
    private Long adminId;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "groups_users",
            joinColumns = @JoinColumn(name = "groupId", referencedColumnName = "groupId"),
            inverseJoinColumns = @JoinColumn(name = "userId", referencedColumnName = "userId"))
    private Set<User> members;
}
