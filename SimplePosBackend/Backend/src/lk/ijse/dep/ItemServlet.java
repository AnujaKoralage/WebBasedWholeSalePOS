package lk.ijse.dep;

import lk.ijse.dep.util.DBConnection;

import javax.json.*;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/item/*")
public class ItemServlet extends HttpServlet {

    private Connection connection = DBConnection.getInstnce().getConnection();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String pathInfo = req.getPathInfo();
        resp.setContentType("application/json");
        PrintWriter writer = resp.getWriter();

        if (pathInfo == null){
            try {
                ResultSet resultSet = connection.prepareStatement("select * from item").executeQuery();
                JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
                while (resultSet.next()){
                    JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
                    arrayBuilder.add(objectBuilder.add("code",resultSet.getString(1))
                            .add("description",resultSet.getString(2))
                            .add("qty",resultSet.getString(3))
                            .add("price",resultSet.getString(4)).build());
                }
                writer.println(arrayBuilder.build().toString());
            } catch (SQLException e) {
                e.printStackTrace();
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
        else {
            try {
                ResultSet resultSet = connection.prepareStatement("select *from item where code=" +pathInfo.substring(1)).executeQuery();

                JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
                if (resultSet.next()){
                    JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
                    arrayBuilder.add(objectBuilder.add("code",resultSet.getString(1))
                            .add("description",resultSet.getString(2))
                            .add("qty",resultSet.getString(3))
                            .add("price",resultSet.getString(4)).build());

                    writer.println(arrayBuilder.build().toString());
                }
                else {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                }


            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        ServletInputStream inputStream = req.getInputStream();
        JsonReader reader = Json.createReader(inputStream);
        Object read = reader.readObject();

        String code = ((JsonObject) read).getString("code");
        String description = ((JsonObject) read).getString("description");
        String qty = ((JsonObject) read).getString("qty");
        String price = ((JsonObject) read).getString("price");

        if (!code.trim().equals("") && !description.trim().equals("") && !qty.trim().equals("") && !price.trim().equals("")){
            try {
                PreparedStatement preparedStatement = connection.prepareStatement("insert into item values(?,?,?,?)");
                preparedStatement.setString(1,code);
                preparedStatement.setString(2,description);
                preparedStatement.setString(3,qty);
                preparedStatement.setString(4,price);

                if (!(preparedStatement.executeUpdate()>0)){
                    resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }

        }
        else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletInputStream inputStream = req.getInputStream();
        JsonReader reader = Json.createReader(inputStream);
        Object read = reader.readObject();

        String code = ((JsonObject) read).getString("code");
        try {
            if (!code.trim().equals("")) {
                if ((connection.prepareStatement("delete from item where code=" + code).executeUpdate() < 1)) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                } else {
                    resp.setStatus(HttpServletResponse.SC_OK);
                }
            } else
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (SQLException e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        String code = pathInfo.substring(1);
        if (pathInfo==null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
        else{
            ServletInputStream inputStream = req.getInputStream();
            JsonReader reader = Json.createReader(inputStream);
            Object read = reader.readObject();

            String description = ((JsonObject) read).getString("description");
            String qty = ((JsonObject) read).getString("qty");
            String price = ((JsonObject) read).getString("price");
            System.out.println(description+qty+price);

            if (!description.trim().equals("") && !qty.trim().equals("") && !price.trim().equals("")){
                try {
                    PreparedStatement preparedStatement = connection.prepareStatement("update item set description=?,qty=?,price=? where code=?");
                    preparedStatement.setString(1,description);
                    preparedStatement.setString(2,qty);
                    preparedStatement.setString(3,price);
                    preparedStatement.setString(4,code);
                    if (preparedStatement.executeUpdate() < 1) {
                        resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    } else {
                        resp.setStatus(HttpServletResponse.SC_OK);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
            }
            else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
    }

}
