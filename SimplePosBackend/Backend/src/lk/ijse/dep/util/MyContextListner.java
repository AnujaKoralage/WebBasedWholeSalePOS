package lk.ijse.dep.util;

import org.apache.commons.dbcp2.BasicDataSource;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
@WebListener
public class MyContextListner implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        File file = new File("E:\\DEP\\Completed Projects\\JavaServletFullWeb\\SimplePosBackend\\Backend\\resourses\\DBConnection.propertie");
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Properties dbPro = new Properties();
        try {
            dbPro.load(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String ip = dbPro.getProperty("ip");
        String port = dbPro.getProperty("port");
        String database = dbPro.getProperty("database");
        String username = dbPro.getProperty("user");
        String password = dbPro.getProperty("password");

        BasicDataSource bds = new BasicDataSource();

        bds.setDriverClassName("com.mysql.jdbc.Driver");
        bds.setUsername(username);
        bds.setPassword(password);
        bds.setUrl("jdbc:mysql://"+ip+":"+port+"/"+database);

        bds.setMaxTotal(20);
        bds.setInitialSize(20);
        bds.setMaxIdle(20);

        sce.getServletContext().setAttribute("pool",bds);
    }
}
