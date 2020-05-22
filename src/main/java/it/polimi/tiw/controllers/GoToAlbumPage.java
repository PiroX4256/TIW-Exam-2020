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
import java.util.ArrayList;
import java.util.List;

@WebServlet("/GoToAlbumPage")
public class GoToAlbumPage extends HttpServlet {
    private static Connection connection;
    private static TemplateEngine templateEngine;

    @Override
    public void init() {
        connection = Initializer.connectionInit(getServletContext());
        templateEngine = Initializer.templateEngineInit(getServletContext());
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int albumId = Integer.parseInt(request.getParameter("album"));
        int pageId = Integer.parseInt(request.getParameter("page"));
        ImageDAO imageDAO = new ImageDAO(connection, albumId);
        List<Image> images;
        try {
            images = imageDAO.retrieveImages();
        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to retrieve album images");
            return;
        }
        if(((pageId+1)*5)<images.size() && request.getParameter("dir")!=null && request.getParameter("dir").equals("next")) pageId++;
        else if(pageId>0 && request.getParameter("dir")!=null && request.getParameter("dir").equals("prev")) pageId--;
        int imageStart = pageId * 5;
        int imageEnd = imageStart + 4;
        List<Image> imagesTemp;
        imagesTemp = new ArrayList<>();
        for (int i = imageStart; i <= imageEnd; i++) {
            if (images.size() > i) {
                imagesTemp.add(images.get(i));
            }
        }
        redirect(request, response, pageId, images, imagesTemp);
    }

    public void redirect(HttpServletRequest request, HttpServletResponse response, int pageId, List<Image>images, List<Image>imagesTemp) throws IOException {
        WebContext webContext = new WebContext(request, response, getServletContext(), request.getLocale());
        webContext.setVariable("pageId", pageId);
        webContext.setVariable("imageList", imagesTemp);
        webContext.setVariable("imageFull", images);
        String path = Pages.ALBUM_PAGE_HTML;
        templateEngine.process(path, webContext, response.getWriter());
    }
}
