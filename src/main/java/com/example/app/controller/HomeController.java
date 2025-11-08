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
     * ルートURLにアクセスした際に物件一覧ページにリダイレクトします。
     * 
     * @return 物件一覧ページへのリダイレクト
     */
    @GetMapping("/")
    public String index() {
        return "redirect:/properties";
    }
}