package com.cloud.system.controller;

import com.cloud.common.base.BaseController;
import com.cloud.common.domain.Result;
import com.cloud.system.dto.UserQueryDTO;
import com.cloud.system.service.UserReadService;
import com.cloud.system.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/system/users")
@Tag(name = "系统用户")
public class SystemUserController extends BaseController<UserQueryDTO, UserVO> {

    private final UserReadService userReadService;

    public SystemUserController(UserReadService userReadService) {
        this.userReadService = userReadService;
    }

    @GetMapping("/{userId}")
    @Operation(summary = "按ID查询用户")
    public Result<UserVO> getById(@PathVariable("userId") Long userId) {
        return ok(userReadService.getByUserId(userId));
    }
}
