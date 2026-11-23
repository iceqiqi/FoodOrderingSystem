package cn.js.ccit.order.feign;

import cn.js.ccit.common.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "stock")
public interface StockFeign {

    @PostMapping("/stock/deduct")
    Result<Boolean> deduct(@RequestParam("dishId") Long dishId, @RequestParam("quantity") Integer quantity);
}
