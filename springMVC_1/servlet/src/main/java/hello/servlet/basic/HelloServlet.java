package hello.servlet.basic;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// "/hello"로 요청하는 경우 처리
@WebServlet(name = "helloServlet", urlPatterns = "/hello")
public class HelloServlet extends HttpServlet {

    // Http 요청이 오면 WAS 의 Servlet Container 가 Request, Response를 새로 생성하여 Servlet에 전달한다.
    // HttpServletRequest, HttpServletResponse 는 모두 인터페이스 톰캣, Jetty, .. 등등의 WAS 서버가 해당 표준 인터페이스를 구현
    // HttpServletResponse 에 응답 데이터를 담는다.
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("HelloServlet.service");
        System.out.println("request = " + req);
        System.out.println("response = " + resp);

        String username = req.getParameter("username");
        System.out.println("username = " + username);

        resp.setContentType("text/plain");
        resp.setCharacterEncoding("utf-8");
        resp.getWriter().write("hello " + username);

    }
}
