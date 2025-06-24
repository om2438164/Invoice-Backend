package com.invoice.mapper;

import com.invoice.dto.InvoiceDTO;
import com.invoice.model.Invoice;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class InvoiceMapper {
    
    public InvoiceDTO toDTO(Invoice invoice) {
        if (invoice == null) {
            return null;
        }

        InvoiceDTO dto = new InvoiceDTO();
        dto.setId(invoice.getId());
        dto.setInvoiceId(invoice.getInvoiceId());
        dto.setProductName(invoice.getProductName());
        dto.setDueDate(invoice.getDueDate());
        dto.setIssuedDate(invoice.getIssuedDate());
        dto.setProductPrice(invoice.getProductPrice());
        dto.setQuantity(invoice.getQuantity());
        dto.setStatus(invoice.getStatus());
        
        if (invoice.getUser() != null) {
            dto.setUserId(invoice.getUser().getId());
            dto.setUserName(invoice.getUser().getUsername());
        }
        
        // Calculate total amount
        if (invoice.getProductPrice() != null && invoice.getQuantity() != null) {
            dto.setTotalAmount(invoice.getProductPrice().multiply(BigDecimal.valueOf(invoice.getQuantity())));
        }
        
        return dto;
    }

    public void updateEntityFromDTO(InvoiceDTO dto, Invoice invoice) {
        if (dto == null || invoice == null) {
            return;
        }

        invoice.setProductName(dto.getProductName());
        invoice.setProductPrice(dto.getProductPrice());
        invoice.setQuantity(dto.getQuantity());
        invoice.setDueDate(dto.getDueDate());
        invoice.setIssuedDate(dto.getIssuedDate());
        invoice.setStatus(dto.getStatus());
    }
} 