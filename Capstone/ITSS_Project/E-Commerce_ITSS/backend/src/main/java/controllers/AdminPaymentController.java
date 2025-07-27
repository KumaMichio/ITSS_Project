package controllers;

import lombok.RequiredArgsConstructor;
import models.TransactionInformation;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import services.PaymentManagementService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/payments")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AdminPaymentController {

    private final PaymentManagementService paymentManagementService;

    /**
     * Get all payments with pagination and sorting
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllPayments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "transactionTime") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search) {

        try {
            Page<TransactionInformation> paymentsPage;

            if (search != null && !search.trim().isEmpty()) {
                paymentsPage = paymentManagementService.searchPayments(search, page, size);
            } else if (status != null && !status.trim().isEmpty()) {
                paymentsPage = paymentManagementService.getPaymentsByStatus(status, page, size);
            } else {
                paymentsPage = paymentManagementService.getAllPayments(page, size, sortBy, sortDir);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("content", paymentsPage.getContent());
            response.put("currentPage", paymentsPage.getNumber());
            response.put("totalItems", paymentsPage.getTotalElements());
            response.put("totalPages", paymentsPage.getTotalPages());
            response.put("pageSize", paymentsPage.getSize());
            response.put("hasNext", paymentsPage.hasNext());
            response.put("hasPrevious", paymentsPage.hasPrevious());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi lấy danh sách thanh toán: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get payment statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getPaymentStatistics() {
        try {
            System.out.println("🔍 AdminPaymentController: Starting getPaymentStatistics...");

            Map<String, Object> statistics = paymentManagementService.getPaymentStatistics();

            System.out.println("✅ AdminPaymentController: Statistics retrieved successfully");
            System.out.println("Statistics data: " + statistics);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", statistics);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("❌ AdminPaymentController: Error in getPaymentStatistics: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi lấy thống kê thanh toán: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get payment details by transaction ID
     */
    @GetMapping("/{transactionId}")
    public ResponseEntity<Map<String, Object>> getPaymentDetails(@PathVariable int transactionId) {
        try {
            Optional<TransactionInformation> transaction = paymentManagementService.getPaymentById(transactionId);

            Map<String, Object> response = new HashMap<>();
            if (transaction.isPresent()) {
                response.put("success", true);
                response.put("data", transaction.get());
            } else {
                response.put("success", false);
                response.put("message", "Không tìm thấy giao dịch với ID: " + transactionId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi lấy chi tiết thanh toán: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get payment by order ID
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<Map<String, Object>> getPaymentByOrderId(@PathVariable int orderId) {
        try {
            Optional<TransactionInformation> transaction = paymentManagementService.getPaymentByOrderId(orderId);

            Map<String, Object> response = new HashMap<>();
            if (transaction.isPresent()) {
                response.put("success", true);
                response.put("data", transaction.get());
            } else {
                response.put("success", false);
                response.put("message", "Không tìm thấy giao dịch cho đơn hàng: " + orderId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi lấy thông tin thanh toán: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Process refund for a transaction
     */
    @PostMapping("/{transactionId}/refund")
    public ResponseEntity<Map<String, Object>> processRefund(
            @PathVariable int transactionId,
            @RequestBody Map<String, String> refundRequest) {

        try {
            String reason = refundRequest.getOrDefault("reason", "Hoàn tiền theo yêu cầu admin");
            String adminUser = refundRequest.getOrDefault("adminUser", "admin");

            boolean success = paymentManagementService.processRefund(transactionId, reason, adminUser);

            Map<String, Object> response = new HashMap<>();
            if (success) {
                response.put("success", true);
                response.put("message", "Hoàn tiền thành công cho giao dịch: " + transactionId);
            } else {
                response.put("success", false);
                response.put("message", "Không thể hoàn tiền cho giao dịch: " + transactionId);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi xử lý hoàn tiền: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Update transaction status
     */
    @PutMapping("/{transactionId}/status")
    public ResponseEntity<Map<String, Object>> updateTransactionStatus(
            @PathVariable int transactionId,
            @RequestBody Map<String, String> statusUpdate) {

        try {
            String newStatus = statusUpdate.get("status");
            String notes = statusUpdate.getOrDefault("notes", "");

            if (newStatus == null || newStatus.trim().isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Trạng thái mới không được để trống");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            boolean success = paymentManagementService.updateTransactionStatus(transactionId, newStatus.trim(), notes);

            Map<String, Object> response = new HashMap<>();
            if (success) {
                response.put("success", true);
                response.put("message", "Cập nhật trạng thái thành công");
            } else {
                response.put("success", false);
                response.put("message", "Không thể cập nhật trạng thái giao dịch: " + transactionId);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi cập nhật trạng thái: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get payments by date range
     */
    @GetMapping("/date-range")
    public ResponseEntity<Map<String, Object>> getPaymentsByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime start = LocalDateTime.parse(startDate + " 00:00:00", formatter);
            LocalDateTime end = LocalDateTime.parse(endDate + " 23:59:59", formatter);

            List<TransactionInformation> transactions = paymentManagementService.getPaymentsByDateRange(start, end);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", transactions);
            response.put("count", transactions.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi lấy danh sách theo khoảng thời gian: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get payments by payment method
     */
    @GetMapping("/method/{paymentMethod}")
    public ResponseEntity<Map<String, Object>> getPaymentsByMethod(@PathVariable String paymentMethod) {
        try {
            List<TransactionInformation> transactions = paymentManagementService.getPaymentsByMethod(paymentMethod);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", transactions);
            response.put("count", transactions.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi lấy danh sách theo phương thức thanh toán: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
