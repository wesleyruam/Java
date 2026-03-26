package com.nexusbank.repository;

import com.nexusbank.entity.Transaction;
import com.nexusbank.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    Optional<Transaction> findByReferenceId(String referenceId);

    boolean existsByReferenceId(String referenceId);

    @Query("""
        SELECT t FROM Transaction t
        WHERE (t.sourceAccount.id = :accountId OR t.destinationAccount.id = :accountId)
        AND (:startDate IS NULL OR t.createdAt >= :startDate)
        AND (:endDate IS NULL OR t.createdAt <= :endDate)
        AND (:type IS NULL OR t.type = :type)
        ORDER BY t.createdAt DESC
        """)
    Page<Transaction> findStatement(
            @Param("accountId") UUID accountId,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate,
            @Param("type") TransactionType type,
            Pageable pageable
    );
}
