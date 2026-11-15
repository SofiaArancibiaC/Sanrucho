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
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariDataSource;

import jakarta.persistence.EntityManagerFactory;

/**
 * Datasource PRIMARIO: base de datos "pedidos".
 *
 * El proyecto usa una base de datos por tabla; por eso cada microservicio
 * declara sus datasources manualmente.
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "cl.sanrucho.pedidos.repository.pedidos",
        entityManagerFactoryRef = "pedidosEntityManagerFactory",
        transactionManagerRef = "pedidosTransactionManager"
)
public class PedidoDataSourceConfig {

    @Primary
    @Bean(name = "pedidosDataSourceProperties")
    @ConfigurationProperties("app.datasource.pedidos")
    public DataSourceProperties pedidosDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean(name = "pedidosDataSource")
    public DataSource pedidosDataSource(
            @Qualifier("pedidosDataSourceProperties") DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Primary
    @Bean(name = "pedidosEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean pedidosEntityManagerFactory(
            @Qualifier("pedidosDataSource") DataSource dataSource,
            @Value("${app.jpa.ddl-auto:update}") String ddlAuto,
            @Value("${app.jpa.show-sql:false}") boolean showSql) {

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("cl.sanrucho.pedidos.model.entity.pedidos");

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

    @Primary
    @Bean(name = "pedidosTransactionManager")
    public PlatformTransactionManager pedidosTransactionManager(
            @Qualifier("pedidosEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
