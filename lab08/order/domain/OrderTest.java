package bg.sofia.uni.fmi.mjt.order.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class OrderTest {

    @Test
    void testOrderCreationFromValidLine() {
        String line = "1,2023-11-20,Laptop,ELECTRONICS,1200.50,1,1200.50,John Doe,New York,CREDIT_CARD,COMPLETED";

        Order order = Order.of(line);

        assertNotNull(order, "Order should not be null");
        assertEquals("1", order.id());
        assertEquals(LocalDate.of(2023, 11, 20), order.date());
        assertEquals("Laptop", order.product());
        assertEquals(Category.ELECTRONICS, order.category());
        assertEquals(1200.50, order.price());
        assertEquals(1, order.quantity());
        assertEquals(1200.50, order.totalSales());
        assertEquals("John Doe", order.customerName());
        assertEquals("New York", order.customerLocation());
        assertEquals(PaymentMethod.CREDIT_CARD, order.paymentMethod());
        assertEquals(Status.COMPLETED, order.status());
    }

    @Test
    void testOrderCreationWithDifferentData() {
        String line = "2,2023-12-01,T-Shirt,CLOTHING,20.00,2,40.00,Jane Smith,London,PAYPAL,PENDING";

        Order order = Order.of(line);

        assertEquals(Category.CLOTHING, order.category());
        assertEquals(Status.PENDING, order.status());
        assertEquals(PaymentMethod.PAYPAL, order.paymentMethod());
    }
}
