package cn.js.ccit.dish.controller;

import cn.js.ccit.common.Result;
import cn.js.ccit.dish.entity.Dish;
import cn.js.ccit.dish.service.DishService;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;

    @PostMapping
    public Result<?> save(@RequestBody Dish d) { return Result.success(dishService.save(d)); }

    @PutMapping
    public Result<?> update(@RequestBody Dish d) { return Result.success(dishService.update(d)); }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) { return Result.success(dishService.delete(id)); }

    @GetMapping("/{id}")
    @SentinelResource(value = "getDishDetail", blockHandler = "detailBlockHandler")
    public Result<Dish> get(@PathVariable Long id) { return Result.success(dishService.getById(id)); }

    // 热点参数限流兜底方法：参数列表与原方法一致，末尾追加 BlockException
    public Result<Dish> detailBlockHandler(Long id, BlockException ex) {
        return Result.error("菜品访问过于频繁，请稍后再试");
    }

    @GetMapping("/list")
    public Result<?> list() { return Result.success(dishService.list()); }

    @GetMapping("/listByShop/{shopId}")
    public Result<?> listByShop(@PathVariable Long shopId) { return Result.success(dishService.listByShop(shopId)); }
}
