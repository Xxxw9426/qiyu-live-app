package org.qiyu.live.living.provider.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-02
 * @Description: 配置MyBatisPlus分页查询
 * @Version: 1.0
 */
@Configuration
public class MybatisPageConfig {


    @Bean
    public PaginationInnerInterceptor paginationInnerInterceptor() {
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();
        paginationInnerInterceptor.setMaxLimit(1000L);
        paginationInnerInterceptor.setDbType(DbType.MYSQL);
        // 开启count 的join优化，只针对left join
        paginationInnerInterceptor.setOptimizeJoin(true);
        return paginationInnerInterceptor;
    }


    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        mybatisPlusInterceptor.setInterceptors(Collections.singletonList(paginationInnerInterceptor()));
        return mybatisPlusInterceptor;
    }
}
