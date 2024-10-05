import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/new-item")

public class Item extends HttpServlet {
    private static final String url = "jdbc:mysql://localhost:3306/billing";
    private static final String pass = "Madhav@120403";
    private static final String user = "root";



    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String itemName = request.getParameter("name");
        float price = Float.parseFloat(request.getParameter("selling_price"));
        int org_id = Integer.parseInt(request.getAttribute("org_id").toString());
        Connection conn = null;
        PrintWriter out = response.getWriter();
        String header = request.getAttribute("org_id").toString();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Use updated MySQL driver
            response.setContentType("text/html");

            conn = DriverManager.getConnection(url, user, pass);
            PreparedStatement stmt = conn.prepareStatement("insert into item (name, selling_price, org_id) values(?,?,?)");
            stmt.setString(1, itemName);
            stmt.setFloat(2, price);
            stmt.setInt(3, org_id);
            int ins = stmt.executeUpdate();

            if(ins > 0) {
                response.sendRedirect("new-item.html");
            }
            else{
                out.println("<html><head></head><body><p> "+"Item not inserted"+" </p></body></html>");
            }
        } catch (Exception e) {
                out.println("<html><head></head><body><p>"+ e.getMessage() + "Exception inserted"+" </p></body></html>");
        }


    }

}
