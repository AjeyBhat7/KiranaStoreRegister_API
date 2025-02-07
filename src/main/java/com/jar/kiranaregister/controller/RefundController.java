package com.jar.kiranaregister.controller;

import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("refund")
public class RefundController {

    @PostMapping("refundRequest")
    public ResponseEntity<?> handleRefundRequestFor() {
        return ResponseEntity.ok("done");
    }

    @PutMapping("refundRequest/approve")
    public ResponseEntity<?> approveRequest() {
        return ResponseEntity.ok("");
    }

    @PutMapping("refundRequest/reject")
    public ResponseEntity<?> rejectRequest(@RequestParam UUID transactionId) {
        return ResponseEntity.ok("");
    }
}
