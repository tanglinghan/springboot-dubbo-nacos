package cn.tlh.admin.consumer.aop.annotaion;

/**
 * @author TANG
 * @description: 限流枚举
 * @date: 2020-12-19
 */
public enum LimitType {
    // 默认
    CUSTOMER,
    //  by ip addr
    IP
}