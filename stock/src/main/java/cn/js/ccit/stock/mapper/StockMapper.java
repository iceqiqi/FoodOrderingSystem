package cn.js.ccit.stock.mapper;

import cn.js.ccit.stock.entity.Stock;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StockMapper extends BaseMapper<Stock> {
}
