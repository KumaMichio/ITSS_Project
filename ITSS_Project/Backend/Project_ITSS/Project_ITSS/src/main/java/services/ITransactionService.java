package services;

import models.TransactionInformation;

import java.util.List;

public interface ITransactionService {
    public void createTransaction(int orderId, String status);
    List<TransactionInformation> getTransactionsByOrderId(int orderId);
}
