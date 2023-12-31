package com.klasha.assessment.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;


@Entity
@Getter
@Setter
@Table(name = "exchange_rate")
@EntityListeners(AuditingEntityListener.class)
public class ExchangeRate implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "source_currency",nullable = false)
    @NonNull
    private String sourceCurrency;

    @Column(name = "target_currency",nullable = false)
    @NonNull
    private String targetCurrency;

    @NonNull
    @Column(name = "rate",nullable = false)
    private BigDecimal rate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @PrePersist
    public void beforeSave() {
        this.createdAt = new Timestamp(new Date().getTime());
    }

    @PreUpdate
    private void beforeUpdate() {
        this.updatedAt = new Timestamp(new Date().getTime());
    }
}
