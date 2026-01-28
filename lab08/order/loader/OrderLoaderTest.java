package bg.sofia.uni.fmi.mjt.order.loader;

import bg.sofia.uni.fmi.mjt.order.domain.Order;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderLoaderTest {

    @Test
    void testLoadReadsOrdersCorrectly() {
        String csvData = """
                1,2023-01-01,ProductA,ELECTRONICS,10.0,1,10.0,UserA,CityA,CREDIT_CARD,COMPLETED
                2,2023-01-02,ProductB,BOOKS,20.0,2,40.0,UserB,CityB,PAYPAL,PENDING
                """;
        Reader reader = new StringReader(csvData);

        List<Order> orders = OrderLoader.load(reader);

        assertEquals(2, orders.size(), "Should load 2 orders");
        assertEquals("1", orders.get(0).id());
        assertEquals("2", orders.get(1).id());
    }

    @Test
    void testLoadWithEmptyInput() {
        Reader reader = new StringReader("");
        List<Order> orders = OrderLoader.load(reader);
        assertTrue(orders.isEmpty(), "Order list should be empty for empty input");
    }

    @Test
    void testLoadThrowsExceptionForNullReader() {
        assertThrows(IllegalArgumentException.class, () -> OrderLoader.load(null));
    }
}
