package com.example.managedevices.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Data
public class NtpServer {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "server_address",nullable = false)
    @NotBlank
    String serverAddress;

    @Column(columnDefinition = "boolean default false")
    boolean state;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id",nullable = false)
    @JsonIgnore
    @NotBlank
    Device device;
}
