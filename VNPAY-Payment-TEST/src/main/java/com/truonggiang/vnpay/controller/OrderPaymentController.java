package com.truonggiang.vnpay.controller;

import com.truonggiang.vnpay.model.OrderRequestDTO;
import com.truonggiang.vnpay.service.OrderPaymentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class OrderPaymentController {

    @Autowired
    private OrderPaymentService orderPaymentService;

    @PostMapping("/api/v1/create-order")
    public ResponseEntity<Map<String, Object>> createOrderPayment(HttpServletRequest request, @RequestBody OrderRequestDTO orderRequestDTO) throws IOException {

        Map<String, Object> result = this.orderPaymentService.createOrder(request, orderRequestDTO);
        return new ResponseEntity<>(result, HttpStatus.OK);

    }
}
