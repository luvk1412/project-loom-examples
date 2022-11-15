package performance.server;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import static utils.Utils.sleep;

public class BasicApi extends HttpServlet {

  private Random random = new Random();
  private AtomicLong requestCount = new AtomicLong();

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    sleep(random.nextInt(50, 200));
    resp.setContentType("application/json");
    resp.setStatus(HttpServletResponse.SC_OK);
    resp.getWriter().println("{ \"status\": \"ok\"}");
    System.out.println("GET request received: " + requestCount.incrementAndGet());
  }
}
