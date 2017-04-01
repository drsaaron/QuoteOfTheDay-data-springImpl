/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blazartech.products.qotdp.data.access.impl.spring.config;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

/**
 *
 * @author scott
 */
@Configuration
public class TransactionManagerConfiguration {
    
    @Autowired
    @Qualifier("quoteOfTheDataDataSource")
    private DataSource dataSource;
    
    @Bean("txManager")
    public PlatformTransactionManager getTransactionManager() {
        DataSourceTransactionManager tx = new DataSourceTransactionManager(dataSource);
        return tx;
    }
}
