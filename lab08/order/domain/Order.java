package bg.sofia.uni.fmi.mjt.order.domain;

import java.time.LocalDate;

public record Order(String id, LocalDate date, String product, Category category, double price,
                    int quantity, double totalSales, String customerName, String customerLocation,
                    PaymentMethod paymentMethod, Status status) {
    public static Order of(String line) {
        String[] fields = line.split(",");
        int index = 0;

        return new Order(fields[index++].trim(),
                LocalDate.parse(fields[index++].trim()),
                fields[index++].trim(),
                Category.valueOf(fields[index++].trim().toUpperCase()),
                Double.parseDouble(fields[index++].trim()),
                Integer.parseInt(fields[index++].trim()),
                Double.parseDouble(fields[index++].trim()),
                fields[index++].trim(),
                fields[index++].trim(),
                PaymentMethod.valueOf(fields[index++].trim().toUpperCase()),
                Status.valueOf(fields[index++].trim().toUpperCase()));
    }
}
