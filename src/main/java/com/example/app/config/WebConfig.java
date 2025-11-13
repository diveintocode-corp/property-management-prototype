package com.example.app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web設定を管理するコンフィグレーションクラス。
 * 日付フォーマット設定などを行います。
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    /**
     * 日付と時刻のフォーマッタを登録するメソッド。
     * ISO形式の日付フォーマットを全システムで使用するように設定します。
     * 
     * @param registry フォーマッタを登録するためのレジストリ
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
        registrar.setUseIsoFormat(true);
        registrar.registerFormatters(registry);
    }
}

