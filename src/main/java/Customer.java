import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/new-customer")
public class Customer extends HttpServlet {
    private static final String url = "jdbc:mysql://localhost:3306/billing";
    private static final String pass = "Madhav@120403";
    private static final String user = "root";


    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        String phno = request.getParameter("phno");
        String street = request.getParameter("street");
        String city = request.getParameter("city");
        String area = request.getParameter("area");
        String landmark = request.getParameter("landmark");
        String pincode = request.getParameter("pincode");
        int org_id = Integer.parseInt(request.getAttribute("org_id").toString());

        Connection conn = null;
        PreparedStatement stmt = null, subst = null;
        ResultSet rs = null;
        PrintWriter out = response.getWriter();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(url, user, pass);
            conn.setAutoCommit(false);
            // SQL query for inserting customer details
            String sql = "INSERT INTO customer (name, phno, org_id) VALUES (?,?,?)";
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);// Prepare statement to return the generated keys
            stmt.setString(1, name);
            stmt.setString(2, phno);
            stmt.setInt(3, org_id);

            int affectedRows = stmt.executeUpdate();

            // Check if the insert was successful and retrieve the generated primary key
            if (affectedRows > 0) {
                rs = stmt.getGeneratedKeys();  // Get the generated keys
                if (rs.next()) {
                    int primaryKey = rs.getInt(1);  // Retrieve the primary key
                    subst = conn.prepareStatement("INSERT INTO customer_address (street, area, landmark, city, pincode, customer_id) VALUES (?,?,?,?,?,?)");
                    subst.setString(1, street);
                    subst.setString(2, area);
                    subst.setString(3, landmark);
                    subst.setString(4, city);
                    subst.setString(5, pincode);
                    subst.setInt(6, primaryKey);
                    int affectedRows1 = subst.executeUpdate();
                    if (affectedRows1 > 0) {
                        conn.commit();
                        response.sendRedirect("admin.html");
                    }
                    else{
                        conn.rollback();
                        out.println("Failed to add customer 2nd query to the database");
                    }
                } else {
                    conn.rollback();
                    out.println("Failed to 1st query add customer to the database");
                }
            } else {
                conn.rollback();
                out.println("Failed to add customer to the database");
            }

        } catch (Exception e) {
            out.println("Failed Exception"+e.getMessage());
        }

    }
}
