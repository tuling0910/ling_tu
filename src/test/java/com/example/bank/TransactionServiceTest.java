package com.example.bank;

import com.example.bank.model.Transaction;
import com.example.bank.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
        assertNotNull(transactions);
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


//    @Test
//    public void testListTransactionsByPage() {
//        var page1 = transactionService.listTransactionsByPage(1, 10);
//        assertEquals(10, page1.getContent().size());
//        assertEquals(1, page1.getPage());
//        assertEquals(10, page1.getSize());
//        assertEquals(25, page1.getTotalElements());
//        assertEquals(3, page1.getTotalPages());
//    }
}

