import io.jsonwebtoken.Claims;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/*")
public class JwtAuthorizationFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String requestURI = httpRequest.getRequestURI();
        if (requestURI.endsWith("home.html") || requestURI.endsWith("super-admin-login.html") || requestURI.endsWith("register.html") || requestURI.endsWith("admin-login.html")|| requestURI.endsWith("login")) {
            // Allow requests to these pages without authentication
            chain.doFilter(request, response);
            return;
        }
        Cookie[] cookies = httpRequest.getCookies();
        String token = null;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("Authorization".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        if (token != null) {
            try {
                String username = JwtUtil.extractUsername(token);

                // Extract all claims from the token
                Claims claims = JwtUtil.extractAllClaims(token);
                String role = claims.get("role", String.class);
                // Check role for authorization
                if ("super_admin".equals(role)) {
                    // Allow access to admin resources
                    if(requestURI.endsWith("super-admin.html") || requestURI.endsWith("all-invoices.jsp")|| requestURI.endsWith("new-admin.html") || requestURI.endsWith("new-admin") || requestURI.endsWith("new-item") || requestURI.endsWith("new-item.html")) {
                        request.setAttribute("org_id", claims.get("org_id", String.class));
                        ((HttpServletResponse) response).setHeader("WWW-Authenticate", "Bearer " + token);
                        chain.doFilter(request, response);
                    }

                } else if (("admin").equals(role)){
                    // User access, proceed with the request
                    if(requestURI.endsWith("admin.html") || requestURI.endsWith("invoices.jsp") || requestURI.endsWith("new-invoice.jsp") || requestURI.endsWith("customer.html") || requestURI.endsWith("new-customer") ) {
                        request.setAttribute("org_id", claims.get("org_id", String.class));
                        chain.doFilter(request, response);
                    }
                }

            } catch (Exception e) {
                response.getWriter().println(e.getMessage());
                if(e!=null) {
                    e.printStackTrace();
                }

                //httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
            }
        } else {
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token not found");
        }
    }
}
