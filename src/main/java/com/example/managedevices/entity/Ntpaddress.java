package com.example.managedevices.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;

@Data
@Entity
@Table(name = "ntp_address")
public class Ntpaddress {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    String address;

    @Column
    boolean status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ntpserver_id",nullable = false)
    @EqualsAndHashCode.Exclude @ToString.Exclude
    @JsonIgnore
    Ntpserver ntpserver;

    public Ntpaddress(String address, boolean status, Ntpserver ntpservers) {
        this.address = address;
        this.status = status;
        this.ntpserver = ntpservers;
    }

    public Ntpaddress() {

    }
}
