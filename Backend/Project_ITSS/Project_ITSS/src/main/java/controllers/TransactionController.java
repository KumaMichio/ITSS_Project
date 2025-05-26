package controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @GetMapping("/order/{orderId}")
    public ResponseObject<List<TransactionInfo>> getTransactionsByOrderId(@PathVariable int orderId) {
        List<TransactionInfo> transactions = transactionService.getTransactionsByOrderId(orderId);
        if (transactions != null && !transactions.isEmpty()) {
            return new ResponseObject<>(HttpStatus.OK, "Success", transactions);
        } else {
            return new ResponseObject<>(HttpStatus.NOT_FOUND, "No transactions found for orderId: " + orderId, null);
        }
    }
}
