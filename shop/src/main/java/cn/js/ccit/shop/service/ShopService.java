package cn.js.ccit.shop.service;

import cn.js.ccit.shop.mapper.ShopMapper;
import cn.js.ccit.shop.entity.Shop;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShopService {
    @Autowired
    private ShopMapper shopMapper;

    public boolean save(Shop s) { return shopMapper.insert(s) > 0; }
    public boolean update(Shop s) { return shopMapper.updateById(s) > 0; }
    public boolean delete(Long id) { return shopMapper.deleteById(id) > 0; }
    public Shop getById(Long id) { return shopMapper.selectById(id); }
    public List<Shop> list() { return shopMapper.selectList(new QueryWrapper<>()); }
}