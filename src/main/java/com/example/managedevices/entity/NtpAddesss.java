package com.example.managedevices.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;

@Data
@Entity
@Table(name = "ntp_address")
public class NtpAddesss {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column
    String address;

    @Column
    boolean status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ntp_id",nullable = false)
    @EqualsAndHashCode.Exclude @ToString.Exclude
    @JsonIgnore
    NtpServer ntpServer;

}
