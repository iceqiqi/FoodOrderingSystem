package cn.js.ccit.dish.service;

import cn.js.ccit.dish.entity.Dish;
import cn.js.ccit.dish.mapper.DishMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DishService {
    @Autowired
    private DishMapper dishMapper;

    public boolean save(Dish d) { return dishMapper.insert(d) > 0; }
    public boolean update(Dish d) { return dishMapper.updateById(d) > 0; }
    public boolean delete(Long id) { return dishMapper.deleteById(id) > 0; }
    public Dish getById(Long id) { return dishMapper.selectById(id); }
    public List<Dish> list() { return dishMapper.selectList(new QueryWrapper<>()); }

    // 按店铺查询菜品
    public List<Dish> listByShop(Long shopId) {
        return dishMapper.selectList(new QueryWrapper<Dish>().eq("shop_id", shopId));
    }
}
