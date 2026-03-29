package com.tms.backend.controller;

import com.tms.backend.dto.request.PaymentRequest;
import com.tms.backend.dto.response.PaymentResponse;
import com.tms.backend.service.PaymentService;
import com.tms.backend.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Initiate payments, webhook callback, and status check")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/initiate")
    @Operation(summary = "Initiate payment", description = "Creates a PENDING payment for a booking, returns transaction ID")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Payment initiated"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid booking or already paid")
    })
    public ResponseEntity<ApiResponse<PaymentResponse>> initiatePayment(
            @Valid @RequestBody PaymentRequest request,
            Authentication authentication) {
        PaymentResponse response = paymentService.initiatePayment(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Payment initiated", response));
    }

    @PostMapping("/webhook")
    @Operation(summary = "Payment gateway webhook", description = "Simulated gateway callback — send { transactionId, status: SUCCESS|FAILED }")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Payment processed"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid payload")
    })
    public ResponseEntity<ApiResponse<PaymentResponse>> webhook(@RequestBody Map<String, String> payload) {
        PaymentResponse response = paymentService.processWebhook(payload);
        return ResponseEntity.ok(ApiResponse.success("Payment processed", response));
    }

    @GetMapping("/{bookingId}")
    @Operation(summary = "Get payment status", description = "Returns payment details for a booking")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Payment status retrieved"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Payment not found")
    })
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentStatus(
            @PathVariable Long bookingId,
            Authentication authentication) {
        PaymentResponse response = paymentService.getPaymentByBookingId(bookingId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Payment status retrieved", response));
    }
}
