package com.smartspend.backend.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.smartspend.backend.entity.wallet.Wallet;
import com.smartspend.backend.repository.WalletRepository;
import com.smartspend.backend.service.WalletService;

@Service
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;

    public WalletServiceImpl(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Override
    public Wallet createWallet(Wallet wallet) {
        return walletRepository.save(wallet);
    }

    @Override
    public Wallet getWalletById(Long id) {
        return walletRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
    }

    @Override
    public List<Wallet> getAllWallets() {
        return List.of();
    }

    @Override
    public Wallet updateWallet(Long id, Wallet wallet) {
        return null;
    }

    @Override
    public void deleteWallet(Long id) {
    }
}