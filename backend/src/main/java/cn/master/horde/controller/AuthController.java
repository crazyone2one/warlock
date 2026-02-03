package cn.master.horde.controller;

import cn.master.horde.common.constants.RsaKey;
import cn.master.horde.common.result.ResultHolder;
import cn.master.horde.core.security.JwtTokenProvider;
import cn.master.horde.dto.AuthenticationRequest;
import cn.master.horde.dto.AuthenticationResponse;
import cn.master.horde.entity.SystemUser;
import cn.master.horde.entity.UserRoleRelation;
import cn.master.horde.util.RsaUtils;
import com.mybatisflex.core.query.QueryChain;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author : 11's papa
 * @since : 2026/1/15, 星期四
 **/

@NullMarked
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authenticate);
        Optional<List<String>> roles = QueryChain.of(SystemUser.class).where(SystemUser::getUserName).eq(request.username()).oneOpt()
                .map(user -> QueryChain.of(UserRoleRelation.class)
                        .where(UserRoleRelation::getUserId).eq(user.getId()).list()
                        .stream().map(UserRoleRelation::getRoleId).toList());
        String accessToken = jwtTokenProvider.generateToken(request.username(), roles.orElse(List.of()));
        String refreshToken = jwtTokenProvider.generateRefreshToken(request.username());
        return ResponseEntity.ok(new AuthenticationResponse(accessToken, refreshToken));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthenticationResponse> refreshToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String refreshToken = authorizationHeader.substring(7);

            if (jwtTokenProvider.validateRefreshToken(refreshToken)) {
                String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
                // 从数据库获取用户角色信息
                Optional<List<String>> roles = QueryChain.of(SystemUser.class)
                        .where(SystemUser::getUserName).eq(username).oneOpt()
                        .map(user -> QueryChain.of(UserRoleRelation.class)
                                .where(UserRoleRelation::getUserId).eq(user.getId()).list()
                                .stream().map(UserRoleRelation::getRoleId).toList());

                // 生成新的access token
                String newAccessToken = jwtTokenProvider.generateToken(username, roles.orElse(List.of()));
                // 返回新的access token和原来的refresh token
                return ResponseEntity.ok(new AuthenticationResponse(newAccessToken, refreshToken));
            }
        }

        // 如果refresh token无效，返回错误
        return ResponseEntity.status(401).body(new AuthenticationResponse(null, null));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        // 清除认证信息
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(null);
        SecurityContextHolder.clearContext();

        return ResponseEntity.ok().body(Map.of(
                "code", 100200,
                "message", "登出成功"
        ));
    }

    @GetMapping(value = "/get-key")
    @Operation(summary = "获取公钥")
    public ResultHolder getKey() throws Exception {
        RsaKey rsaKey = RsaUtils.getRsaKey();
        return ResultHolder.success(rsaKey.publicKey());
    }
}
