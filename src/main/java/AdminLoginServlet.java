import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
@WebServlet("/admin_login")

public class AdminLoginServlet extends HttpServlet {

    private static final String url = "jdbc:mysql://localhost:3306/billing";
    private static final String pass = "Madhav@120403";
    private static final String user = "root";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        HttpSession session = request.getSession();
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(url, user, pass);
            stmt = conn.prepareStatement("SELECT * FROM admin WHERE email=? AND password=?");
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String email = rs.getString("email");
                String password1 = rs.getString("password");
                int org_id = rs.getInt("org_id");

                if (email.equals(username) && password1.equals(password)) {
                    // Generate JWT token
                    String token = JwtUtil.generateToken(username, "admin", Integer.toString(org_id));


                    // Add token to a cookie without "Bearer " prefix
                    Cookie tokenCookie = new Cookie("Authorization", token);
                    tokenCookie.setHttpOnly(true); // Prevents JavaScript access
                    tokenCookie.setSecure(true);
                    tokenCookie.setMaxAge(60 * 60); // Valid for 1 hour
                    response.addCookie(tokenCookie);
                    response.setHeader("Authorization", "Bearer " + token);


                    // Redirect to super admin page
                    response.sendRedirect("admin.html");
                } else {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid credentials");
                }
            } else {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid credentials");
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            // Close resources
            if (stmt != null) try { stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }


}
