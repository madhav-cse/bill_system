import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class Register extends HttpServlet {
    private static final String url = "jdbc:mysql://localhost:3306/billing";
    private static final String pass = "Madhav@120403";
    private static final String user = "root";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get form parameters
        String name = request.getParameter("name");
        String address = request.getParameter("address");
        String gstin = request.getParameter("gstin");
        String phno = request.getParameter("phno");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        Connection conn = null;
        PreparedStatement orgStmt = null;
        PreparedStatement adminStmt = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Use updated MySQL driver
            conn = DriverManager.getConnection(url, user, pass);
            conn.setAutoCommit(false); // Disable auto-commit

            orgStmt = conn.prepareStatement("INSERT INTO Organization (name, address, gstin, phno) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            orgStmt.setString(1, name);
            orgStmt.setString(2, address);
            orgStmt.setString(3, gstin);
            orgStmt.setString(4, phno);
            int orgUpdate = orgStmt.executeUpdate();

            // Check if the Organization insert was successful
            if (orgUpdate > 0) {
                ResultSet rs = orgStmt.getGeneratedKeys(); // Get generated keys
                if (rs.next()) {
                    int orgId = rs.getInt(1); // Get the generated organization ID

                    adminStmt = conn.prepareStatement("INSERT INTO super_admin (email, password, org_id) VALUES (?, ?, ?)");
                    adminStmt.setString(1, email);
                    adminStmt.setString(2, password); // Consider hashing the password before storing it
                    adminStmt.setInt(3, orgId);
                    int adminUpdate = adminStmt.executeUpdate();

                    // Check if the super_admin insert was successful
                    if (adminUpdate > 0) {
                        conn.commit(); // Commit the transaction if both inserts were successful
                    } else {
                        conn.rollback(); // Rollback if the super_admin insert failed
                    }
                } else {
                    conn.rollback(); // Rollback if the generated keys could not be retrieved
                }
            } else {
                conn.rollback(); // Rollback if the Organization insert failed
            }

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback on SQL exception
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            // Close resources
            try {
                if (adminStmt != null) adminStmt.close();
                if (orgStmt != null) orgStmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
