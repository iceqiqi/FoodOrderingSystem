package cn.js.ccit.pay.service;

import cn.js.ccit.pay.entity.Payment;
import cn.js.ccit.pay.mapper.PaymentMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PaymentService {
    @Autowired
    private PaymentMapper paymentMapper;

    public boolean save(Payment p) { return paymentMapper.insert(p) > 0; }
    public boolean update(Payment p) { return paymentMapper.updateById(p) > 0; }
    public boolean delete(Long id) { return paymentMapper.deleteById(id) > 0; }
    public Payment getById(Long id) { return paymentMapper.selectById(id); }
    public List<Payment> list() { return paymentMapper.selectList(new QueryWrapper<>()); }

    // 生成支付记录（默认已支付）
    public Payment doPay(String orderNo, Long userId, BigDecimal amount, String payType) {
        Payment p = new Payment();
        p.setOrderNo(orderNo);
        p.setUserId(userId);
        p.setAmount(amount);
        p.setPayType(payType);
        p.setStatus(1);
        paymentMapper.insert(p);
        return p;
    }
}
