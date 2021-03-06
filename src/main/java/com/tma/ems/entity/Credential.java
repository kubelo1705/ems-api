package com.tma.ems.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
@Entity
@Table(name = "credentials")
public class Credential {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @Column(unique = true, nullable = false)
    @NotBlank(message = "Empty name credential")
    String name;

    @Column(nullable = false)
    @NotBlank(message = "Empty username")
    @Size(min = 5)
    String username;

    @Column(nullable = false)
    @NotBlank(message = "Empty password")
    @Size(min = 5)
    String password;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "credential")
    @JsonIgnore
    Set<Device> devices;
}
