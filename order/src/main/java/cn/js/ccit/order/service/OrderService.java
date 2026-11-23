package cn.js.ccit.order.service;

import cn.js.ccit.common.Result;
import cn.js.ccit.order.entity.Order;
import cn.js.ccit.order.feign.StockFeign;
import cn.js.ccit.order.mapper.OrderMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {
    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private StockFeign stockFeign;

    public boolean save(Order o) { return orderMapper.insert(o) > 0; }
    public boolean update(Order o) { return orderMapper.updateById(o) > 0; }
    public boolean delete(Long id) { return orderMapper.deleteById(id) > 0; }
    public Order getById(Long id) { return orderMapper.selectById(id); }
    public List<Order> list() { return orderMapper.selectList(new QueryWrapper<>()); }

    // 创建订单：生成 UUID 订单号 -> 插入订单 -> 远程调用 stock 扣减库存（Seata 全局事务）
    @GlobalTransactional
    public Order createOrder(Long userId, Long shopId, Long dishId, Integer quantity, BigDecimal totalAmount) {
        Order order = new Order();
        order.setOrderNo(UUID.randomUUID().toString().replace("-", ""));
        order.setUserId(userId);
        order.setShopId(shopId);
        order.setTotalAmount(totalAmount);
        order.setStatus(0);
        orderMapper.insert(order);

        Result<Boolean> result = stockFeign.deduct(dishId, quantity);
        if (result == null || result.getCode() == null || result.getCode() != 200) {
            throw new RuntimeException("扣减库存失败");
        }
        return order;
    }
}
