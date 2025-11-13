package com.example.app.controller;

import com.example.app.model.User;
import com.example.app.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 認証に関する処理を担当するコントローラークラス。
 * ログイン、登録などのWebインターフェースを提供します。
 * ログアウトはSpring Securityが処理します。
 */
@Controller
@RequiredArgsConstructor
public class AuthController {
    
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository = 
        new HttpSessionSecurityContextRepository();
    
    /**
     * ログインフォームを表示します。
     * 
     * @param error エラーパラメータ
     * @param logout ログアウトパラメータ
     * @param model ビューに渡すモデル
     * @return ログインフォームのテンプレート名
     */
    @GetMapping("/login")
    public String loginForm(@RequestParam(required = false) String error,
                           @RequestParam(required = false) String logout,
                           Model model) {
        if (error != null) {
            model.addAttribute("error", "ユーザー名またはパスワードが正しくありません");
        }
        if (logout != null) {
            model.addAttribute("message", "ログアウトしました");
        }
        return "auth/login";
    }
    
    /**
     * 新規登録フォームを表示します。
     * 
     * @param model ビューに渡すモデル
     * @return 登録フォームのテンプレート名
     */
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }
    
    /**
     * 新規ユーザー登録処理を行います。
     * 登録成功後、自動的にログインします。
     * 
     * @param user 登録するユーザー情報
     * @param bindingResult バリデーション結果
     * @param request HTTPリクエスト
     * @param response HTTPレスポンス
     * @param redirectAttributes リダイレクト時に使用する属性
     * @return リダイレクト先のURL
     */
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") User user,
                          BindingResult bindingResult,
                          HttpServletRequest request,
                          HttpServletResponse response,
                          RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }
        
        try {
            // パスワードを保存（後で認証に使用）
            String rawPassword = user.getPassword();
            
            // ユーザーを登録（パスワードはハッシュ化される）
            userService.register(user);
            
            // 登録後、自動的にログイン
            UsernamePasswordAuthenticationToken authRequest = 
                UsernamePasswordAuthenticationToken.unauthenticated(user.getUsername(), rawPassword);
            
            Authentication authentication = authenticationManager.authenticate(authRequest);
            
            // セキュリティコンテキストを作成してセッションに保存
            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            securityContext.setAuthentication(authentication);
            SecurityContextHolder.setContext(securityContext);
            securityContextRepository.saveContext(securityContext, request, response);
            
            redirectAttributes.addFlashAttribute("message", "ユーザー登録が完了し、ログインしました。");
            return "redirect:/properties";
        } catch (IllegalStateException e) {
            bindingResult.rejectValue("username", "error.username", e.getMessage());
            return "auth/register";
        } catch (Exception e) {
            bindingResult.reject("error", "登録中にエラーが発生しました: " + e.getMessage());
            return "auth/register";
        }
    }
}

