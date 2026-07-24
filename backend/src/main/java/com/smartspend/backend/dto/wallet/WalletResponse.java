package com.smartspend.backend.dto.wallet;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.smartspend.backend.entity.wallet.Wallet;
import com.smartspend.backend.entity.wallet.WalletType;

public class WalletResponse {

    private Long id;
    private String name;
    private WalletType type;
    private String currency;
    private BigDecimal balance;
    private boolean archived;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static WalletResponse from(Wallet wallet) {
        WalletResponse response = new WalletResponse();

        response.setId(wallet.getId());
        response.setName(wallet.getName());
        response.setType(wallet.getType());
        response.setCurrency(wallet.getCurrency());
        response.setBalance(wallet.getBalance());
        response.setArchived(wallet.isArchived());
        response.setCreatedAt(wallet.getCreatedAt());
        response.setUpdatedAt(wallet.getUpdatedAt());

        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public WalletType getType() {
        return type;
    }

    public void setType(WalletType type) {
        this.type = type;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
