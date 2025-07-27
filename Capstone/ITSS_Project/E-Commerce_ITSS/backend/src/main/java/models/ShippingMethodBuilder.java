package models;

import java.util.List;
import java.util.ArrayList;

public class ShippingMethodBuilder {
    private int methodID;
    private String methodName;
    private boolean isRush;
    private double shippingFees;
    private List<Order> orders = new ArrayList<>();

    public ShippingMethodBuilder methodID(int methodID) {
        this.methodID = methodID;
        return this;
    }

    public ShippingMethodBuilder methodName(String methodName) {
        this.methodName = methodName;
        return this;
    }

    public ShippingMethodBuilder isRush(boolean isRush) {
        this.isRush = isRush;
        return this;
    }

    public ShippingMethodBuilder shippingFees(double shippingFees) {
        this.shippingFees = shippingFees;
        return this;
    }

    public ShippingMethodBuilder orders(List<Order> orders) {
        this.orders = orders;
        return this;
    }

    public ShippingMethod build() {
        ShippingMethod shippingMethod = new ShippingMethod();
        shippingMethod.setMethodID(methodID);
        shippingMethod.setMethodName(methodName);
        shippingMethod.setRush(isRush);
        shippingMethod.setShippingFees(shippingFees);
        shippingMethod.setOrders(orders);
        return shippingMethod;
    }
}
