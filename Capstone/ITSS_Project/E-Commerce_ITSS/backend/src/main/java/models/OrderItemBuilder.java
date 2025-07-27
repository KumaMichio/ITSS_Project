package models;

public class OrderItemBuilder {
    private int id;
    private int productId;
    private int quantity;
    private double price;
    private Order order;

    public OrderItemBuilder id(int id) {
        this.id = id;
        return this;
    }

    public OrderItemBuilder productId(int productId) {
        this.productId = productId;
        return this;
    }

    public OrderItemBuilder quantity(int quantity) {
        this.quantity = quantity;
        return this;
    }

    public OrderItemBuilder price(double price) {
        this.price = price;
        return this;
    }

    public OrderItemBuilder order(Order order) {
        this.order = order;
        return this;
    }

    public OrderItem build() {
        OrderItem orderItem = new OrderItem();
        orderItem.setId(id);
        orderItem.setProductId(productId);
        orderItem.setQuantity(quantity);
        orderItem.setPrice(price);
        orderItem.setOrder(order);
        return orderItem;
    }
}
