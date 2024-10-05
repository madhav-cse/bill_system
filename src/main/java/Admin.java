import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/new-admin")

public class Admin extends HttpServlet {
    private static final String url = "jdbc:mysql://localhost:3306/billing";
    private static final String pass = "Madhav@120403";
    private static final String user = "root";



    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String admin_mail = request.getParameter("mail");
        String password = request.getParameter("pass");
        int org_id = Integer.parseInt(request.getAttribute("org_id").toString());
        Connection conn = null;
        PrintWriter out = response.getWriter();


        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Use updated MySQL driver
            response.setContentType("text/html");

            conn = DriverManager.getConnection(url, user, pass);
            PreparedStatement stmt = conn.prepareStatement("insert into admin (email, password, org_id) values(?,?,?)");
            stmt.setString(1, admin_mail);
            stmt.setString(2, password);
            stmt.setInt(3, org_id);
            int ins = stmt.executeUpdate();

            if(ins > 0) {
                System.out.println("admin added");
                response.sendRedirect("new-admin.html");
            }
            else{
                out.println("<html><head></head><body><p> "+"Item not inserted"+" </p></body></html>");
            }
        } catch (Exception e) {
            out.println("<html><head></head><body><p>"+ e.getMessage() + "Exception inserted"+" </p></body></html>");
        }


    }

}
