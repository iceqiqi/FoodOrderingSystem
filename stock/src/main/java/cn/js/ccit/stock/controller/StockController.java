package cn.js.ccit.stock.controller;

import cn.js.ccit.common.Result;
import cn.js.ccit.stock.entity.Stock;
import cn.js.ccit.stock.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stock")
public class StockController {
    @Autowired
    private StockService stockService;

    @PostMapping
    public Result<?> save(@RequestBody Stock s) { return Result.success(stockService.save(s)); }

    @PutMapping
    public Result<?> update(@RequestBody Stock s) { return Result.success(stockService.update(s)); }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) { return Result.success(stockService.delete(id)); }

    @GetMapping("/{id}")
    public Result<Stock> get(@PathVariable Long id) { return Result.success(stockService.getById(id)); }

    @GetMapping("/list")
    public Result<?> list() { return Result.success(stockService.list()); }

    // 扣减库存：POST /stock/deduct?dishId=1&quantity=1
    @PostMapping("/deduct")
    public Result<?> deduct(@RequestParam Long dishId, @RequestParam Integer quantity) {
        return Result.success(stockService.deduct(dishId, quantity));
    }
}
