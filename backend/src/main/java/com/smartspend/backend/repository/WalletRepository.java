package com.smartspend.backend.repository;

import com.smartspend.backend.entity.wallet.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

}