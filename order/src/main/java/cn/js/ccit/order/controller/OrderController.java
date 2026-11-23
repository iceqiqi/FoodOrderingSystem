package cn.js.ccit.order.controller;

import cn.js.ccit.common.Result;
import cn.js.ccit.order.entity.Order;
import cn.js.ccit.order.service.OrderService;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    // 下单：POST /order/create?userId=3&shopId=1&dishId=1&quantity=1&totalAmount=15.00
    @PostMapping("/create")
    @SentinelResource(value = "/order/create", fallback = "createFallback")
    public Result<?> create(@RequestParam Long userId,
                            @RequestParam Long shopId,
                            @RequestParam Long dishId,
                            @RequestParam Integer quantity,
                            @RequestParam BigDecimal totalAmount) {
        return Result.success(orderService.createOrder(userId, shopId, dishId, quantity, totalAmount));
    }

    // 兜底降级方法：参数列表必须与 create 一致，末尾额外加一个 Throwable
    public Result<?> createFallback(Long userId, Long shopId, Long dishId, Integer quantity,
                                    BigDecimal totalAmount, Throwable e) {
        return Result.error("下单失败，请稍后再试（Sentinel 兜底降级）");
    }

    @PutMapping
    public Result<?> update(@RequestBody Order o) { return Result.success(orderService.update(o)); }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) { return Result.success(orderService.delete(id)); }

    @GetMapping("/{id}")
    public Result<Order> get(@PathVariable Long id) { return Result.success(orderService.getById(id)); }

    @GetMapping("/list")
    public Result<?> list() { return Result.success(orderService.list()); }
}
