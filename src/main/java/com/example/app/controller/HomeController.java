package com.example.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * アプリケーションのホーム画面を制御するコントローラークラス。
 * メインページやダッシュボードの表示を担当します。
 */
@Controller
public class HomeController {
    
    /**
     * アプリケーションのホーム画面を表示します。
     * 
     * @return index.htmlテンプレートの名前
     */
    @GetMapping("/")
    public String index() {
        return "index";
    }
}