package cn.js.ccit.stock.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("stock")
public class Stock {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long dishId;
    private Integer total;
    private Integer available;
    private Integer status;
}
