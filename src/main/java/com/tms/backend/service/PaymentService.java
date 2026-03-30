package com.tms.backend.service;

import com.tms.backend.dto.request.PaymentRequest;
import com.tms.backend.dto.response.PaymentResponse;
import com.tms.backend.exception.BadRequestException;
import com.tms.backend.exception.ResourceNotFoundException;
import com.tms.backend.model.entity.Booking;
import com.tms.backend.model.entity.Payment;
import com.tms.backend.model.enums.BookingStatus;
import com.tms.backend.model.enums.PaymentStatus;
import com.tms.backend.repository.BookingRepository;
import com.tms.backend.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final BookingService bookingService;

    @Transactional
    public PaymentResponse initiatePayment(PaymentRequest request, String userEmail) {
        Booking booking = bookingRepository.findByIdWithDetails(request.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking", request.getBookingId()));

        if (!booking.getUser().getEmail().equals(userEmail)) {
            throw new BadRequestException("This booking does not belong to you");
        }

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new BadRequestException("Payment can only be initiated for PENDING bookings");
        }

        if (paymentRepository.findByBookingId(booking.getId()).isPresent()) {
            throw new BadRequestException("Payment already initiated for this booking");
        }

        String transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();

        Payment payment = Payment.builder()
                .booking(booking)
                .transactionId(transactionId)
                .amount(booking.getTotalAmount())
                .status(PaymentStatus.PENDING)
                .build();

        paymentRepository.save(payment);
        return mapToResponse(payment);
    }

    @Transactional
    public PaymentResponse processWebhook(Map<String, String> webhookPayload) {
        String transactionId = webhookPayload.get("transactionId");
        String status = webhookPayload.get("status");

        if (transactionId == null || status == null) {
            throw new BadRequestException("transactionId and status are required in webhook payload");
        }

        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for transaction: " + transactionId));

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new BadRequestException("Payment has already been processed (status: " + payment.getStatus() + ")");
        }

        if ("SUCCESS".equalsIgnoreCase(status)) {
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setPaidAt(LocalDateTime.now());
            bookingService.confirmBooking(payment.getBooking().getId(),
                    payment.getBooking().getUser().getEmail());
        } else {
            payment.setStatus(PaymentStatus.FAILED);
        }

        paymentRepository.save(payment);
        return mapToResponse(payment);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByBookingId(Long bookingId, String userEmail) {
        Payment payment = paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for booking: " + bookingId));

        if (!payment.getBooking().getUser().getEmail().equals(userEmail)) {
            throw new BadRequestException("This payment does not belong to you");
        }

        return mapToResponse(payment);
    }

    private PaymentResponse mapToResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .bookingId(payment.getBooking().getId())
                .transactionId(payment.getTransactionId())
                .amount(payment.getAmount())
                .status(payment.getStatus().name())
                .paidAt(payment.getPaidAt())
                .build();
    }
}
