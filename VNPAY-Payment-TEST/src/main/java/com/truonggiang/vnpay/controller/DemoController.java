package com.truonggiang.vnpay.controller;

import com.truonggiang.vnpay.model.OrderRequestDTO;
import com.truonggiang.vnpay.service.OrderPaymentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.Map;

@Controller
public class DemoController {

    @Autowired
    private OrderPaymentService orderPaymentService;

    @GetMapping("/demo")
    @ResponseBody
    public String showDemo() {
        return """
                <!DOCTYPE html>
                <html lang="vi">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>VNPay Demo - Test Payment</title>
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            margin: 0;
                            padding: 20px;
                            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                            color: white;
                            min-height: 100vh;
                        }
                        .container {
                            max-width: 600px;
                            margin: 0 auto;
                            background: rgba(255,255,255,0.1);
                            padding: 40px;
                            border-radius: 15px;
                            backdrop-filter: blur(10px);
                        }
                        h1 { color: #fff; margin-bottom: 30px; text-align: center; }
                        .form-group {
                            margin: 20px 0;
                        }
                        label {
                            display: block;
                            margin-bottom: 5px;
                            font-weight: bold;
                        }
                        input, select {
                            width: 100%;
                            padding: 12px;
                            border: none;
                            border-radius: 5px;
                            font-size: 16px;
                            background: rgba(255,255,255,0.9);
                            color: #333;
                            box-sizing: border-box;
                        }
                        button {
                            background: #00ff88;
                            color: #333;
                            padding: 15px 30px;
                            border: none;
                            border-radius: 5px;
                            font-size: 18px;
                            font-weight: bold;
                            cursor: pointer;
                            width: 100%;
                            margin-top: 20px;
                        }
                        button:hover {
                            background: #00cc70;
                        }
                        .info {
                            background: rgba(0,255,136,0.2);
                            color: #00ff88;
                            padding: 15px;
                            border-radius: 5px;
                            margin: 20px 0;
                            border: 2px solid #00ff88;
                        }
                        .back-link {
                            color: #00ff88;
                            text-decoration: none;
                            margin-bottom: 20px;
                            display: inline-block;
                        }
                        .back-link:hover {
                            text-decoration: underline;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <a href="/" class="back-link">← Quay về trang chủ</a>

                        <h1>💳 Demo Thanh toán VNPay</h1>

                        <div class="info">
                            <strong>📋 Hướng dẫn test:</strong><br>
                            1. Điền thông tin đơn hàng bên dưới<br>
                            2. Nhấn "Thanh toán" để chuyển đến VNPay<br>
                            3. Sử dụng thẻ test: 9704198526191432198<br>
                            4. Nhập OTP: 123456
                        </div>

                        <form action="/demo/create-payment" method="post">
                            <div class="form-group">
                                <label for="amount">Số tiền (VND):</label>
                                <input type="number" id="amount" name="amount" value="100000" min="10000" required>
                            </div>

                            <div class="form-group">
                                <label for="orderInfo">Thông tin đơn hàng:</label>
                                <input type="text" id="orderInfo" name="orderInfo" value="Thanh toan don hang test" required>
                            </div>

                            <div class="form-group">
                                <label for="bankCode">Ngân hàng:</label>
                                <select id="bankCode" name="bankCode">
                                    <option value="">Cổng thanh toán VNPay</option>
                                    <option value="NCB">NCB - Ngân hàng Quốc dân</option>
                                    <option value="AGRIBANK">Agribank</option>
                                    <option value="SCB">SCB - Ngân hàng Sài Gòn</option>
                                    <option value="SACOMBANK">Sacombank</option>
                                    <option value="EXIMBANK">Eximbank</option>
                                    <option value="MSBANK">MSBank</option>
                                    <option value="NAMABANK">NamABank</option>
                                    <option value="VNMART">Vi điện tử VnMart</option>
                                </select>
                            </div>

                            <div class="form-group">
                                <label for="language">Ngôn ngữ:</label>
                                <select id="language" name="language">
                                    <option value="vn">Tiếng Việt</option>
                                    <option value="en">English</option>
                                </select>
                            </div>

                            <button type="submit">🚀 Thanh toán với VNPay</button>
                        </form>
                    </div>
                </body>
                </html>
                """;
    }

