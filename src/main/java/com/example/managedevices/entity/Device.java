package com.example.managedevices.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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

    @Column(columnDefinition = "integer default 22")
    int port=22;

    @Column(columnDefinition = "boolean default false")
    boolean status=false;

    @ManyToOne
    @EqualsAndHashCode.Exclude @ToString.Exclude
    @JoinColumn(name = "credential_id",nullable = false)
    Credential credential;

    @OneToMany(mappedBy = "device",fetch = FetchType.EAGER)
    Set<Interface> interfaces;

    @OneToMany (mappedBy = "device",fetch = FetchType.EAGER)
    Set<Port> ports;

    @OneToMany(mappedBy = "device",fetch = FetchType.EAGER)
    Set<NtpServer> ntpServer;
}
