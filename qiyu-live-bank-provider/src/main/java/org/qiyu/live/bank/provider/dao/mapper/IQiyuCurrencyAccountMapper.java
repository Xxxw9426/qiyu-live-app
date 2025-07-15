package org.qiyu.live.bank.provider.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.qiyu.live.bank.provider.dao.po.QiyuCurrencyAccountPO;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-06
 * @Description: 账户余额数据库表操作mapper接口
 * @Version: 1.0
 */

@Mapper
public interface IQiyuCurrencyAccountMapper extends BaseMapper<QiyuCurrencyAccountPO> {


    /**
     * 增长余额
     * @param userId
     * @param num
     */
    @Update("update qiyu_live_bank.t_qiyu_currency_account set " +
            "qiyu_live_bank.t_qiyu_currency_account.current_balance=qiyu_live_bank.t_qiyu_currency_account.current_balance + #{num} " +
            "where qiyu_live_bank.t_qiyu_currency_account.user_id= #{userId}")
    void incr(@Param("userId") long userId, @Param("num") int num);


    /***
     * 减少余额
     * @param userId
     * @param num
     */
    @Update("update qiyu_live_bank.t_qiyu_currency_account set " +
            "qiyu_live_bank.t_qiyu_currency_account.current_balance=qiyu_live_bank.t_qiyu_currency_account.current_balance - #{num} " +
            "where qiyu_live_bank.t_qiyu_currency_account.user_id= #{userId}")
    void decr(@Param("userId") long userId, @Param("num") int num);


    /***
     * 查询传入用户账户余额
     * @param userId
     * @return
     */
    @Select("select qiyu_live_bank.t_qiyu_currency_account.current_balance from qiyu_live_bank.t_qiyu_currency_account " +
            "where user_id = #{userId} and status = 1 limit 1")
    Integer queryBalance(@Param("userId") long userId);
}
