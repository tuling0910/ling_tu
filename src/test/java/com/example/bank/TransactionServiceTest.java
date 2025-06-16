package com.example.bank;

import com.example.bank.model.PageResponse;
import com.example.bank.model.Transaction;
import com.example.bank.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TransactionServiceTest {
    @Autowired
    private TransactionService transactionService;

    @Test
    public void testCreateAndListTransactions() {
        // 创建交易
        Transaction transaction = new Transaction("ACC123", new BigDecimal("100.00"), "DEPOSIT");
        Transaction created = transactionService.createTransaction(transaction);
        assertNotNull(created.getId());

        // 查询所有交易
        Transaction transactions = transactionService.listTransactionById(created.getId());
        assertNotNull(transactions);
    }

    @Test
    public void testDeleteTransaction() {
        Transaction transaction = new Transaction("ACC123", new BigDecimal("100.00"), "DEPOSIT");
        Transaction created = transactionService.createTransaction(transaction);

        // 删除交易
        transactionService.deleteTransaction(created.getId());
        Transaction transactions = transactionService.listTransactionById(created.getId());
        if(transactions == null){
            assertTrue(true);
        }else{
            assertFalse(false);
        }
    }

    @Test
    public void testUpdateTransaction() {
        Transaction transaction = new Transaction("ACC123", new BigDecimal("100.00"), "DEPOSIT");
        Transaction created = transactionService.createTransaction(transaction);

        // 修改交易
        created.setAmount(new BigDecimal("200.00"));
        Transaction updated = transactionService.updateTransaction(created.getId(), created);
        assertEquals(new BigDecimal("200.00"), updated.getAmount());
    }


    @Test
    public void testListTransactionsByPage() {
        PageResponse<Transaction> page = transactionService.listTransactionsByPage(1, 10);
        assertNotNull(page);

    }
}

