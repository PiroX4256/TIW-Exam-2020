package it.polimi.tiw.controllers;

import it.polimi.tiw.dao.ImageDAO;
import it.polimi.tiw.utils.Initializer;
import it.polimi.tiw.utils.Pages;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet("/ShowImageDetails")
public class ShowImageDetails extends HttpServlet {
    private static Connection connection;
    private static TemplateEngine templateEngine;

    public void init() {
        connection = Initializer.connectionInit(getServletContext());
        templateEngine = Initializer.templateEngineInit(getServletContext());
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int image = Integer.parseInt(request.getParameter("image"));
        ImageDAO imageDAO = new ImageDAO(connection, image);
        try {
            imageDAO.getImageById();
        }
        catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to get the desired image.");
            return;
        }
        WebContext webContext = new WebContext(request, response, getServletContext(), request.getLocale());
        webContext.setVariable("image", image);
        String path = Pages.albumPage;
        templateEngine.process(path, webContext, response.getWriter());
    }
}
