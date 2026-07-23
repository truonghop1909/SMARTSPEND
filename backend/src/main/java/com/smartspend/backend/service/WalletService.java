package com.smartspend.backend.service;

import java.util.List;

import com.smartspend.backend.entity.Wallet;

public interface WalletService {

    Wallet createWallet(Wallet wallet);

    Wallet getWalletById(Long id);

    List<Wallet> getAllWallets();

    Wallet updateWallet(Long id, Wallet wallet);

    void deleteWallet(Long id);
}