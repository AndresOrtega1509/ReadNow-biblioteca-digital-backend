package co.edu.uniquindio.read_now.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;

import javax.sql.DataSource;

@Configuration
public class AuditDataSourceConfig {

    @Bean
    public static BeanPostProcessor auditingDataSourceBeanPostProcessor() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
                if (bean instanceof DataSource dataSource && !(bean instanceof AuditingDataSource)) {
                    return new AuditingDataSource(dataSource);
                }
                return bean;
            }
        };
    }
}
