package es.nicolas.config.websockets;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
public class WebSocketHandler extends TextWebSocketHandler implements SubProtocolCapable, WebSocketSender {
    private final String entity; // Entidad que se notificar

    public WebSocketHandler(String entity) {this.entity = entity;}

    // Sesiones de clientes conectados. Los recorremos y enviamos mensajes (patrón observer)
    // es concurrente porque puede ser compartida por varios hilos
    private final Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();


    // session == sesión del cliente
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        log.info("Conexión establecida con el servidor");
        log.info("Sesión: {}", session);
        sessions.add(session);
        TextMessage message = new TextMessage("Updates Web socket: " + entity + " - (App de Alumnos)");
        log.info("Servidor envia: {}", message);
        session.sendMessage(message);
    }


    // Cuando se cierra la conexión con el servidos
    // status == estado de la conexion
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus  status) throws IOException {
        log.info("Conexión cerrada con el servidor: {}", status);
        sessions.remove(session);
    }


    // Envia mensaje a todos los clientes conectados
    @Override
    public void sendMessage(String message) throws IOException {
        log.info("Enviar mensaje de cambio en la entidad: {} : {}", entity, message);
        // Mensaje para todos los clientes
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                log.info("Servido WS envía: {}", message);
                session.sendMessage(new TextMessage(message));
            }
        }
    }

    @Scheduled(fixedRate = 5000) // Cada 5 segundos
    @Override
    public void sendPeriodicMessages() throws IOException {
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                String broadcast = "server periodic message " + LocalTime.now();
                log.info("Server sends: {}",  broadcast);
                session.sendMessage(new TextMessage(broadcast));
            }
        }
    }


    // Maneja los mensajes que llegan del servidor
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // No hacemos nada, si fuera un chat lo gestionariamos aquí
        // Leemos el mensaje y lo enviaámos a todos los clientes conectados
        //    String request = message.getPayload();
        //    log.info("Server received: " + request);
        //    String response = String.format("response from server to '%s'", HtmlUtils.htmlEscape(request));
        //    log.info("Server sends: " + response);
        //    session.sendMessage(new TextMessage(response));
    }

    // Manejamos los errores de transporte que llegan al servidor,
    // con la excepcion que se ha producido por parametros
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.info("Error de transporte con el servidor: {}", exception.getMessage());
    }

    // Devuelve los subprotocolos que soporta el servidor
    @Override
    public List<String> getSubProtocols() {
        return List.of("subprotocol.demo.websocket");
    }
}
