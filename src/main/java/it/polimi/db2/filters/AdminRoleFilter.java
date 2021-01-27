package it.polimi.db2.filters;

import java.io.IOException;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@jakarta.servlet.annotation.WebFilter(filterName = "AdminRoleFilter")
public class AdminRoleFilter implements jakarta.servlet.Filter {
    public void destroy() {
    }

    public void doFilter(jakarta.servlet.ServletRequest req, jakarta.servlet.ServletResponse resp, jakarta.servlet.FilterChain chain) throws jakarta.servlet.ServletException, IOException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        String loginPath = req.getServletContext().getContextPath() + "/index.html";

        HttpSession session = request.getSession();

        //if it's not logged it
        if (session.isNew() || session.getAttribute("admin") == null) {
            session.invalidate();
            response.sendRedirect(loginPath);
            (response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().println("error: unauthorized user, please log in as an admin");
            return;
        }

        chain.doFilter(req, resp);
    }

    public void init(jakarta.servlet.FilterConfig config) throws jakarta.servlet.ServletException {

    }

}
