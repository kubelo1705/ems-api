package com.example.managedevices.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.Set;


@Entity
@Data
@Table(name = "ntpservers")
public class NtpServer {
    @Id
    @Column(name = "id", nullable = false)
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

    @OneToMany(mappedBy = "ntpServer",fetch = FetchType.EAGER)
    Set<NtpAddesss> ntpAddessses;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id",nullable = false)
    @EqualsAndHashCode.Exclude @ToString.Exclude
    @JsonIgnore
    Device device;
}
