package com.tma.ems.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;

@Data
@Entity
@Table(name = "ntp_address")
public class Ntpaddress {
    @Column
    String address;
    @Column
    boolean status = false;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ntpserver_id", nullable = false)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonIgnore
    Ntpserver ntpserver;
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public Ntpaddress(String address, boolean status) {
        this.address = address;
        this.status = status;
    }

    public Ntpaddress(String address) {
        this.address = address;
    }

    public Ntpaddress() {

    }
}
