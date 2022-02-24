package com.example.managedevices.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "device")
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @Column(name = "ip_address",unique = true,length = 15)
    String ipAddress;

    @Column
    String name;

    @Column
    String type;

    @Column(name="firmware_version")
    String firmwareVersion;

    @Column(name = "serial_number")
    String serialNumber;




}
