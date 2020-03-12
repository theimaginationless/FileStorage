package Server;

import Common.Const;
import java.io.IOException;
import java.util.logging.LogManager;
import java.util.logging.Logger;

class Server  {
    private static Logger logger = Logger.getLogger(Server.class.getName());
    public static void main(String[] args) {
        try {
            LogManager.getLogManager().readConfiguration(Server.class.getResourceAsStream(Const.loggerConfiguration));
        } catch(IOException | NullPointerException ex) {
            System.err.println("[" + Thread.currentThread().getId() + "] Cannot read configuration file '" + Const.loggerConfiguration + "'");
            System.exit(1);
        }
        ConnectionHandler cHandler = new ConnectionHandler();
    }
}
