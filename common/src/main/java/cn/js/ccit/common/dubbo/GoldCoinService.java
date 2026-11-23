package cn.js.ccit.common.dubbo;

/**
 * 发金币 Dubbo 契约。
 *
 * <p>Sentinel Dubbo 资源的命名规则为：接口全限定名:方法名(参数类型全限定名,以英文逗号相连)。
 * 因此本接口的方法对应的资源名必须严格为：
 * {@code cn.js.ccit.common.dubbo.GoldCoinService:updateGoldCoin(java.lang.String,java.lang.Integer,java.lang.String)}
 */
public interface GoldCoinService {

    /**
     * 下单成功后为用户发放金币。
     *
     * @param userName 用户名
     * @param goldCoin 发放数量
     * @param orderNo  订单号
     */
    void updateGoldCoin(String userName, Integer goldCoin, String orderNo);
}
