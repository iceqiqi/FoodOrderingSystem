package cn.js.ccit.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("shop")
public class Shop {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String shopName;
    private Long ownerId;
    private String address;
    private String phone;
    private Integer status;
}