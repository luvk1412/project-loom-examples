package performance.server;

import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

public abstract class JettyServer {

  private final Server server;
  private final int port;

  public JettyServer(int port) {
    server = new Server(threadPool());
    this.port = port;
  }

  public void start() throws Exception {
    ServerConnector connector = new ServerConnector(server, new HttpConnectionFactory());
    connector.setPort(port);
    server.addConnector(connector);
    ServletContextHandler handler = new ServletContextHandler(server, "/demo");
    handler.addServlet(BasicApi.class, "/hi");
    server.start();
  }

  protected abstract QueuedThreadPool threadPool();

  public static class PlatformThreadServer extends JettyServer {

    public PlatformThreadServer(int port) {
      super(port);
    }

    @Override
    protected QueuedThreadPool threadPool() {
      return new QueuedThreadPool(200, 100);
    }
  }

  public static class VirtualThreadServer extends JettyServer {

    public VirtualThreadServer(int port) {
      super(port);
    }

    @Override
    protected QueuedThreadPool threadPool() {
      QueuedThreadPool pool = new QueuedThreadPool(Integer.MAX_VALUE, 100);
      pool.setUseVirtualThreads(true);
      return pool;
    }
  }


  public static void main(String[] args) throws Exception {
    JettyServer server = new PlatformThreadServer(8080);
    server.start();
  }

}
