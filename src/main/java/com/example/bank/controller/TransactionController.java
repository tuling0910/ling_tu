package com.example.bank.controller;

import com.example.bank.model.PageResponse;
import com.example.bank.model.Transaction;
import com.example.bank.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }


//    @GetMapping("/")
//    public String index(Model model) {
//        List<Transaction> transactions = transactionService.listAllTransactions();
//        model.addAttribute("transactions", transactions);
//        return "index"; // 对应 resources/templates/index.html
//    }

    // 2. 跳转新增交易页
    @GetMapping("/create")
    public String showCreateForm() {
        return "create"; // 对应 create.html
    }

    // 创建交易
    @PostMapping("/create")
    public String createTransaction(@ModelAttribute Transaction transaction) {
        try {
            Transaction created = transactionService.createTransaction(transaction);
            return "redirect:/"; // 新增后跳转到列表页
        } catch (IllegalArgumentException e) {
            return "index";
        }
    }

    // 4. 跳转编辑交易页（带交易数据）
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable String id, Model model) {
        Transaction t = transactionService.listTransactionById(id);
        model.addAttribute("transaction", t);
        return "edit"; // 对应 edit.html
    }

    // 修改交易
    @PostMapping("/edit/{id}")
    public String updateTransaction(@PathVariable String id, @ModelAttribute Transaction updatedTransaction) {
        try {
            Transaction transaction = transactionService.updateTransaction(updatedTransaction);
            return "redirect:/";
        } catch (IllegalArgumentException e) {
            return "index";
        }
    }


    // 6. 删除交易
    @GetMapping("/delete/{id}")
    public String deleteTransaction(@PathVariable String id) {
        transactionService.deleteTransaction(id);
        return "redirect:/"; // 删除后跳转到列表页
    }


    // 查询所有交易
    @GetMapping("/")
    public String listTransactionsByPage(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, Model model) {
        PageResponse<Transaction> response = transactionService.listTransactionsByPage(page, size);
        model.addAttribute("transactions", response.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", response.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("totalItems", response.getTotalElements());

        return "index";
    }



}
