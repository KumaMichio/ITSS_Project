package services;

import models.TransactionInformation;

import java.util.List;

public interface ITransactionService {
    public void createTransaction(int orderId, String status);

    public void createTransaction(int orderId, String status, String vnpTransactionNo, String vnpBankCode,
            String vnpBankTranNo, String vnpResponseCode);

    List<TransactionInformation> getTransactionsByOrderId(int orderId);

    List<TransactionInformation> getAllTransactions();

    TransactionInformation getTransactionById(int id);

    boolean updateTransactionStatus(int id, String status);
}
