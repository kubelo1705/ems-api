package com.example.managedevices.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Data
@Entity
@Table(name = "interface")
public class Interface {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false,unique = true)
    @NotBlank
    String name;

    @Column(name = "ip_address",unique = true,nullable = false,length = 15)
    @NotBlank
    String ipAddress;

    @Column(columnDefinition = "boolean default true")
    boolean state;

    @Column(columnDefinition = "boolean default false")
    boolean dhcp;

    @Column
    String netmask;

    @Column
    String gateway;

    @Column
    String info;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id",nullable = false)
    @NotBlank
    @JsonIgnore
    Device device;
}