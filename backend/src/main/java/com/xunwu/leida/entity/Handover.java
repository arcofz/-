package com.xunwu.leida.entity;

import java.time.OffsetDateTime;

/**
 * 交接表 handovers(claim_id 唯一,一条认领最多一次交接)
 */
public class Handover {

    private Long id;
    private Long claimId;
    private OffsetDateTime handTime;
    private String place;
    private OffsetDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClaimId() {
        return claimId;
    }

    public void setClaimId(Long claimId) {
        this.claimId = claimId;
    }

    public OffsetDateTime getHandTime() {
        return handTime;
    }

    public void setHandTime(OffsetDateTime handTime) {
        this.handTime = handTime;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
