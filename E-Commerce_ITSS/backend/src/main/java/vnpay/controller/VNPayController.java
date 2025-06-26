package vnpay.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vnpay.dto.VNPayRequest;
import vnpay.dto.VNPayResponse;
import vnpay.service.VNPayService;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * VNPay Payment Controller
 */
@RestController
@RequestMapping("/api/vnpay")
@CrossOrigin(origins = "*")
public class VNPayController {

    @Autowired
    private VNPayService vnPayService;

    /**
     * Create VNPay payment URL
     */
    @PostMapping("/create-payment")
    public ResponseEntity<VNPayResponse> createPayment(
            @RequestBody VNPayRequest request,
            HttpServletRequest httpRequest) {

        VNPayResponse response = vnPayService.createPaymentUrl(request, httpRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Handle VNPay payment return (callback)
     */
    @GetMapping("/payment-return")
    public ResponseEntity<Map<String, Object>> paymentReturn(
            @RequestParam Map<String, String> params) {

        boolean isValid = vnPayService.validateResponse(params);

        Map<String, Object> response = Map.of(
                "valid", isValid,
                "params", params);

        return ResponseEntity.ok(response);
    }

    /**
     * Handle VNPay IPN (Instant Payment Notification)
     */
    @PostMapping("/payment-ipn")
    public ResponseEntity<Map<String, String>> paymentIPN(
            @RequestParam Map<String, String> params) {

        boolean isValid = vnPayService.validateResponse(params);

        if (isValid) {
            // Process the payment result
            String vnp_ResponseCode = params.get("vnp_ResponseCode");

            if ("00".equals(vnp_ResponseCode)) {
                // Payment successful
                // Update order status in database
                return ResponseEntity.ok(Map.of("RspCode", "00", "Message", "Confirm Success"));
            } else {
                // Payment failed
                return ResponseEntity.ok(Map.of("RspCode", "01", "Message", "Payment Failed"));
            }
        } else {
            return ResponseEntity.ok(Map.of("RspCode", "97", "Message", "Invalid Signature"));
        }
    }
}
