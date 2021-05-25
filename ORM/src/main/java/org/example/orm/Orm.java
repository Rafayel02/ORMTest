package org.example.orm;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Orm {

    private final Connection connection;

    public Orm(String database, String host, String port, String db, String user, String password) throws Exception{
        StringBuilder url = new StringBuilder();
        url.append("jdbc:")
                .append(database)
                .append("://")
                .append(host)
                .append(":")
                .append(port)
                .append("/")
                .append(db);
        connection = DriverManager.getConnection(url.toString(), user, password);
    }

    public void register(Class<?> clazz) throws Exception {
        StringBuilder sql = new StringBuilder();
        sql.append("create table if not exists ")
                .append(clazz.getSimpleName()+"Table")
                .append("(")
                .append(getFields(clazz))
                .append(")");

        Statement statement = connection.createStatement();
        statement.execute(sql.toString());
    }

    public void save(Object obj) throws Exception{
        StringBuilder sql = new StringBuilder();
        sql.append("insert into ")
                .append(obj.getClass().getSimpleName()+"Table ")
                .append(setValuesFor(obj));

        Statement statement = connection.createStatement();
        statement.executeUpdate(sql.toString());

        System.out.println("User has been saved successfully!");
    }

    public <T> void getAll(Class<T> clazz) throws Exception {
        String sql = "select * from "+clazz.getSimpleName()+"Table";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        List<T> resultList = new ArrayList<>();

        while(resultSet.next()) {

            //Creating new instance if exists row
            Constructor<T> constructor = clazz.getConstructor();
            T currentObj = constructor.newInstance();

            //Getting values from db by fields names and setting object fields values
            Field[] fields = clazz.getDeclaredFields();
            for(Field field : fields) {
                field.setAccessible(true);
                field.set(currentObj, resultSet.getString(field.getName().toString()));
                field.setAccessible(false);
            }
            resultList.add(currentObj);
        }
        resultList.stream().forEach(e -> System.out.println(e.toString()));
    }

    public void deleteByParam(String paramType, String paramValue, Class<?> clazz) throws Exception{
        StringBuilder sql = new StringBuilder();
        sql
                .append("delete from ")
                .append(clazz.getSimpleName()+"Table ")
                .append("where ")
                .append(paramType)
                .append(" = ")
                .append("'")
                .append(paramValue)
                .append("'");
        Statement statement = connection.createStatement();
        statement.execute(sql.toString());
        System.out.println("User has been deleted successfully!");
    }

    public void delete(Object obj) {

    }

    private String setValuesFor(Object obj) throws Exception{
        StringBuilder sb = new StringBuilder();
        HashMap<String, String> keyValue = getMapOf(obj);

        sb.append("(");
        for(String field: keyValue.keySet()) {
            sb.append(field)
                    .append(",");
        }
        sb.deleteCharAt(sb.length()-1).append(")");
        sb.append(" values(");
        for(String field: keyValue.values()) {
            sb.append("'")
                    .append(field)
                    .append("'")
                    .append(",");
        }
        sb.deleteCharAt(sb.length()-1).append(")");

        return sb.toString();
    }

    private HashMap<String, String> getMapOf(Object obj) throws Exception{
        HashMap<String, String> map = new HashMap<>();
        Field[] fields = obj.getClass().getDeclaredFields();
        for(Field field: fields) {
            field.setAccessible(true);
            Object value = field.get(obj);
            map.put(field.getName(), value.toString());
        }
        return map;
    }

    //Getting fields in String for sql format
    private String getFields(Class<?> clazz) {
        StringBuilder sb = new StringBuilder();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            sb.append(field.getName())
                    .append(" ")
                    .append(convertToSqlType(field.getType()))
                    .append(",");
        }
        sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }

    private String convertToSqlType(Class<?> type) {
        String typeName = type.getSimpleName();
        if("String".equals(typeName)) {
            return "text";
        }
        return null;
    }

}
