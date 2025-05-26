package controllers;

import lombok.RequiredArgsConstructor;
import models.TransactionInformation;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import response.ResponseObject;
import services.TransactionService;

import java.util.List;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @GetMapping("/order/{orderId}")
    public ResponseObject<List<TransactionInformation>> getTransactionsByOrderId(@PathVariable int orderId) {
        List<TransactionInformation> transactions = transactionService.getTransactionsByOrderId(orderId);
        if (transactions != null && !transactions.isEmpty()) {
            return new ResponseObject<>(HttpStatus.OK, "Success", transactions);
        } else {
            return new ResponseObject<>(HttpStatus.NOT_FOUND, "No transactions found for orderId: " + orderId, null);
        }
    }
}
