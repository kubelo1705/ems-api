package com.example.managedevices.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Table(name = "interfaces")
public class Interface {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    String name;

    @Column(name = "ip_address",nullable = false)
    String ipAddress;

    @Column(columnDefinition = "boolean default true")
    boolean state=true;

    @Column(columnDefinition = "boolean default false")
    boolean dhcp=false;

    @Column
    String netmask;

    @Column
    String gateway;

    @Column
    String info;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id",nullable = false)
    @JsonIgnore
    @EqualsAndHashCode.Exclude @ToString.Exclude
    Device device;

}
