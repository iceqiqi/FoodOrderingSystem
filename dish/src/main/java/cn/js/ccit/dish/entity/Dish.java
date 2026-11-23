package cn.js.ccit.dish.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("dish")
public class Dish {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long shopId;
    private String dishName;
    private String category;
    private BigDecimal price;
    private String description;
    private String image;
    private Integer status;
}
