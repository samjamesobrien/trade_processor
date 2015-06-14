package obrien.websockets;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Web socket for the {@link obrien.processing.Trends} resource.
 */
public class TrendsSocket extends WebSocketAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(TrendsSocket.class);

    private static Set<Session> sessions = new CopyOnWriteArraySet<>();

    @Override
    public void onWebSocketConnect(Session session) {
        super.onWebSocketConnect(session);
        sessions.add(session);
        LOG.info("Socket Connected: {}", Integer.toHexString(session.hashCode()));
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        sessions.remove(getSession());
        super.onWebSocketClose(statusCode, reason);
        LOG.info("Socket Closed: {} {}", statusCode, reason);
    }

    @Override
    public void onWebSocketError(Throwable cause) {
        super.onWebSocketError(cause);
        LOG.error("Websocket error", cause);
    }

    @Override
    public void onWebSocketText(String message) {
        LOG.info("Got text {} from {}", message, Integer.toHexString(getSession().hashCode()));
    }

    /**
     * Send the message to the web socket.
     * @param message string to be sent.
     */
    public static void broadcast(String message) {
        sessions.forEach(session -> {
            try {
                session.getRemote().sendString(message);
            } catch (IOException e) {
                LOG.error("Problem broadcasting message", e);
            }
        });
    }
}
