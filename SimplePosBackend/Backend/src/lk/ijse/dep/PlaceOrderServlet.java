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

@WebServlet(urlPatterns = "/placeorder/*")
public class PlaceOrderServlet extends HttpServlet {

    Connection connection = DBConnection.getInstnce().getConnection();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String pathInfo = req.getPathInfo();
        resp.setContentType("application/json");
        PrintWriter writer = resp.getWriter();
        if (pathInfo==null){
            try {
                JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
                ResultSet resultSet = connection.prepareStatement("select * from orderdetails").executeQuery();

                while (resultSet.next()){
                    JsonArrayBuilder arrayBuilder1 = Json.createArrayBuilder();
                    arrayBuilder.add(arrayBuilder1.add(Json.createObjectBuilder().add("orderid",resultSet.getString(1))
                            .add("cusid",resultSet.getString(2))
                            .add("orderdate",resultSet.getString(3)).build()));

                    ResultSet resultSet1 = connection.prepareStatement("select * from orderitems where orderid=" + resultSet.getString(1)).executeQuery();

                    while (resultSet1.next()){
                        arrayBuilder.add(arrayBuilder1.add(Json.createObjectBuilder().add("itemcode",resultSet1.getString(2))
                                .add("qty",resultSet1.getString(3)).build()));
                    }
                    arrayBuilder1.build();
                }
                writer.println(arrayBuilder.build().toString());
            } catch (SQLException e) {
                e.printStackTrace();
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }else {
            try {
                JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
                ResultSet resultSet = connection.prepareStatement("select * from orderdetails where orderid="+pathInfo.substring(1)).executeQuery();


                if (resultSet.next()){
                    JsonArrayBuilder arrayBuilder1 = Json.createArrayBuilder();
                    arrayBuilder.add(arrayBuilder1.add(Json.createObjectBuilder().add("orderid",resultSet.getString(1))
                            .add("cusid",resultSet.getString(2))
                            .add("orderdate",resultSet.getString(3)).build()));

                    ResultSet resultSet1 = connection.prepareStatement("select * from orderitems where orderid=" + resultSet.getString(1)).executeQuery();

                    while (resultSet1.next()){
                        arrayBuilder.add(arrayBuilder1.add(Json.createObjectBuilder().add("itemcode",resultSet1.getString(2))
                                .add("qty",resultSet1.getString(3)).build()));
                    }
                    arrayBuilder1.build();
                    writer.println(arrayBuilder.build().toString());
                }
                else {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                }

            } catch (SQLException e) {
                e.printStackTrace();
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        ServletInputStream inputStream = req.getInputStream();
        JsonReader reader = Json.createReader(inputStream);
        JsonArray jsonValues = reader.readArray();

        JsonArray jsonArray1 = jsonValues.getJsonArray(0);
        JsonArray jsonArray2 = jsonValues.getJsonArray(1);

        try {
            if ( !jsonArray1.getString(1).trim().equals("") && !jsonArray1.getString(2).trim().equals("")){
                connection.setAutoCommit(false);
                String orderid = String.valueOf(jsonArray1.getInt(0));

                PreparedStatement preparedStatement = connection.prepareStatement("insert into orderdetails values (?,?,?)");
                preparedStatement.setString(1, orderid);
                preparedStatement.setString(2,jsonArray1.getString(1));
                preparedStatement.setString(3,jsonArray1.getString(2));

                preparedStatement.executeUpdate();

                for (int i=0;i<jsonArray2.size();i++){
                    JsonObject jsonObject = jsonArray2.getJsonObject(i);

                    String itemcode = jsonObject.getString("itemcode");
                    String qty = jsonObject.getString("qty");

                    PreparedStatement preparedStatement1 = connection.prepareStatement("insert into orderitems values (?,?,?)");
                    preparedStatement1.setString(1,orderid);
                    preparedStatement1.setString(2,itemcode);
                    preparedStatement1.setString(3,qty);

                    preparedStatement1.executeUpdate();

                    int oldqty=0;
                    PreparedStatement ps1 = connection.prepareStatement("UPDATE item set qty=? where code=?");
                    PreparedStatement ps2 = connection.prepareStatement("select item.qty from item where item.code=?");

                    ps2.setString(1,itemcode);
                    ResultSet resultSet = ps2.executeQuery();
                    if (resultSet.next()){
                        oldqty = resultSet.getInt(1);
                    }

                    ps1.setString(1, String.valueOf(oldqty-Integer.parseInt(qty)));
                    System.out.println(String.valueOf(oldqty-Integer.parseInt(qty)));
                    ps1.setString(2,itemcode);
                    ps1.executeUpdate();


                }

                connection.commit();
                connection.setAutoCommit(true);
            }
            else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        ServletInputStream inputStream = req.getInputStream();
        JsonReader reader = Json.createReader(inputStream);
        JsonArray jsonValues = reader.readArray();

        JsonArray jsonArray1 = jsonValues.getJsonArray(1);
        JsonArray jsonArray2 = jsonValues.getJsonArray(2);

        try {
            if (!jsonArray1.getString(0).trim().equals("") && !jsonArray1.getString(1).trim().equals("") && !jsonArray1.getString(2).trim().equals("")){
                connection.setAutoCommit(false);

                PreparedStatement preparedStatement = connection.prepareStatement("update orderdetails set cusid=?,orderdate=? where orderid=?");
                preparedStatement.setString(1,jsonArray1.getString(1));
                preparedStatement.setString(2,jsonArray1.getString(2));
                preparedStatement.setString(3,jsonArray1.getString(0));

                preparedStatement.executeUpdate();

                for (int i=0;i<jsonArray2.size();i++){
                    JsonObject jsonObject = jsonArray2.getJsonObject(i);
                    PreparedStatement preparedStatement1 = connection.prepareStatement("update orderitems set qty=? where orderid=? and itemCode=?");
                    preparedStatement1.setString(2,jsonArray1.getString(1));
                    preparedStatement1.setString(3,jsonObject.getString("itemcode"));
                    preparedStatement1.setString(1,jsonObject.getString("qty"));

                    preparedStatement1.executeUpdate();
                }

                connection.commit();
            }
            else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }


    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletInputStream inputStream = req.getInputStream();
        JsonReader reader = Json.createReader(inputStream);
        Object read = reader.readObject();

        String code = ((JsonObject) read).getString("orderid");
        try {
            if (!code.trim().equals("")) {
                if ((connection.prepareStatement("delete from orderdetails where orderid=" + code).executeUpdate() < 1)) {
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
}
