package it.polimi.tiw.controllers;

import it.polimi.tiw.dao.ImageDAO;
import it.polimi.tiw.utils.Initializer;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.apache.tomcat.util.http.fileupload.servlet.ServletRequestContext;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/InsertImage")
public class InsertImage extends HttpServlet {
    private static Connection connection;
    private static final String SAVE_DIR = "uploads" + File.separator + "images";

    public void init() {
        connection = Initializer.connectionInit(getServletContext());
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, String> fieldMapValue = handleRequest(request, response);
        int albumId = Integer.parseInt(fieldMapValue.get("album"));
        String title = fieldMapValue.get("title");
        String description = fieldMapValue.get("description");
        String fileName = fieldMapValue.get("fileName");
        Date date = Date.valueOf(fieldMapValue.get("date"));
        ImageDAO imageDAO = new ImageDAO(connection, albumId);
        try {
            imageDAO.createNewImage(title, fileName, date, description);
        }
        catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to insert image!");
            return;
        }
        response.sendRedirect(getServletContext().getContextPath() + "/GoToAlbumPage?album=" + albumId);
    }

    protected Map<String, String> handleRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HashMap<String, String> fieldMapValue = new HashMap<>();
        ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
        File file;
        String profilePicturePath = getServletContext().getRealPath("") + File.separator + SAVE_DIR + File.separator;
        File uploadDir = new File(profilePicturePath);
        if(!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        try {
            List<FileItem> list = upload.parseRequest(new ServletRequestContext(request));
            for (FileItem item: list){
                if(item.isFormField()) {
                    fieldMapValue.put(item.getFieldName(), item.getString());
                }
                else {
                    String fileName = item.getName();
                    if (fileName.lastIndexOf("\\") >= 0) {
                        file = new File(profilePicturePath + fileName.substring(fileName.lastIndexOf("\\")));
                    } else {
                        file = new File(profilePicturePath + fileName.substring(fileName.lastIndexOf("\\") + 1));
                    }
                    item.write(file);
                    fieldMapValue.put("fileName", SAVE_DIR + File.separator + fileName);
                }
            }
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error during parsing form input!");
            return null;
        }
        return fieldMapValue;
    }
}
