package com.invoice.service;

import com.invoice.dto.InvoiceDTO;
import com.invoice.mapper.InvoiceMapper;
import com.invoice.model.Invoice;
import com.invoice.model.User;
import com.invoice.repository.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvoiceService {
    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private InvoiceMapper invoiceMapper;

    @Transactional
    public InvoiceDTO createInvoice(InvoiceDTO dto, String username) {
        User user = userService.getUserByUsername(username);
        
        Invoice invoice = new Invoice();
        invoiceMapper.updateEntityFromDTO(dto, invoice);
        invoice.setUser(user);
        invoice.setInvoiceId(generateInvoiceId(user));
        
        user.addInvoice(invoice);
        Invoice savedInvoice = invoiceRepository.save(invoice);
        return invoiceMapper.toDTO(savedInvoice);
    }

    private String generateInvoiceId(User user) {
        long count = invoiceRepository.count() + 1;
        return String.format("INV-%s-%04d", user.getUsername().toUpperCase(), count);
    }

    @Transactional(readOnly = true)
    public List<InvoiceDTO> getAllInvoices() {
        return invoiceRepository.findAll().stream()
            .map(invoiceMapper::toDTO)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public InvoiceDTO getInvoiceById(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Invoice not found"));
        return invoiceMapper.toDTO(invoice);
    }

    @Transactional(readOnly = true)
    public List<InvoiceDTO> getInvoicesByUser(String username) {
        User user = userService.getUserByUsername(username);
        return invoiceRepository.findByUser(user).stream()
            .map(invoiceMapper::toDTO)
            .collect(Collectors.toList());
    }

    @Transactional
    public InvoiceDTO updateInvoice(Long id, InvoiceDTO dto) {
        Invoice existingInvoice = invoiceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Invoice not found"));

        invoiceMapper.updateEntityFromDTO(dto, existingInvoice);
        Invoice updatedInvoice = invoiceRepository.save(existingInvoice);
        return invoiceMapper.toDTO(updatedInvoice);
    }

    @Transactional
    public void deleteInvoice(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Invoice not found"));
        User user = invoice.getUser();
        user.removeInvoice(invoice);
        invoiceRepository.delete(invoice);
    }
} 