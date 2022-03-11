package com.example.managedevices.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "device")
public class Device {
    @Id
    @Column(name = "id",nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @Column(name = "ip_address",unique = true,length = 15,nullable = false)
    @NotBlank(message = "Empty ip address")
    @Size(max = 15)
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "credential_id",nullable = false)
    @NotNull(message = "Empty credential")
    Credential credential;

    @OneToMany(mappedBy = "device",fetch = FetchType.LAZY)
    Set<Interface> interfaces;

    @OneToMany (mappedBy = "device",fetch = FetchType.LAZY)
    Set<Port> ports;

    @OneToMany(mappedBy = "device",fetch = FetchType.LAZY)
    Set<NtpServer> ntpServers;
}