    @PostMapping("/demo/create-payment")
    public String createDemoPayment(
            HttpServletRequest request,
            @RequestParam String amount,
            @RequestParam String orderInfo,
            @RequestParam(required = false) String bankCode,
            @RequestParam(defaultValue = "vn") String language) throws IOException {

        OrderRequestDTO orderRequest = new OrderRequestDTO();
        orderRequest.setAmount(Long.parseLong(amount));
        orderRequest.setOrderInfo(orderInfo);
        orderRequest.setBankCode(bankCode != null && !bankCode.isEmpty() ? bankCode : null);
        orderRequest.setLanguage(language);
        Map<String, Object> result = orderPaymentService.createOrder(request, orderRequest);
        String paymentUrl = (String) result.get("redirect_url");

        if (paymentUrl == null || paymentUrl.isEmpty()) {
            throw new RuntimeException("Failed to generate payment URL");
        }

        return "redirect:" + paymentUrl;
    }

    @GetMapping("/demo/callback")
    @ResponseBody
    public String paymentCallback(@RequestParam Map<String, String> params) {
        String responseCode = params.get("vnp_ResponseCode");
        String transactionStatus = params.get("vnp_TransactionStatus");
        String amount = params.get("vnp_Amount");
        String orderInfo = params.get("vnp_OrderInfo");

        boolean isSuccess = "00".equals(responseCode) && "00".equals(transactionStatus);

        String successDiv = isSuccess
                ? "<div class=\"success\">✅ <strong>Thanh toán thành công!</strong><br>Giao dịch đã được xử lý thành công.</div>"
                : "<div class=\"error\">❌ <strong>Thanh toán thất bại!</strong><br>Giao dịch không được thực hiện.</div>";

        String amountFormatted = amount != null ? String.format("%,d VND", Long.parseLong(amount) / 100) : "N/A";
        String orderInfoDisplay = orderInfo != null ? orderInfo : "N/A";

        return "<!DOCTYPE html>" +
                "<html lang=\"vi\">" +
                "<head>" +
                "<meta charset=\"UTF-8\">" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "<title>Kết quả thanh toán</title>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; margin: 0; padding: 20px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; min-height: 100vh; }"
                +
                ".container { max-width: 600px; margin: 0 auto; background: rgba(255,255,255,0.1); padding: 40px; border-radius: 15px; backdrop-filter: blur(10px); text-align: center; }"
                +
                ".success { background: rgba(0,255,136,0.2); color: #00ff88; padding: 20px; border-radius: 10px; border: 2px solid #00ff88; margin: 20px 0; }"
                +
                ".error { background: rgba(255,0,0,0.2); color: #ff6b6b; padding: 20px; border-radius: 10px; border: 2px solid #ff6b6b; margin: 20px 0; }"
                +
                ".details { background: rgba(255,255,255,0.1); padding: 20px; border-radius: 10px; margin: 20px 0; text-align: left; }"
                +
                ".back-link { background: #00ff88; color: #333; padding: 15px 30px; text-decoration: none; border-radius: 5px; font-weight: bold; display: inline-block; margin: 10px; }"
                +
                ".back-link:hover { background: #00cc70; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class=\"container\">" +
                "<h1>📋 Kết quả thanh toán</h1>" +
                successDiv +
                "<div class=\"details\">" +
                "<h3>Chi tiết giao dịch:</h3>" +
                "<p><strong>Mã phản hồi:</strong> " + responseCode + "</p>" +
                "<p><strong>Trạng thái:</strong> " + transactionStatus + "</p>" +
                "<p><strong>Số tiền:</strong> " + amountFormatted + "</p>" +
                "<p><strong>Thông tin đơn hàng:</strong> " + orderInfoDisplay + "</p>" +
                "</div>" +
                "<a href=\"/demo\" class=\"back-link\">🔄 Thử lại thanh toán</a>" +
                "<a href=\"/\" class=\"back-link\">🏠 Về trang chủ</a>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
}
