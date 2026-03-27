package org.ust.task.config;

import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

/**
 * Hibernate/JPA configuration for advanced optimization.
 * Enables second-level caching, batch processing, and query optimization.
 */
@Configuration
public class HibernateOptimizationConfig {

    /**
     * Configure entity manager factory with optimization settings.
     */
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("org.ust.task.entity");
        
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        
        Properties hibernateProperties = new Properties();
        
        // =============== Second-Level Cache Configuration ===============
        hibernateProperties.setProperty("hibernate.cache.use_second_level_cache", "true");
        hibernateProperties.setProperty("hibernate.cache.use_query_cache", "true");
        hibernateProperties.setProperty("hibernate.cache.region.factory_class", 
                "org.hibernate.cache.jcache.JCacheRegionFactory");
        
        // =============== Batch Processing Configuration ===============
        hibernateProperties.setProperty("hibernate.jdbc.batch_size", "20");
        hibernateProperties.setProperty("hibernate.order_inserts", "true");
        hibernateProperties.setProperty("hibernate.order_updates", "true");
        hibernateProperties.setProperty("hibernate.jdbc.batch_versioned_data", "true");
        
        // =============== Query Optimization ===============
        hibernateProperties.setProperty("hibernate.query.in_clause_parameter_padding", "true");
        hibernateProperties.setProperty("hibernate.dialect_fetches_all_attributes", "false");
        
        // =============== Connection Pool Optimization ===============
        hibernateProperties.setProperty("hibernate.connection.provider_disables_autocommit", "true");
        
        // =============== SQL Generation ===============
        hibernateProperties.setProperty("hibernate.format_sql", "false"); // Set to true for dev/debug
        hibernateProperties.setProperty("hibernate.generate_statistics", "false"); // Set to true for monitoring
        
        // =============== Lazy Loading & Proxy ===============
        hibernateProperties.setProperty("hibernate.enable_lazy_load_no_trans", "true");
        
        // =============== Entity Graph Support ===============
        hibernateProperties.setProperty("hibernate.use_entity_graph", "true");
        
        em.setJpaProperties(hibernateProperties);
        
        return em;
    }
}

