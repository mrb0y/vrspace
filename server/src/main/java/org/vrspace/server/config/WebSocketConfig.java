package org.vrspace.server.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.vrspace.server.core.SessionManager;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
  @Autowired
  SessionManager sessionManager;

  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    // setAllowedOrigins or 403 forbidden when behind proxy
    registry.addHandler(sessionManager, "/vrspace").setAllowedOrigins("*");
  }
}