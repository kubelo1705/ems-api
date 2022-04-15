package com.tma.ems.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;

@Data
@Entity
@Table(name = "interfaces")
public class Interface {
    @Column(nullable = false)
    String name;
    @Column(name = "ip_address", nullable = false)
    String ipAddress = "";
    @Column(columnDefinition = "boolean default true")
    boolean state = true;
    @Column(columnDefinition = "boolean default false")
    boolean dhcp = false;
    @Column
    String netmask = "255.255.255.0";
    @Column
    String gateway = "";
    @Column
    String info = "";
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "port_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonIgnore
    Port port;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    Device device;
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
}
