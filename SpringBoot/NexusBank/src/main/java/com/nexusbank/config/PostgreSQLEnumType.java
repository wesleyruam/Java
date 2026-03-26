package com.nexusbank.config;

/**
 * PostgreSQL ENUM Type Configuration
 * 
 * Note: With modern Hibernate and Spring Boot, @Enumerated(EnumType.STRING) 
 * handles enum conversion automatically. This class is kept for reference.
 * 
 * To use with PostgreSQL native enums, configure in application.yml:
 * spring:
 *   jpa:
 *     database-platform: org.hibernate.dialect.PostgreSQL10Dialect
 *     hibernate:
 *       ddl-auto: validate
 * 
 * Create PostgreSQL enums:
 *   CREATE TYPE user_status AS ENUM ('ACTIVE', 'INACTIVE', 'SUSPENDED', 'DELETED');
 *   CREATE TYPE user_role AS ENUM ('ROLE_USER', 'ROLE_ADMIN', 'ROLE_SUPPORT');
 *   CREATE TYPE account_type AS ENUM ('CHECKING', 'SAVINGS', 'INVESTMENT', 'CREDIT');
 *   CREATE TYPE transaction_type AS ENUM ('TRANSFER', 'DEPOSIT', 'WITHDRAWAL', 'PAYMENT', 'REFUND', 'FEE');
 *   CREATE TYPE transaction_status AS ENUM ('PENDING', 'COMPLETED', 'FAILED', 'CANCELLED', 'REVERSED');
 *   CREATE TYPE bill_status AS ENUM ('PENDING', 'PAID', 'FAILED', 'CANCELLED', 'OVERDUE');
 */
public final class PostgreSQLEnumType {
    private PostgreSQLEnumType() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
