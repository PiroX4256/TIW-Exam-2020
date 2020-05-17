package it.polimi.tiw.controllers;

import it.polimi.tiw.beans.Image;
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
import java.util.List;

@WebServlet("/GoToAlbumPage")
public class GoToAlbumPage extends HttpServlet {
    private static Connection connection;
    private TemplateEngine templateEngine;

    public void init() {
        connection = Initializer.connectionInit(getServletContext());
        templateEngine = Initializer.templateEngineInit(getServletContext());
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int albumId = Integer.parseInt(request.getParameter("album"));
        ImageDAO imageDAO = new ImageDAO(connection, albumId);
        List<Image> images;
        try {
            images = imageDAO.retrieveImages();
        }
        catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to retrieve album images");
            return;
        }
        WebContext webContext = new WebContext(request, response, getServletContext(), request.getLocale());
        webContext.setVariable("imageList", images);
        String path = Pages.albumPage;
        templateEngine.process(path, webContext, response.getWriter());
    }
}
