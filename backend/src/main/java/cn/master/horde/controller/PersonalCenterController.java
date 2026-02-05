package cn.master.horde.controller;

import cn.master.horde.common.result.BizException;
import cn.master.horde.common.util.SessionUtils;
import cn.master.horde.security.security.KickoutService;
import cn.master.horde.model.dto.request.PersonalUpdatePasswordRequest;
import cn.master.horde.service.SystemUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : 11's papa
 * @since : 2026/1/30, 星期五
 **/
@RestController
@Tag(name = "个人中心")
@RequiredArgsConstructor
@RequestMapping("/personal")
public class PersonalCenterController {
    private final SystemUserService systemUserService;
    private final KickoutService kickoutService;

    @PostMapping("/update-password")
    @Operation(summary = "个人中心-修改密码")
    // @Loggable("修改密码")
    public void updateUser(@Validated @RequestBody PersonalUpdatePasswordRequest request) {
        checkPermission(request.getId());
        if (systemUserService.updatePassword(request)) {
            kickoutService.kickUserByUsername(SessionUtils.getCurrentUsername());
        }
    }

    private void checkPermission(String id) {
        if (!id.equals(SessionUtils.getCurrentUserId())) {
            throw new BizException("personal.no.permission");
        }
    }
}
