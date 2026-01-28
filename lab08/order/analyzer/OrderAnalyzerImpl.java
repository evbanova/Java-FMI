package bg.sofia.uni.fmi.mjt.order.analyzer;

import bg.sofia.uni.fmi.mjt.order.domain.Category;
import bg.sofia.uni.fmi.mjt.order.domain.Order;
import bg.sofia.uni.fmi.mjt.order.domain.PaymentMethod;
import bg.sofia.uni.fmi.mjt.order.domain.Status;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.Collections;
import java.util.stream.Collectors;

public class OrderAnalyzerImpl implements OrderAnalyzer {

    private final List<Order> orders;

    public OrderAnalyzerImpl(List<Order> orders) {
        if (orders == null) {
            throw new IllegalArgumentException("orders is null");
        }

        this.orders = orders;
    }

    /**
     * Returns an immutable copy of all orders.
     *
     * @return the list of all orders
     */
    public List<Order> allOrders() {
        return List.copyOf(orders);
    }

    /**
     * Returns an immutable list of all orders placed by the given customer.
     *
     * @param customer the customer name (case-sensitive) to filter by
     * @return a list of orders made by the specified customer,
     * or an empty list if none exist
     * @throws IllegalArgumentException if customer is null or blank
     */
    public List<Order> ordersByCustomer(String customer) {
        if (customer == null) {
            throw new IllegalArgumentException("customer is null");
        }

        List<Order> ordersByCustomer = new ArrayList<>();
        for (Order order : orders) {
            if (order.customerName().equals(customer)) {
                ordersByCustomer.add(order);
            }
        }

        return List.copyOf(ordersByCustomer);
    }

    /**
     * Returns the date on which the most orders were placed and their count.
     * In case of a tie, return the earliest of the dates with equal number of orders.
     *
     * @return the date with the highest number of orders and the count of the orders,
     * or null if there are no orders
     */
    public Map.Entry<LocalDate, Long> dateWithMostOrders() {
        if (orders.isEmpty()) {
            return null;
        }

        return orders.stream()
                .collect(Collectors.groupingBy(
                        Order::date,
                        Collectors.counting()
                )).entrySet().stream()
                .max(Map.Entry.<LocalDate, Long>comparingByValue()
                        .thenComparing(Map.Entry.comparingByKey(Comparator.reverseOrder())))
                .orElse(null);
    }

    /**
     * Returns the top N most frequently ordered products, where frequency is
     * the number of orders containing the product (not the sum of ordered quantities).
     * If two products have the same number of orders, sort them alphabetically.
     *
     * @param n the number of products to return. If n is 0, returns an empty list.
     *          If n exceeds the number of distinct products, returns all of them.
     * @return a list of product names ordered by frequency
     * @throws IllegalArgumentException if n < 0
     */
    public List<String> topNMostOrderedProducts(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("n is negative");
        } else if (n == 0) {
            return Collections.emptyList();
        }

        return orders.stream()
                .collect(Collectors.groupingBy(
                        Order::product,
                        Collectors.counting()
                )).entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry<String, Long>::getValue)
                        .reversed().thenComparing(Map.Entry::getKey))
                .limit(n).map(Map.Entry::getKey).toList();
    }

    /**
     * Computes the total revenue for each {@link Category} present in the dataset.
     * Revenue is defined as the sum of {@link Order#totalSales()} for all orders
     * in that category.
     *
     * @return a map from category to total revenue
     */
    public Map<Category, Double> revenueByCategory() {
        if (orders.isEmpty()) {
            return null;
        }

        return orders.stream()
                .collect(Collectors.groupingBy(
                        Order::category,
                        Collectors.summingDouble(Order::totalSales)));
    }

    /**
     * Identifies customers whose ordering behavior is suspicious.
     * A customer is suspicious if they have more than 3 orders that are both:
     * <ul>
     *   <li>with status {@link Status#CANCELLED}</li>
     *   <li>with total sales value < 100.0</li>
     * </ul>
     *
     * @return a set of suspicious customer names
     */
    public Set<String> suspiciousCustomers() {
        if (orders.isEmpty()) {
            return null;
        }

        final double suspiciousTotalValue = 100.0;
        final int suspiciousOrdersCount = 3;

        return orders.stream()
                .filter(order -> order.status() == Status.CANCELLED)
                .filter(order -> order.totalSales() < suspiciousTotalValue)
                .collect(Collectors.groupingBy(
                        Order::customerName,
                        Collectors.counting()))
                .entrySet().stream()
                .filter(entry -> entry.getValue() > suspiciousOrdersCount)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    /**
     * Determines the most frequently used {@link PaymentMethod}
     * for each product category in the dataset. In case the dataset is empty,
     * returns an empty Map.
     * In case of a tie for a category, return the {@link PaymentMethod}
     * whose name comes first alphabetically.
     *
     * @return a map from category to its most frequently used payment method
     */
    public Map<Category, PaymentMethod> mostUsedPaymentMethodForCategory() {
        if (orders.isEmpty()) {
            return new HashMap<>();
        }
        return orders.stream().collect(Collectors.groupingBy(Order::category,
               Collectors.collectingAndThen(Collectors.groupingBy(Order::paymentMethod,
                     Collectors.counting()),
                     frequencyMap -> frequencyMap.entrySet()
                             .stream().max(Map.Entry.<PaymentMethod, Long>comparingByValue()
                             .thenComparing(Map.Entry.comparingByKey(Comparator.comparing(PaymentMethod::name)
                                     .reversed())))
               .map(Map.Entry::getKey).orElse(null))));
    }

    /**
     * Determines the customer location that has the most orders.
     * If multiple locations tie, return the one that is alphabetically smallest.
     *
     * @return the location with the most orders, or null if there are no orders
     */
    public String locationWithMostOrders() {
        if (orders.isEmpty()) {
            return null;
        }

        return orders.stream().collect(Collectors.groupingBy(Order::customerLocation,
                Collectors.counting())).entrySet().stream()
                .max(Comparator.comparing(Map.Entry<String, Long>::getValue)
                .thenComparing(Map.Entry.comparingByKey(Comparator.reverseOrder())))
                        .map(Map.Entry::getKey).orElse(null);
    }

    /**
     * Groups orders first by {@link Category}, and within each category
     * by {@link Status}, counting how many orders fall into each group.
     * In case the dataset is empty, returns an empty Map.
     *
     * @return a map where each category maps to another map
     * from status to the count of orders with that status
     */
    public Map<Category, Map<Status, Long>> groupByCategoryAndStatus() {
        if (orders.isEmpty()) {
            return new HashMap<>();
        }

        return orders.stream().collect(Collectors.groupingBy(Order::category,
                        Collectors.groupingBy(Order::status, Collectors.counting())));
    }
}
