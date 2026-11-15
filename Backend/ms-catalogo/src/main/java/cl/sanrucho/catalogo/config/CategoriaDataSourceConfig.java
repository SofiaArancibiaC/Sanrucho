package cl.sanrucho.catalogo.config;

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
 * Datasource SECUNDARIO: base de datos "categorias".
 *
 * No es @Primary: la transaccion por defecto del microservicio es la de
 * productos. Las operaciones sobre categorias declaran explicitamente
 * transactionManager = "categoriasTransactionManager".
 */
@Configuration
@EnableJpaRepositories(
        basePackages = "cl.sanrucho.catalogo.repository.categorias",
        entityManagerFactoryRef = "categoriasEntityManagerFactory",
        transactionManagerRef = "categoriasTransactionManager"
)
public class CategoriaDataSourceConfig {

    @Bean(name = "categoriasDataSourceProperties")
    @ConfigurationProperties("app.datasource.categorias")
    public DataSourceProperties categoriasDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "categoriasDataSource")
    public DataSource categoriasDataSource(
            @Qualifier("categoriasDataSourceProperties") DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean(name = "categoriasEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean categoriasEntityManagerFactory(
            @Qualifier("categoriasDataSource") DataSource dataSource,
            @Value("${app.jpa.ddl-auto:update}") String ddlAuto,
            @Value("${app.jpa.show-sql:false}") boolean showSql) {

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("cl.sanrucho.catalogo.model.entity.categorias");

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

    @Bean(name = "categoriasTransactionManager")
    public PlatformTransactionManager categoriasTransactionManager(
            @Qualifier("categoriasEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
