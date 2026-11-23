package cn.js.ccit.stock.service;

import cn.js.ccit.stock.entity.Stock;
import cn.js.ccit.stock.mapper.StockMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockService {
    @Autowired
    private StockMapper stockMapper;

    public boolean save(Stock s) { return stockMapper.insert(s) > 0; }
    public boolean update(Stock s) { return stockMapper.updateById(s) > 0; }
    public boolean delete(Long id) { return stockMapper.deleteById(id) > 0; }
    public Stock getById(Long id) { return stockMapper.selectById(id); }
    public List<Stock> list() { return stockMapper.selectList(new QueryWrapper<>()); }

    // 扣减库存
    public boolean deduct(Long dishId, Integer quantity) {
        Stock stock = stockMapper.selectOne(new QueryWrapper<Stock>().eq("dish_id", dishId));
        if (stock == null) {
            throw new RuntimeException("库存记录不存在");
        }
        if (stock.getAvailable() < quantity) {
            throw new RuntimeException("库存不足");
        }
        stock.setAvailable(stock.getAvailable() - quantity);
        return stockMapper.updateById(stock) > 0;
    }
}
