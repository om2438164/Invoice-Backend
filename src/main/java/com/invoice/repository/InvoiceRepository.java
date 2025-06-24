package com.invoice.repository;

import com.invoice.model.Invoice;
import com.invoice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByUser(User user);
    boolean existsByInvoiceId(String invoiceId);
} 