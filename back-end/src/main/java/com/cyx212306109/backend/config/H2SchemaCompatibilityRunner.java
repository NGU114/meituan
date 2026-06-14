package com.cyx212306109.backend.config;

import com.cyx212306109.backend.enums.OrderStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class H2SchemaCompatibilityRunner implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;
    private final String datasourceUrl;

    public H2SchemaCompatibilityRunner(JdbcTemplate jdbcTemplate,
                                       @Value("${spring.datasource.url:}") String datasourceUrl) {
        this.jdbcTemplate = jdbcTemplate;
        this.datasourceUrl = datasourceUrl;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!datasourceUrl.startsWith("jdbc:h2:")) {
            return;
        }
        repairOrderStatusEnumColumns();
    }

    private void repairOrderStatusEnumColumns() {
        alterEnumColumnIfExists("status", true);
        alterEnumColumnIfExists("refund_previous_status", false);
    }

    private void alterEnumColumnIfExists(String columnName, boolean required) {
        Integer count = jdbcTemplate.queryForObject("""
                        SELECT COUNT(*)
                        FROM information_schema.columns
                        WHERE LOWER(table_name) = 'orders'
                          AND LOWER(column_name) = ?
                        """,
                Integer.class,
                columnName);
        if (count == null || count == 0) {
            return;
        }
        jdbcTemplate.execute("""
                ALTER TABLE orders ALTER COLUMN %s ENUM(%s)%s
                """.formatted(columnName, orderStatusValues(), required ? " NOT NULL" : ""));
    }

    private String orderStatusValues() {
        return Arrays.stream(OrderStatus.values())
                .map(status -> "'" + status.name() + "'")
                .reduce((left, right) -> left + ", " + right)
                .orElseThrow();
    }
}
