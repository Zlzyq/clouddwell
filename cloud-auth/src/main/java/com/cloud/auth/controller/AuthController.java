package com.cloud.auth.controller;

import com.cloud.auth.dto.LoginRequest;
import com.cloud.auth.service.AuthService;
import com.cloud.auth.vo.LoginVO;
import com.cloud.common.base.BaseController;
import com.cloud.common.domain.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/auth")
@Tag(name = "认证")
public class AuthController extends BaseController<LoginRequest, LoginVO> {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "账号密码登录")
    public Result<LoginVO> login(@Valid @RequestBody LoginRequest dto) {
        return ok(authService.login(dto));
    }
}
