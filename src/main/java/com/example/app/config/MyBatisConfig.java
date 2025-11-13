package com.example.app.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatisの設定を管理するコンフィグレーションクラス。
 * Mapperのスキャンを行います。
 * その他の設定はapplication.propertiesとSpring Bootの自動設定で行われます。
 */
@Configuration
@MapperScan("com.example.app.mapper")
public class MyBatisConfig {
    // MyBatis Spring Boot Starterが自動設定を行うため、
    // application.propertiesの設定で十分です。
}