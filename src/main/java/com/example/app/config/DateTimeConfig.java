package com.example.app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 日付と時刻のフォーマット設定を管理するコンフィグレーションクラス。
 * アプリケーション全体での日付と時刻の表示形式をISO形式に統一します。
 */
@Configuration
public class DateTimeConfig implements WebMvcConfigurer {

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