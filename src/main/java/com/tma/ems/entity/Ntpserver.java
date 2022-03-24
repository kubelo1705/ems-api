package com.tma.ems.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.Set;


@Entity
@Data
@Table(name = "ntpservers")
public class Ntpserver {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    boolean client;

    @Column
    int offset;

    @Column
    int dscp;

    @Column
    int vlanPriority;

    @Column
    String syncStatus;

    @Column
    String timeIntervals;

    @Column
    int numberOfMessages;

    @OneToMany(mappedBy = "ntpserver",fetch = FetchType.EAGER,cascade = {CascadeType.PERSIST,CascadeType.REMOVE})
    Set<Ntpaddress> ntpaddresses;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id",referencedColumnName = "id")
    @EqualsAndHashCode.Exclude @ToString.Exclude
    @JsonIgnore
    Device device;
}
