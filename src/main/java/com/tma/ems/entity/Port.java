package com.tma.ems.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;

@Data
@Entity
@Table(name = "ports")
public class Port {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    String connector;

    @Column(name = "port_name",nullable = false)
    String portName;

    @Column(nullable = false)
    boolean state;

    @Column(columnDefinition = "varchar(20) default 'auto'")
    String speed;

    @Column
    String mtu;

    @Column(columnDefinition = "varchar(20) default 'auto'")
    String mdi;

    @Column(name = "mac_address",nullable = false,length = 17)
    String macAddress;

    @OneToOne(fetch = FetchType.LAZY,cascade = CascadeType.REMOVE)
    @JsonIgnore
    Interface anInterface;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id",nullable = false)
    @EqualsAndHashCode.Exclude @ToString.Exclude
    @JsonIgnore
    Device device;
}
