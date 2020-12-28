package it.polimi.db2.servlets;

import it.polimi.db2.auxiliary.images.ImageProcessor;
import it.polimi.db2.entities.Product;
import it.polimi.db2.entities.User;
import it.polimi.db2.services.ProductService;
import it.polimi.db2.services.UserService;
import jakarta.ejb.EJB;
import jakarta.ejb.EJBTransactionRolledbackException;
import jakarta.persistence.PersistenceException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.validator.routines.EmailValidator;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static it.polimi.db2.auxiliary.images.ImageProcessor.*;

@WebServlet("/UploadImage")
@MultipartConfig
public class UploadImage extends HttpServlet {
    @EJB(name = "it.polimi.db2.entities.services/ProductService")
    private ProductService productService;

    public static byte[] readImage(InputStream imageInputStream) throws IOException {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];// image can be maximum of 4MB
        int bytesRead = -1;

        try {
            while ((bytesRead = imageInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            byte[] imageBytes = outputStream.toByteArray();
            return imageBytes;
        } catch (IOException e) {
            throw e;
        }

    }

    private void sendBackError(String error, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain");
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().println(error);
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        InputStream imgStream = request.getPart("image").getInputStream();
        byte[] file = readImage(imgStream);

        //file = ImageProcessor.AI_Cropper(file);
        switch (getImageType(file)) {
            case "image/png":
                //convert to jpeg
                file = toByteArray(prepareImage(file, true), "jpg");
                break;

            case "image/jpeg":
                file = toByteArray(prepareImage(file, false), "jpg");
                break;
            default:
                sendBackError("format not supported. It would probably jeopardize the whole web application", response);
                return;


        }

        if (file.length < 2048) {
            sendBackError("file is too large. It would probably jeopardize the whole web application", response);
            return;
        }
        Product product = productService.getProductOfTheDay();
        productService.dummyImageLoad(product, file);
        //write image to db
        request.getParameter("imgId");

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
