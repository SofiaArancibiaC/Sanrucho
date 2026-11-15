package cl.sanrucho.pedidos.config;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import com.zaxxer.hikari.HikariDataSource;

import jakarta.persistence.EntityManagerFactory;

/**
 * Datasource SECUNDARIO: base de datos "detalle_pedidos".
 *
 * Las escrituras de detalle usan explicitamente
 * transactionManager = "detallePedidosTransactionManager" (ver DetallePedidoService).
 */
@Configuration
@EnableJpaRepositories(
        basePackages = "cl.sanrucho.pedidos.repository.detallepedidos",
        entityManagerFactoryRef = "detallePedidosEntityManagerFactory",
        transactionManagerRef = "detallePedidosTransactionManager"
)
public class DetallePedidoDataSourceConfig {

    @Bean(name = "detallePedidosDataSourceProperties")
    @ConfigurationProperties("app.datasource.detalle-pedidos")
    public DataSourceProperties detallePedidosDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "detallePedidosDataSource")
    public DataSource detallePedidosDataSource(
            @Qualifier("detallePedidosDataSourceProperties") DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean(name = "detallePedidosEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean detallePedidosEntityManagerFactory(
            @Qualifier("detallePedidosDataSource") DataSource dataSource,
            @Value("${app.jpa.ddl-auto:update}") String ddlAuto,
            @Value("${app.jpa.show-sql:false}") boolean showSql) {

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("cl.sanrucho.pedidos.model.entity.detallepedidos");

        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setDatabase(Database.POSTGRESQL);
        adapter.setShowSql(showSql);
        em.setJpaVendorAdapter(adapter);

        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", ddlAuto);
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        properties.put("hibernate.format_sql", showSql);
        em.setJpaPropertyMap(properties);

        return em;
    }

    @Bean(name = "detallePedidosTransactionManager")
    public PlatformTransactionManager detallePedidosTransactionManager(
            @Qualifier("detallePedidosEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
