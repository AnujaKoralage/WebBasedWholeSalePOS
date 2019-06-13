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

@WebServlet(urlPatterns = "/customer/*")
public class CustomerServlet extends HttpServlet {

    private Connection connection = DBConnection.getInstnce().getConnection();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String pathInfo = req.getPathInfo();
        resp.setContentType("application/json");
        PrintWriter writer = resp.getWriter();

        if (pathInfo == null){
            try {
                ResultSet resultSet = connection.prepareStatement("select * from customer").executeQuery();
                JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
                while (resultSet.next()){
                    JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
                    arrayBuilder.add(objectBuilder.add("id",resultSet.getString(1))
                                                .add("name",resultSet.getString(2))
                                                .add("address",resultSet.getString(3)).build());
                }
                writer.println(arrayBuilder.build().toString());
            } catch (SQLException e) {
                e.printStackTrace();
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
        else {
            try {
                ResultSet resultSet = connection.prepareStatement("select *from customer where id=" +pathInfo.substring(1,2)).executeQuery();

                    JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
                    if (resultSet.next()){
                        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
                        arrayBuilder.add(objectBuilder.add("id",resultSet.getString(1))
                                .add("name",resultSet.getString(2))
                                .add("address",resultSet.getString(3)).build());

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

        String id = ((JsonObject) read).getString("id");
        String name = ((JsonObject) read).getString("name");
        String address = ((JsonObject) read).getString("address");

        if (!id.trim().equals("") && !name.trim().equals("") && !address.trim().equals("")){
            try {
                PreparedStatement preparedStatement = connection.prepareStatement("insert into customer values(?,?,?)");
                preparedStatement.setString(1,id);
                preparedStatement.setString(2,name);
                preparedStatement.setString(3,address);

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

        String id = ((JsonObject) read).getString("id");
        System.out.println(id);
        try {
            if (!id.trim().equals("")) {
                if ((connection.prepareStatement("delete from customer where id='" + id  +"'").executeUpdate() < 1)) {
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
        String id = pathInfo.substring(1);
        if (pathInfo==null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
        else{
            ServletInputStream inputStream = req.getInputStream();
            JsonReader reader = Json.createReader(inputStream);
            Object read = reader.readObject();

            String name = ((JsonObject) read).getString("name");
            String address = ((JsonObject) read).getString("address");

            if (!name.trim().equals("") && !address.trim().equals("")){
                try {
                    PreparedStatement preparedStatement = connection.prepareStatement("update customer set name=?,address=? where id=?");
                    preparedStatement.setString(1,name);
                    preparedStatement.setString(2,address);
                    preparedStatement.setString(3,id);
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
