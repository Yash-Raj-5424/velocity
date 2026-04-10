package com.velocity.gateway.filter;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.velocity.gateway.user.UserRegisterdto;
import com.velocity.gateway.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.text.ParseException;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserSyncFilter implements WebFilter {

    private final UserService userService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String userId = exchange.getRequest().getHeaders().getFirst("X-User-Id");
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
        UserRegisterdto userRegisterdto = getUserDetails(token);

        if (userId == null) {
            userId = userRegisterdto.getKeycloakId();
        }

        if(userId != null && token != null){
            String finalUserId = userId;
            return userService.validateUser(userId)
                    .flatMap(exist -> {
                        if(!exist){
                            if(userRegisterdto != null){
                                return userService.registerUser(userRegisterdto)
                                        .then(Mono.empty());
                            } else {
                                return Mono.empty();
                            }
                        }else{
                            log.info("User with ID {} already exists, skipping registration");
                            return Mono.empty();
                        }
                    })
                    .then(Mono.defer(() -> {
                        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                                .header("X-User-Id", finalUserId)
                                .build();

                        return chain.filter(exchange.mutate().request(mutatedRequest).build());
                    }));
        }

        return chain.filter(exchange);
    }

    private UserRegisterdto getUserDetails(String token) {
        try{
            String tokenWithoutBearer = token.replace("Bearer ", "");
            SignedJWT signedJWT = SignedJWT.parse(tokenWithoutBearer);
            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();

            UserRegisterdto registerdto = new UserRegisterdto();
            registerdto.setEmail(claimsSet.getStringClaim("email"));
            registerdto.setKeycloakId(claimsSet.getStringClaim("sub"));
            registerdto.setFirstName(claimsSet.getStringClaim("given_name"));
            registerdto.setLastName(claimsSet.getStringClaim("family_name"));
            registerdto.setPassword("defaultPassword");

            return registerdto;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
