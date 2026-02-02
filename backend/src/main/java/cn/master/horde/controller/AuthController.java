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
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * @author : 11's papa
 * @since : 2026/1/15, 星期四
 **/


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
        String token = jwtTokenProvider.generateToken(request.username(), roles.orElse(List.of()));
        return ResponseEntity.ok(new AuthenticationResponse(token, null));
    }

    @PostMapping("/logout")
    public void logout() {
        // authenticationService.logout();
        System.out.println("logout");
    }

    @GetMapping(value = "/get-key")
    @Operation(summary = "获取公钥")
    public ResultHolder getKey() throws Exception {
        RsaKey rsaKey = RsaUtils.getRsaKey();
        return ResultHolder.success(rsaKey.publicKey());
    }
}
