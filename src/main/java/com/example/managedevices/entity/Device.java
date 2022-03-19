package com.example.managedevices.entity;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Table(name = "devices")
public class Device {
    @Id
    @Column(name = "id",nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @Column(name = "ip_address",unique = true,length = 15,nullable = false)
    String ipAddress;

    @Column
    String name;

    @Column
    String type;

    @Column(name="firmware_version")
    String firmwareVersion;

    @Column(name = "serial_number")
    String serialNumber;

    @Column(name="mac_base_address")
    String macAddress;

    @Column(name = "unit_identifier")
    String unitIdentifier;

    @Column(columnDefinition = "integer default 22")
    int port=22;

    @Column(columnDefinition = "boolean default false")
    boolean status=false;

    @ManyToOne
    @EqualsAndHashCode.Exclude @ToString.Exclude
    @JoinColumn(name = "credential_id",nullable = false)
    Credential credential;

    @OneToMany(mappedBy = "device",fetch = FetchType.EAGER,cascade = CascadeType.REMOVE)
    Set<Interface> interfaces=new HashSet<>();

    @OneToMany (mappedBy = "device",fetch = FetchType.EAGER)
    Set<Port> ports=new HashSet<>();

    @OneToOne(mappedBy = "device",fetch = FetchType.EAGER)
    Ntpserver ntpserver;
}
