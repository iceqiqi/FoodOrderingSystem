package cn.js.ccit.pay.controller;

import cn.js.ccit.common.Result;
import cn.js.ccit.pay.entity.Payment;
import cn.js.ccit.pay.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/pay")
public class PayController {
    @Autowired
    private PaymentService paymentService;

    @PostMapping
    public Result<?> save(@RequestBody Payment p) { return Result.success(paymentService.save(p)); }

    @PutMapping
    public Result<?> update(@RequestBody Payment p) { return Result.success(paymentService.update(p)); }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) { return Result.success(paymentService.delete(id)); }

    @GetMapping("/{id}")
    public Result<Payment> get(@PathVariable Long id) { return Result.success(paymentService.getById(id)); }

    @GetMapping("/list")
    public Result<?> list() { return Result.success(paymentService.list()); }

    // 生成支付记录：POST /pay/doPay?orderNo=xxx&userId=1&amount=15.00&payType=ALIPAY
    @PostMapping("/doPay")
    public Result<?> doPay(@RequestParam String orderNo,
                           @RequestParam Long userId,
                           @RequestParam BigDecimal amount,
                           @RequestParam(defaultValue = "ALIPAY") String payType) {
        return Result.success(paymentService.doPay(orderNo, userId, amount, payType));
    }
}
