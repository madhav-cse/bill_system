<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.sql.*, java.util.*, java.io.*, java.util.*" %>
<%@ page import="java.time.format.DateTimeFormatter, java.time.LocalDateTime" %>
<%@ page import="java.sql.Date" %>

<html>
<head>
  <title>Invoice Print</title>
</head>
<body>
<h1> Invoice </h1>
<%

  int org_id = Integer.parseInt(request.getAttribute("org_id").toString());
  final String url = "jdbc:mysql://localhost:3306/billing";
  final String pass = "Madhav@120403";
  final String user = "root";
  System.out.println("Outside try block");
  try {
    System.out.println("Inside try block org_id = "+org_id);
    Class.forName("com.mysql.cj.jdbc.Driver");
    Connection conn = DriverManager.getConnection(url, user, pass);
    PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM invoice WHERE org_id = ?");

    pstmt.setInt(1, org_id);

    ResultSet rs = pstmt.executeQuery();

    while(rs.next()){
      int customerId = rs.getInt("customer_id");
      System.out.println("customer_id = "+customerId);
      PreparedStatement customerStatement = conn.prepareStatement("SELECT name, phno FROM customer WHERE id = ?");
      customerStatement.setInt(1, customerId);
      ResultSet customer = customerStatement.executeQuery();
      while(customer.next()){
%>
<div>
  <span>Customer Name : <%= customer.getString("name")%></span><br>
  <span>Customer Phone : <%= customer.getString("phno")%></span>><br>
</div>
<%
  }
%>
<div>
  <span>Invoice Id : <%= rs.getInt("id")%></span><br>
  <span>Invoice Date : <%= rs.getDate("invoice_date")%></span><br>
  <span>Sub-total: <%= rs.getFloat("sub_total")%></span><br>
  <span>Discount: <%= rs.getFloat("discount")%></span><br>
  <span>Cgst : <%= rs.getFloat("cgst")%></span><br>
  <span>Sgst : <%= rs.getFloat("sgst")%></span><br>
  <span>Total : <%= rs.getFloat("net_total")%></span><br>
</div>
<table>
  <tr>
    <th>Item Name                  </th>
    <th>Rate     </th>
    <th>Quantity </th>
    <th>Price    </th>
  </tr>


<%
  PreparedStatement lineItemStatement= conn.prepareStatement("SELECT * FROM line_item WHERE inv_id = ?");
  lineItemStatement.setInt(1, rs.getInt("id"));
  ResultSet lineItems = lineItemStatement.executeQuery();
  while(lineItems.next()){
%>
<tr>
  <td><%= lineItems.getString("name")%></td>
  <td><%= lineItems.getFloat("rate")%></td>
  <td><%= lineItems.getInt("qty")%></td>
  <td><%= lineItems.getFloat("amt")%></td>
</tr>
</table>

<%


      }



    }
    System.out.println("Came out of while");

  }
  catch (Exception e){
    System.out.println(e.getMessage());

  }


%>
</body>
</html>
