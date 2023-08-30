package com.nagarro.si.pba.controller;

import com.nagarro.si.pba.dto.TransactionDTO;
import com.nagarro.si.pba.dto.TransactionFilterDTO;
import com.nagarro.si.pba.service.TokenService;
import com.nagarro.si.pba.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController implements TransactionControllerApi {
    private final TransactionService transactionService;
    private final TokenService tokenService;

    @Autowired
    public TransactionController(TransactionService transactionService, TokenService tokenService) {
        this.transactionService = transactionService;
        this.tokenService = tokenService;
    }

    @Override
    public List<TransactionDTO> getAll(TransactionFilterDTO transactionFilterDTO, String token) {
        return transactionService.getAllTransactions(transactionFilterDTO, tokenService.extractUserId(token));
    }

    @Override
    public void addTransaction(String token, TransactionDTO transactionDTO) {
        transactionService.addTransaction(transactionDTO, tokenService.extractUserId(token));
    }
}
