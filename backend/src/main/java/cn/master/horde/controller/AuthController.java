package cn.master.horde.controller;

import cn.master.horde.common.annotation.Loggable;
import cn.master.horde.core.security.JwtTokenProvider;
import cn.master.horde.dao.AuthenticationRequest;
import cn.master.horde.dao.AuthenticationResponse;
import cn.master.horde.entity.SystemUser;
import cn.master.horde.entity.UserRoleRelation;
import com.mybatisflex.core.query.QueryChain;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @Loggable("用户登录")
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
    }
}
