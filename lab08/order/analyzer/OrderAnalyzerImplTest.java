package bg.sofia.uni.fmi.mjt.order.analyzer;

import bg.sofia.uni.fmi.mjt.order.domain.Category;
import bg.sofia.uni.fmi.mjt.order.domain.Order;
import bg.sofia.uni.fmi.mjt.order.domain.PaymentMethod;
import bg.sofia.uni.fmi.mjt.order.domain.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class OrderAnalyzerImplTest {

    private OrderAnalyzer analyzer;
    private List<Order> orders;

    @BeforeEach
    void setUp() {
        orders = List.of(
                createOrder("1", "2023-01-01", "P1", Category.ELECTRONICS, 10.0, "John", Status.CANCELLED),
                createOrder("2", "2023-01-01", "P1", Category.ELECTRONICS, 10.0, "John", Status.CANCELLED),
                createOrder("3", "2023-01-01", "P1", Category.ELECTRONICS, 10.0, "John", Status.CANCELLED),
                createOrder("4", "2023-01-01", "P1", Category.ELECTRONICS, 10.0, "John", Status.CANCELLED),
                createOrder("5", "2023-01-02", "P2", Category.BOOKS, 200.0, "Mike", Status.CANCELLED),
                createOrder("6", "2023-01-05", "Apple", Category.CLOTHING, 50.0, "Alice", Status.COMPLETED),
                createOrder("7", "2023-01-05", "Apple", Category.CLOTHING, 50.0, "Alice", Status.COMPLETED),
                createOrder("8", "2023-01-06", "Apple", Category.CLOTHING, 50.0, "Bob", Status.COMPLETED),
                createOrder("9", "2023-01-07", "Carrot", Category.CLOTHING, 50.0, "Bob", Status.COMPLETED),
                createOrder("10", "2023-01-07", "Carrot", Category.CLOTHING, 50.0, "Bob", Status.COMPLETED),
                createOrder("11", "2023-01-07", "Carrot", Category.CLOTHING, 50.0, "Bob", Status.COMPLETED),
                createOrder("12", "2023-01-08", "Banana", Category.FOOTWEAR, 50.0, "Charlie", Status.COMPLETED),
                createOrder("13", "2023-01-08", "Banana", Category.FOOTWEAR, 50.0, "Charlie", Status.COMPLETED)
        );

        analyzer = new OrderAnalyzerImpl(orders);
    }

    private Order createOrder(String id, String date, String product, Category cat, double val, String name, Status status) {
        return new Order(id, LocalDate.parse(date), product, cat, val, 1, val, name, "Location", PaymentMethod.CREDIT_CARD, status);
    }

    @Test
    void testAllOrdersReturnsImmutableCopy() {
        List<Order> all = analyzer.allOrders();
        assertEquals(orders.size(), all.size());
        assertThrows(UnsupportedOperationException.class, () -> all.add(orders.get(0)));
    }

    @Test
    void testOrdersByCustomer() {
        List<Order> johnsOrders = analyzer.ordersByCustomer("John");
        assertEquals(4, johnsOrders.size());
        assertEquals("John", johnsOrders.get(0).customerName());

        List<Order> empty = analyzer.ordersByCustomer("Nobody");
        assertTrue(empty.isEmpty());
    }

    @Test
    void testOrdersByCustomerThrowsNull() {
        assertThrows(IllegalArgumentException.class, () -> analyzer.ordersByCustomer(null));
    }

    @Test
    void testTopNMostOrderedProducts() {
        List<String> top3 = analyzer.topNMostOrderedProducts(3);

        assertEquals(3, top3.size());
        assertEquals("P1", top3.get(0));
        assertEquals("Apple", top3.get(1));
        assertEquals("Carrot", top3.get(2));
    }

    @Test
    void testTopNMostOrderedProductsWithLargeN() {
        List<String> result = analyzer.topNMostOrderedProducts(100);
        assertEquals(5, result.size());
    }

    @Test
    void testSuspiciousCustomers() {
        Set<String> suspicious = analyzer.suspiciousCustomers();

        assertEquals(1, suspicious.size());
        assertTrue(suspicious.contains("John"), "John has 4 cancelled orders < 100.0");
    }

    @Test
    void testRevenueByCategory() {
        Map<Category, Double> revenue = analyzer.revenueByCategory();

        assertEquals(300.0, revenue.get(Category.CLOTHING));
        assertEquals(100.0, revenue.get(Category.FOOTWEAR));
    }

    @Test
    void testDateWithMostOrders() {
        Map.Entry<LocalDate, Long> entry = analyzer.dateWithMostOrders();

        assertNotNull(entry);
        assertEquals(LocalDate.parse("2023-01-01"), entry.getKey());
        assertEquals(4L, entry.getValue());
    }

    @Test
    void testDateWithMostOrdersTieBreaker() {
        List<Order> tieOrders = List.of(
                createOrder("1", "2023-01-05", "A", Category.BOOKS, 10, "A", Status.COMPLETED),
                createOrder("2", "2023-01-01", "A", Category.BOOKS, 10, "A", Status.COMPLETED)
        );
        OrderAnalyzer localAnalyzer = new OrderAnalyzerImpl(tieOrders);

        Map.Entry<LocalDate, Long> result = localAnalyzer.dateWithMostOrders();
        assertEquals(LocalDate.parse("2023-01-01"), result.getKey(),
                "Should return earliest date in case of tie");
    }

    @Test
    void testGroupByCategoryAndStatus() {
        Map<Category, Map<Status, Long>> result = analyzer.groupByCategoryAndStatus();

        assertEquals(6L, result.get(Category.CLOTHING).get(Status.COMPLETED));
        assertEquals(4L, result.get(Category.ELECTRONICS).get(Status.CANCELLED));
        assertNull(result.get(Category.CLOTHING).get(Status.PENDING));
    }

    @Test
    void testMostUsedPaymentMethodForCategory() {
        List<Order> specificOrders = List.of(
                new Order("1", LocalDate.now(), "A", Category.BOOKS, 10, 1, 10, "A", "Loc", PaymentMethod.PAYPAL, Status.COMPLETED),
                new Order("2", LocalDate.now(), "A", Category.BOOKS, 10, 1, 10, "A", "Loc", PaymentMethod.PAYPAL, Status.COMPLETED),
                new Order("3", LocalDate.now(), "A", Category.BOOKS, 10, 1, 10, "A", "Loc", PaymentMethod.CREDIT_CARD, Status.COMPLETED)
        );
        OrderAnalyzer localAnalyzer = new OrderAnalyzerImpl(specificOrders);

        Map<Category, PaymentMethod> result = localAnalyzer.mostUsedPaymentMethodForCategory();

        assertEquals(PaymentMethod.PAYPAL, result.get(Category.BOOKS));
    }
}