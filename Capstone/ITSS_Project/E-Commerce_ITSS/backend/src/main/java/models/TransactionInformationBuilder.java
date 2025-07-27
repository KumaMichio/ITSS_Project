package models;

import java.time.LocalDateTime;

public class TransactionInformationBuilder {
    private int transactionId;
    private Order order;
    private double totalFee;
    private String status;
    private LocalDateTime transactionTime;
    private String content;
    private String paymentMethod;
    private String vnpTransactionNo;
    private String vnpBankCode;
    private String vnpBankTranNo;
    private String vnpResponseCode;
    private String orderReference;

    public TransactionInformationBuilder transactionId(int transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    public TransactionInformationBuilder order(Order order) {
        this.order = order;
        return this;
    }

    public TransactionInformationBuilder totalFee(double totalFee) {
        this.totalFee = totalFee;
        return this;
    }

    public TransactionInformationBuilder status(String status) {
        this.status = status;
        return this;
    }

    public TransactionInformationBuilder transactionTime(LocalDateTime transactionTime) {
        this.transactionTime = transactionTime;
        return this;
    }

    public TransactionInformationBuilder content(String content) {
        this.content = content;
        return this;
    }

    public TransactionInformationBuilder paymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
        return this;
    }

    public TransactionInformationBuilder vnpTransactionNo(String vnpTransactionNo) {
        this.vnpTransactionNo = vnpTransactionNo;
        return this;
    }

    public TransactionInformationBuilder vnpBankCode(String vnpBankCode) {
        this.vnpBankCode = vnpBankCode;
        return this;
    }

    public TransactionInformationBuilder vnpBankTranNo(String vnpBankTranNo) {
        this.vnpBankTranNo = vnpBankTranNo;
        return this;
    }

    public TransactionInformationBuilder vnpResponseCode(String vnpResponseCode) {
        this.vnpResponseCode = vnpResponseCode;
        return this;
    }

    public TransactionInformationBuilder orderReference(String orderReference) {
        this.orderReference = orderReference;
        return this;
    }

    public TransactionInformation build() {
        TransactionInformation transaction = new TransactionInformation();
        transaction.setTransactionId(transactionId);
        transaction.setOrder(order);
        transaction.setTotalFee(totalFee);
        transaction.setStatus(status);
        transaction.setTransactionTime(transactionTime);
        transaction.setContent(content);
        transaction.setPaymentMethod(paymentMethod);
        transaction.setVnpTransactionNo(vnpTransactionNo);
        transaction.setVnpBankCode(vnpBankCode);
        transaction.setVnpBankTranNo(vnpBankTranNo);
        transaction.setVnpResponseCode(vnpResponseCode);
        transaction.setOrderReference(orderReference);
        return transaction;
    }
}
