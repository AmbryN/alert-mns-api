package dev.ambryn.alertmntapi.socket;

import dev.ambryn.alertmntapi.beans.Channel;
import dev.ambryn.alertmntapi.beans.User;
import dev.ambryn.alertmntapi.repositories.UserRepository;
import dev.ambryn.alertmntapi.security.JwtUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Configuration
@EnableWebSocketMessageBroker
public class SocketBrokerConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtUtils jwtUtils;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/chat")
                .addInterceptors(getWebSocketInterceptor())
                .setAllowedOrigins("http://localhost:5173", "http://localhost:4200");
        //        registry.addEndpoint("/chat/")
        //                .setAllowedOrigins("http://localhost:5173")
        //                .withSockJS();
    }

    @Bean
    public HandshakeInterceptor getWebSocketInterceptor() {
        return new HandshakeInterceptor() {
            @Override
            public boolean beforeHandshake(ServerHttpRequest request,
                                           ServerHttpResponse response,
                                           WebSocketHandler wsHandler,
                                           Map<String, Object> attributes) throws Exception {
                Optional<String> oQuery = Optional.ofNullable(request.getURI()
                                                                     .getQuery());
                if (oQuery.isPresent()) {
                    String query = oQuery.get();
                    Long channelId = Long.parseLong(query.substring(8, query.indexOf("&")));
                    int tokenIndex = query.indexOf("token=");
                    String token = StringEscapeUtils.unescapeHtml4(query.substring(tokenIndex + 6));
                    if (token.startsWith("Bearer ")) {
                        Optional<List<Long>> oChannelIds = jwtUtils.getEmailFromBearer(token)
                                                                   .flatMap(email -> userRepository.findByEmail(email))
                                                                   .map(User::getChannels)
                                                                   .map(channels -> channels.stream()
                                                                                            .map(Channel::getId)
                                                                                            .toList());
                        if (oChannelIds.isPresent()) {
                            return oChannelIds.get()
                                              .contains(channelId);
                        }
                    }
                }
                return false;
            }

            @Override
            public void afterHandshake(ServerHttpRequest request,
                                       ServerHttpResponse response,
                                       WebSocketHandler wsHandler,
                                       Exception exception) {

            }
        };
    }
}
