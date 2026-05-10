package com.crs.config;

import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * 数据库与 JPA 配置类 (DatabaseConfig)
 * 
 * <p>本类负责配置 Spring Data JPA 的核心组件，包括实体管理器工厂 (EntityManagerFactory) 
 * 和平台事务管理器 (PlatformTransactionManager)。</p>
 * 
 * <p>主要职责：</p>
 * <ul>
 *     <li>定义实体扫描范围，确保 `com.crs.entity` 包下的所有 JPA 实体被正确加载。</li>
 *     <li>集成 Hibernate 作为 JPA 供应商适配器。</li>
 *     <li>从外部配置文件 (application.yml/properties) 加载并应用自定义的 JPA 属性。</li>
 *     <li>配置全局事务管理器，支持 `@Transactional` 注解。</li>
 * </ul>
 */
@Configuration
public class DatabaseConfig {
    
    /** 基础数据源，由 Spring Boot 自动配置注入 */
    private final DataSource dataSource;
    /** 从外部配置中读取的 JPA 相关属性（如 ddl-auto, show-sql 等） */
    private final JpaProperties jpaProperties;
    
    /**
     * 构造函数，通过构造器注入依赖。
     */
    public DatabaseConfig(DataSource dataSource, JpaProperties jpaProperties) {
        this.dataSource = dataSource;
        this.jpaProperties = jpaProperties;
    }
    
    /**
     * 配置实体管理器工厂。
     * 
     * @return 实体管理器工厂 Bean
     */
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(dataSource);
        // 指定实体类所在的包路径
        factory.setPackagesToScan("com.crs.entity");
        // 指定 JPA 供应商为 Hibernate
        factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        
        // 转换并应用自定义 JPA 属性
        Properties properties = new Properties();
        properties.putAll(jpaProperties.getProperties());
        factory.setJpaProperties(properties);
        
        return factory;
    }
    
    /**
     * 配置 JPA 事务管理器。
     * 
     * @return 事务管理器 Bean
     */
    @Bean
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
        return transactionManager;
    }
}

