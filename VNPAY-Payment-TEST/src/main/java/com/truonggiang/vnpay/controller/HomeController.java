package com.truonggiang.vnpay.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

    @GetMapping("/")
    @ResponseBody
    public String home() {
        return """
                <!DOCTYPE html>
                <html lang="vi">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>VNPay Payment Integration</title>
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
                            max-width: 800px;
                            margin: 0 auto;
                            text-align: center;
                            background: rgba(255,255,255,0.1);
                            padding: 40px;
                            border-radius: 15px;
                            backdrop-filter: blur(10px);
                        }
                        h1 {
                            color: #fff;
                            margin-bottom: 30px;
                            font-size: 2.5em;
                        }
                        .endpoints {
                            background: rgba(255,255,255,0.1);
                            padding: 20px;
                            border-radius: 10px;
                            margin: 20px 0;
                            text-align: left;
                        }
                        .endpoint {
                            margin: 10px 0;
                            padding: 10px;
                            background: rgba(255,255,255,0.1);
                            border-radius: 5px;
                            border-left: 4px solid #00ff88;
                        }
                        .method {
                            font-weight: bold;
                            color: #00ff88;
                        }
                        .test-card {
                            background: rgba(255,255,255,0.1);
                            padding: 20px;
                            border-radius: 10px;
                            margin: 20px 0;
                        }
                        .card-info {
                            background: rgba(0,0,0,0.2);
                            padding: 15px;
                            border-radius: 8px;
                            margin: 10px 0;
                            font-family: monospace;
                        }
                        .status {
                            background: rgba(0,255,136,0.2);
                            color: #00ff88;
                            padding: 10px;
                            border-radius: 5px;
                            margin: 20px 0;
                            border: 2px solid #00ff88;
                        }
                        .warning {
                            background: rgba(255,165,0,0.2);
                            color: #ffa500;
                            padding: 10px;
                            border-radius: 5px;
                            margin: 20px 0;
                            border: 2px solid #ffa500;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <h1>🏦 VNPay Payment Integration</h1>

                        <div class="status">
                            ✅ Ứng dụng đang hoạt động bình thường!<br>
                            📡 Spring Boot 3.0.12 + Java 21<br>
                            🔧 VNPay Sandbox Environment
                        </div>

                        <div class="endpoints">
                            <h2>📋 API Endpoints:</h2>

                            <div class="endpoint">
                                <span class="method">POST</span> /api/v1/create-order<br>
                                <small>Tạo đơn hàng và redirect đến VNPay</small>
                            </div>

                            <div class="endpoint">
                                <span class="method">GET</span> /api/v1/callback<br>
                                <small>Callback URL từ VNPay sau thanh toán</small>
                            </div>

                            <div class="endpoint">
                                <span class="method">POST</span> /api/v1/get-status<br>
                                <small>Kiểm tra trạng thái thanh toán</small>
                            </div>
                        </div>

                        <div class="test-card">
                            <h2>💳 Thông tin thẻ test (Sandbox):</h2>
                            <div class="card-info">
                                <strong>Ngân hàng:</strong> NCB<br>
                                <strong>Số thẻ:</strong> 9704198526191432198<br>
                                <strong>Tên chủ thẻ:</strong> NGUYEN VAN A<br>
                                <strong>Ngày phát hành:</strong> 07/15<br>
                                <strong>Mật khẩu OTP:</strong> 123456
                            </div>
                        </div>

                        <div class="warning">
                            ⚠️ <strong>Lưu ý:</strong> Đây là môi trường Sandbox của VNPay.<br>
                            Chỉ sử dụng để test, không dùng cho thanh toán thực tế!
                        </div>                <div class="endpoints">
                            <h2>🔗 Links hữu ích:</h2>
                            <div class="endpoint">
                                <a href="/demo" style="color: #00ff88; text-decoration: none; font-size: 1.2em;">
                                    🧪 <strong>Demo Thanh toán</strong> - Test payment flow
                                </a>
                            </div>
                            <div class="endpoint">
                                <a href="https://sandbox.vnpayment.vn/merchantv2/" target="_blank" style="color: #00ff88;">
                                    🏪 VNPay Merchant Admin
                                </a>
                            </div>
                            <div class="endpoint">
                                <a href="https://sandbox.vnpayment.vn/apis/docs/thanh-toan-pay/pay.html" target="_blank" style="color: #00ff88;">
                                    📚 VNPay API Documentation
                                </a>
                            </div>
                        </div>

                        <div style="margin-top: 40px; font-size: 0.9em; opacity: 0.8;">
                            <p>🚀 VNPay Spring Boot Integration v1.0</p>
                            <p>📅 Deployed: June 13, 2025</p>
                        </div>
                    </div>
                </body>
                </html>
                """;
    }
}
