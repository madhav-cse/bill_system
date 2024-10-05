
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.sql.*, java.util.*, java.io.*, java.util.*" %>
<%@ page import="java.time.format.DateTimeFormatter, java.time.LocalDateTime" %>
<%@ page import="java.sql.Date" %>

<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>Create Invoice</title>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.2.1/css/all.min.css">
  <!--- Styles --->
  <style>
    body {
      font-family: 'Arial', sans-serif;
      margin: 50px;
      background-color: #ffffff;
    }

    h1 {
      text-align: center;
      color: #333;
      font-size: 3em;
      margin-bottom: 20px;
    }

    .container {
      width: 80%;
      margin: 0 auto;
      padding: 20px;
      border: 1px solid #ddd;
      border-radius: 10px;
      background-color: #fff;
      box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
      position: relative;
    }

    .logo {
      text-align: center;
      margin-bottom: 20px;
    }

    .logo img {
      max-width: 100%;
      height: auto;
      max-height: 200px;
    }

    .form-group {
      margin-bottom: 15px;
    }

    .form-group label {
      display: block;
      margin-bottom: 5px;
      font-weight: bold;
    }

    .form-group input, .form-group select {
      width: 100%;
      padding: 8px;
      box-sizing: border-box;
      border: 1px solid #ccc;
      border-radius: 4px;
    }

    .form-group input::placeholder {
      color: #999;
    }

    .btn-primary, .btn-success {
      background-color: #007bff;
      color: #fff;
      border: none;
      border-radius: 4px;
      cursor: pointer;
      transition: background-color 0.3s ease;
    }

    .btn-primary:hover, .btn-success:hover {
      background-color: #0056b3;
    }

    .item-container {
      display: flex;
      justify-content: space-between;
      margin-bottom: 10px;
    }

    .item-container input, .item-container select {
      width: 45%;
      padding: 8px;
      border: 1px solid #ccc;
      border-radius: 4px;
    }

    .notification {
      margin-top: 20px;
      padding: 10px;
      border: 1px solid #ddd;
      background-color: #e1f5fe;
      border-radius: 5px;
      text-align: center;
    }

    .small-btn {
      width: 100px;
      padding: 10px;
      background-color: #007bff;
      color: #fff;
      border: none;
      border-radius: 4px;
      cursor: pointer;
      transition: background-color 0.3s ease;
      position: absolute;
      top: 20px;
      right: 20px;
    }

    .small-btn:hover {
      background-color: #0056b3;
    }

  </style>
</head>
<body>

