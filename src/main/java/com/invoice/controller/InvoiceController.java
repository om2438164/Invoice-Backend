package com.invoice.controller;

import com.invoice.dto.InvoiceDTO;
import com.invoice.service.InvoiceService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/invoices")
@CrossOrigin(origins = "http://localhost:3000")
public class InvoiceController {
    private static final Logger logger = LoggerFactory.getLogger(InvoiceController.class);

    @Autowired
    private InvoiceService invoiceService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createInvoice(@Valid @RequestBody InvoiceDTO invoiceDTO, Authentication authentication) {
        try {
            logger.info("Creating invoice for user: {}", authentication.getName());
            InvoiceDTO created = invoiceService.createInvoice(invoiceDTO, authentication.getName());
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            logger.error("Error creating invoice: ", e);
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getUserInvoices(Authentication authentication) {
        try {
            logger.info("Fetching invoices for user: {}", authentication.getName());
            List<InvoiceDTO> invoices = invoiceService.getInvoicesByUser(authentication.getName());
            return ResponseEntity.ok(invoices);
        } catch (Exception e) {
            logger.error("Error fetching invoices: ", e);
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateInvoice(
            @PathVariable Long id,
            @Valid @RequestBody InvoiceDTO invoiceDTO,
            Authentication authentication) {
        try {
            logger.info("Updating invoice {} for user: {}", id, authentication.getName());
            InvoiceDTO updated = invoiceService.updateInvoice(id, invoiceDTO);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            logger.error("Error updating invoice {}: ", id, e);
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteInvoice(@PathVariable Long id, Authentication authentication) {
        try {
            logger.info("Deleting invoice {} for user: {}", id, authentication.getName());
            invoiceService.deleteInvoice(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Invoice deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error deleting invoice {}: ", id, e);
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
} 