package com.example.DuAnTotNghiepKs.controller;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@WebSocket
public class WebRTCSignalingServer {

    private static Set<Session> sessions = new HashSet<>();

    @OnWebSocketConnect
    public void onConnect(Session session) throws IOException {
        sessions.add(session);
        System.out.println("Client đã kết nối: " + session.getRemoteAddress().getAddress());
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        sessions.remove(session);
        System.out.println("Client đã ngắt kết nối: " + session.getRemoteAddress().getAddress());
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        // Gửi thông điệp cho tất cả các client khác
        for (Session s : sessions) {
            if (s.isOpen() && !s.equals(session)) {
                s.getRemote().sendString(message);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Server server = new Server(8080);
        ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        handler.setContextPath("/");

        server.setHandler(handler);

        handler.addServlet(WebSocketServletImpl.class, "/ws");

        server.start();
        System.out.println("Signaling Server chạy tại ws://localhost:8080/ws");
        server.join();
    }

    @WebServlet(name = "WebSocketServlet", urlPatterns = {"/ws"})
    public static class WebSocketServletImpl extends WebSocketServlet {
        @Override
        public void configure(WebSocketServletFactory factory) {
            factory.register(WebRTCSignalingServer.class);
        }
    }
}