<div class="container">
  <h1>Create Invoice</h1>
  <form id="invoiceForm" method="post" action="new-invoice.jsp">
    <div class="form-group">
      <label for="customerPhone">Customer Phone:</label>
      <input type="text" name="customerPhone" id="customerPhone" placeholder="Enter Customer Phone">
    </div>
    <div class="form-group">
      <label for="customerName">Customer Name:</label>
      <input type="text" name="customerName" id="customerName" placeholder="Enter Customer Name">
    </div>
    <div id="items">
      <div class="item-container">
        <select name="itemName" class="item-name-dropdown">
          <option value="">Select Item</option>
          <%
            int org_id = Integer.parseInt(request.getAttribute("org_id").toString());
            final String url = "jdbc:mysql://localhost:3306/billing";
            final String pass = "Madhav@120403";
            final String user = "root";
            try {
              Class.forName("com.mysql.cj.jdbc.Driver");
              Connection conn = DriverManager.getConnection(url,user,pass);
              PreparedStatement stmt = conn.prepareStatement("SELECT name FROM item WHERE org_id = ?");
              stmt.setInt(1,org_id);
              ResultSet rs = stmt.executeQuery();
              while (rs.next()) {
                String itemName = rs.getString("name");
                out.println("<option value='" + itemName + "'>" + itemName + "</option>");
              }
              rs.close();
              stmt.close();
              conn.close();
            } catch (Exception e) {
              e.printStackTrace();
            }
          %>
        </select>
        <input type="number" name="quantity" placeholder="Quantity">
      </div>
    </div>
    <button type="button" onclick="addItem()" class="btn btn-success">Add Another Item</button>
    <div class="form-group">
      <label for="discount">Discount (%):</label>
      <input type="number" step="1.00" name="discount" id="discount" placeholder="Enter Discount Percentage">
    </div>
    <div class="form-group">
      <label for="cgst">CGST (%):</label>
      <input type="number" step="1.00" name="cgst" id="cgst" placeholder="Enter CGST percentage">
    </div>
    <div class="form-group">
      <label for="sgst">SGST (%):</label>
      <input type="number" step="1.00" name="sgst" id="sgst" placeholder="Enter SGST Percentage">
    </div>

    <button type="submit" class="btn btn-primary" onclick="return validateForm()">Create Invoice</button>
    <%
      String customerPhone = request.getParameter("customerPhone");
      String customerName = request.getParameter("customerName");
      String[] itemNames = request.getParameterValues("itemName");
      String[] quantities = request.getParameterValues("quantity");
      float discount = request.getParameter("discount") != null && !request.getParameter("discount").isEmpty() ? Float.parseFloat(request.getParameter("discount")) : 0.0f;
      float cgst = request.getParameter("cgst") != null && !request.getParameter("cgst").isEmpty() ? Float.parseFloat(request.getParameter("cgst")) : 0.0f;
      float sgst = request.getParameter("sgst") != null && !request.getParameter("sgst").isEmpty() ? Float.parseFloat(request.getParameter("sgst")) : 0.0f;




      Connection conn = null;



      if (customerPhone != null && customerName != null && itemNames != null && quantities != null) {
        try {

          Class.forName("com.mysql.cj.jdbc.Driver");
          conn = DriverManager.getConnection(url,user,pass);
          conn.setAutoCommit(false);
          PreparedStatement customerStatement = conn.prepareStatement("SELECT id FROM customer WHERE name = ? AND phno = ? AND org_id = ?");
          customerStatement.setString(1,customerName);
          customerStatement.setString(2,customerPhone);
          customerStatement.setInt(3,org_id);
          ResultSet rs = customerStatement.executeQuery();
          int customerId;
          rs.next();
          customerId = rs.getInt("id");




          // Insert invoice
          PreparedStatement psInvoice = conn.prepareStatement("INSERT INTO Invoice (invoice_date, sub_total, discount, cgst, sgst, net_total, customer_id, org_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
          psInvoice.setDate(1, Date.valueOf(java.time.LocalDate.now()));



          float subtotal = 0.0f;

          // Fetch item_rate and calculate subtotal
          for (int i = 0; i < itemNames.length; i++) {
            System.out.printf(itemNames[i]);
            System.out.println("org_id = " + org_id);
            if(!itemNames[i].equals("Select Item")) {
              PreparedStatement psItem = conn.prepareStatement("SELECT selling_price FROM item WHERE name = ? AND org_id  = ?");
              psItem.setString(1, itemNames[i]);
              psItem.setInt(2, org_id);
              ResultSet rsItem = psItem.executeQuery();
              rsItem.next();
              float rate = rsItem.getFloat("selling_price");

              subtotal += Integer.parseInt(quantities[i]) * rate;


            }

          }

          float discountAmount = subtotal * (discount / 100.00f);
          float discountedAmount = subtotal - discountAmount;
          float total = discountedAmount + subtotal*(cgst/100.00f) + subtotal*(sgst/100.00f);

          psInvoice.setFloat(2, subtotal);
          psInvoice.setFloat(3, discountAmount);
          psInvoice.setFloat(4, subtotal*(cgst/100));
          psInvoice.setFloat(5, subtotal*(sgst/100));
          psInvoice.setFloat(6, total);
          psInvoice.setInt(7, customerId);
          psInvoice.setInt(8, org_id);

          psInvoice.executeUpdate();

          rs = psInvoice.getGeneratedKeys();
          rs.next();
          int invoiceId = rs.getInt(1), rr;
          session.setAttribute("inv_id", invoiceId);

          // Insert bill items and update item quantities
          for (int i = 0; i < itemNames.length; i++) {
            PreparedStatement psItem = conn.prepareStatement("SELECT selling_price FROM item WHERE name = ? AND org_id = ?");
            psItem.setString(1, itemNames[i]);
            psItem.setInt(2, org_id);
            ResultSet rsItem = psItem.executeQuery();

            if (rsItem.next()) {
              float rate = rsItem.getFloat("selling_price");
              PreparedStatement psBillItem = conn.prepareStatement("INSERT INTO line_item (name, rate, qty, amt, inv_id) VALUES (?, ?, ?, ?, ?)");

              psBillItem.setString(1, itemNames[i]);
              psBillItem.setFloat(2, rate);
              psBillItem.setFloat(3, Integer.parseInt(quantities[i]));
              psBillItem.setFloat(4, rate*Integer.parseInt(quantities[i]));
              psBillItem.setInt(5, invoiceId);
              rr = psBillItem.executeUpdate();


            }
          }

          conn.commit();
          response.sendRedirect("invoices.jsp?inv_id=" + invoiceId);
          out.println("<div class='notification'>Invoice created successfully. Total: Rs." + total + "</div>");

        } catch (Exception e) {
          try {
            if (conn != null) {
              conn.rollback();
            }
          } catch (SQLException rollbackException) {
            rollbackException.printStackTrace();
          }
          e.printStackTrace();
          out.println("<div class='notification'>Error: " + e.getMessage() + "</div>");
        } finally {
          try {
            if (conn != null) {
              conn.close();
            }
          } catch (SQLException ex) {
            ex.printStackTrace();
          }
        }

      }





    %>
  </form>
  <button onclick="window.location.href = 'index.jsp';" class="btn btn-secondary small-btn">Home</button>
</div>
<script>
  function addItem() {
    var itemDiv = document.getElementById('items');
    var newItem = document.createElement('div');
    newItem.className = 'item-container';
    newItem.innerHTML = '<select name="itemName" class="item-name-dropdown">' +
            document.querySelector('.item-name-dropdown').innerHTML +
            '</select>' +
            '<input type="text" name="quantity" placeholder="Quantity">';
    itemDiv.appendChild(newItem);
  }

  function validateForm() {
    var customerPhone = document.getElementById('customerPhone').value;
    var customerName = document.getElementById('customerName').value;

    if (customerPhone === "" || customerName === "") {
      alert("Please enter both customer phone and customer name.");
      return false;
    }

    return true;



  }
</script>
</body>
</html>