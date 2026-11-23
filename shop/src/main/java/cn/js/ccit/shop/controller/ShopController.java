package cn.js.ccit.shop.controller;

import cn.js.ccit.common.Result;
import cn.js.ccit.shop.entity.Shop;
import cn.js.ccit.shop.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shop")
public class ShopController {
    @Autowired
    private ShopService shopService;

    @PostMapping
    public Result<?> save(@RequestBody Shop s) { return Result.success(shopService.save(s)); }

    @PutMapping
    public Result<?> update(@RequestBody Shop s) { return Result.success(shopService.update(s)); }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) { return Result.success(shopService.delete(id)); }

    @GetMapping("/{id}")
    public Result<Shop> get(@PathVariable Long id) { return Result.success(shopService.getById(id)); }

    @GetMapping("/list")
    public Result<?> list() { return Result.success(shopService.list()); }
}