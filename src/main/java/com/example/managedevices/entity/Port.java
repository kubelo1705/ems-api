package com.example.managedevices.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "port")
public class Port {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    boolean status;

    @Column
    String connector;

    @Column(unique = true,name = "port_name",nullable = false)
    String portName;

    @Column(nullable = false)
    boolean state;

    @Column(columnDefinition = "varchar(20) default 'auto'")
    String speed;

    @Column
    String mtu;

    @Column(columnDefinition = "varchar(20) default 'auto'")
    String mdi;

    @Column(name = "mac_address",unique = true,nullable = false,length = 17)
    String macAddress;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id",nullable = false)
    @JsonIgnore
    Device device;
}
