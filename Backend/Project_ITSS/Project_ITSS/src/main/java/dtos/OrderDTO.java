package dtos;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import models.DeliveryInformation;
import models.Product;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    private int id; // mapping với orderId

    private double shippingFees;
    private double totalAmount;
    private LocalDateTime createdAt;
    private int VAT;
    private double totalFee;
    private boolean isPayment;

    private DeliveryInformation delivery; // mapping với deliveryInformation
    private List<Product> products;
}