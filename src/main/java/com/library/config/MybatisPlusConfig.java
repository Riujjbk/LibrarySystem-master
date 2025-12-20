package com.library.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis Plus 插件：
 * MybatisPlusInterceptor：MyBatis Plus 的拦截器容器，用于管理多个插件。
 * PaginationInnerInterceptor：分页插件，用于自动处理分页查询。
 * Spring 注解：
 * @Configuration：标记该类为 Spring 配置类。
 * @Bean：声明一个由 Spring 容器管理的 Bean。
 */
@Configuration
public class MybatisPlusConfig {
    /**
     * 配置 MyBatis Plus 拦截器容器 Bean
     * 该方法创建 MybatisPlusInterceptor 实例并注册分页插件
     *
     * @return 配置好的 MyBatis Plus 拦截器容器
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 添加分页插件到拦截器容器，启用自动分页功能
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        return interceptor;
    }
}
