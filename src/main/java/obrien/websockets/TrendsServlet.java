package obrien.websockets;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

/**
 * Servlet for the {@link obrien.websockets.TrendsSocket} socket.
 */
public class TrendsServlet extends WebSocketServlet {

    @Override
    public void configure(WebSocketServletFactory factory) {
        factory.register(TrendsSocket.class);
    }
}
