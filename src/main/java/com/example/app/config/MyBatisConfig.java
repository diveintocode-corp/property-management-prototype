package com.example.app.config;

import javax.sql.DataSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 * MyBatisの設定を管理するコンフィグレーションクラス。
 * データベースとの連携に必要なMyBatisの各種コンポーネントを設定します。
 * MapperのスキャンやSQLセッションファクトリの設定を行います。
 */
@Configuration
@MapperScan("com.example.app.mapper")
public class MyBatisConfig {

    /**
     * SQLセッションファクトリを構成するBeanを作成します。
     * MapperXMLファイルの場所やモデルクラスのパッケージを設定します。
     * 
     * @param dataSource データベース接続用のデータソース
     * @return 設定済みのSQLセッションファクトリBean
     * @throws Exception 設定中にエラーが発生した場合
     */
    @Bean
    public SqlSessionFactoryBean sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setMapperLocations(
            new PathMatchingResourcePatternResolver()
                .getResources("classpath:mappers/*.xml")
        );
        sessionFactory.setTypeAliasesPackage("com.example.app.model");
        return sessionFactory;
    }

    /**
     * SQLセッションテンプレートを作成するBeanメソッド。
     * MyBatisを使用したデータベース操作の実行に使用されます。
     * 
     * @param sqlSessionFactory SQLセッションを生成するファクトリ
     * @return 設定済みのSQLセッションテンプレート
     * @throws Exception SQLセッションの作成中にエラーが発生した場合
     */
    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactoryBean sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory.getObject());
    }
}