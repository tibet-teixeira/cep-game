package tesky.cep.model;

import java.io.Serializable;
import java.net.Socket;

public class SocketHandler implements Serializable {
    private Socket socket;

    public SocketHandler() {
    }

    public SocketHandler(Socket socket) {
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }
}