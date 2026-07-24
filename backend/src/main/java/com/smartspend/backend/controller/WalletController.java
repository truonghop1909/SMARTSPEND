package com.smartspend.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartspend.backend.common.dto.ApiResponse;
import com.smartspend.backend.dto.wallet.WalletResponse;
import com.smartspend.backend.service.WalletService;

@RestController
@RequestMapping("/api/wallets")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<WalletResponse>> getWalletById(@PathVariable Long id) {
        WalletResponse wallet = WalletResponse.from(walletService.getWalletById(id));

        return ResponseEntity.ok(ApiResponse.success(wallet));
    }
}
