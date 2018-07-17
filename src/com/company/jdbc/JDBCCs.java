package com.company.jdbc;

import java.sql.*;
import java.util.Scanner;

/**
 * Created by qy on 2018/7/17.
 */
public class JDBCCs {
    private Connection getConnection() {
        //加载驱动
        try {
            Class.forName("com.mysql.jdbc.Driver");
            //创建连接字符串
            String dbURL = "jdbc:mysql://127.0.0.1:3306/lx?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
            //创立数据库链接2
            Connection connection = DriverManager.getConnection(dbURL, "admin", "qlpjane");
            return connection;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    //添加数据
    private void insertData(int id, String book_name,String book_publishers,String book_author){
        Statement statement=null;
        Connection connection = null;
        try {
            //创建数据库的链接
            connection = getConnection();
            //构建添加数据的SQL语句
            String sql = "insert into book(id,book_name,book_publishers,book_author) " +
                    " VALUES ("+id+",'" + book_name + "','" + book_publishers + "','"+book_author +"')";
            //执行SQL语句
            statement = connection.createStatement();
            //得到执行结果，确定是否添加成功
            int rows= statement.executeUpdate(sql);
            System.out.println("您插入了"+rows+"行，插入成功！");
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            close(connection,statement,null);
        }
    }

    //修改数据
    private void updateData( int id,String book_name,String book_publishers,String book_author) {
        Statement statement = null;
        Connection connection = null;
        try {
            //创建数据库链接
            connection = getConnection();
            //创建updat语句
            String sql = "Update book set book_name='" + book_name + "',book_publishers='" + book_publishers + "',book_author='" + book_author + "' where id=" + id;
            //执行SQL语句
            statement = connection.createStatement();
            //获取执行所影响的行数，判断是否执行成功
            int rows = statement.executeUpdate(sql);
            System.out.println("您更新的结果为：" + (rows > 0));
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(connection, statement, null);
        }
    }

    //删除数据
    private  void  deleteData(int id){
        Statement statement=null;
        Connection connection = null;
        try {
            //创建数据库链接
            connection = getConnection();
            //构建删除的SQL语句
            String sql = "DELETE  from book where id="+id;
            //执行删除语句
            statement = connection.createStatement();
            //获取执行所影响的行数，判断是否执行成功
            int rows = statement.executeUpdate(sql);
            System.out.println("有" + rows + "行被删除,删除成功！");
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            close(connection,statement,null);
        }
    }

    //以二维数组的形式查找数据，但是不显示
    private String [][] bestFindAllData(){
        //申明一个100*5的数组，代表100行5列
        String [][] datas=new String[100][5];
        //获取数据库链接
        Connection connection=getConnection();
        Statement statement=null;
        ResultSet resultSet=null;
        //构建查询的sql
        String sql="select * from book";
        try{
            //执行sql，并获得结果集
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            //4. 遍历结果集，输出每条记录的信息。
            int index = 0;
            while(resultSet.next()) {
                int id = resultSet.getInt("id");
                String book_name = resultSet.getString("book_name");
                String book_pubshers = resultSet.getString("book_publishers");
                String book_author=resultSet.getString("book_author");
                String create_time=resultSet.getString("create_time");
                datas[index][0] = id +"";
                datas[index][1] = book_name;
                datas[index][2] = book_pubshers;
                datas[index][3] = book_author;
                datas[index][4] = create_time;
                index ++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(connection,statement,resultSet);
        }
        return datas;
    }


    //查询所有数据
    private  void  findAllDataFormatOutput(){
        String [][] datas=bestFindAllData();
        //遍布结果集，输出每条记录的信息
        System.out.println("您全部的数据为：");
        StringBuffer buffer=new StringBuffer();
        buffer.append("=====================================================================================================================" + System.lineSeparator());
        buffer.append("        id                         book_name                         book_publishers                  book_author"+System.lineSeparator());
        buffer.append("====================================================================================================================="+System.lineSeparator());
        for(int i=0;i<datas.length;i++){
            String [] values=datas[i];
            //因为返回的数组里可能包含多余数据，所以需要过滤
            if (values[0] != null && values[1] != null && values[2] != null && values[3]!=null){
                buffer.append(
                        String .format(
                                "%s\t\t\t\t\t\t\t|%s\t\t\t\t\t\t\t|%s\t\t\t\t\t\t\t",values[0],values[1],values[2]));
                buffer.append(System.lineSeparator());
            }
        }
        System.out.println(buffer.toString());
    }


    /*
   * 模糊搜索数据，根据用户输入的关键字（书名，出版商，作者）来模糊查询
     */
    private void findBookDataLikeKey(String keyWord) {
        //获取数据库链接
        Connection connection = getConnection();
        Statement statement=null;
        ResultSet resultSet=null;
        //创建updat语句
        String sql ="SELECT  id,book_name,book_publishers,book_author,create_time  FROM book "+"WHERE book_name LIKE '%" + keyWord+"%' or book_publishers like'%"+keyWord+"%' or book_author like'%"+keyWord+"%'";
        try{
            //执行SQL，并获得结果集
            statement=connection.createStatement();
            resultSet=statement.executeQuery(sql);
            StringBuffer buffer=new StringBuffer();

            buffer.append("======================================================================================================" + System.lineSeparator());
            buffer.append("        id                   book_name                 book_publishers             book_author"+System.lineSeparator());
            buffer.append("======================================================================================================="+System.lineSeparator());
            while(resultSet.next()){
                int id = resultSet.getInt("id");
                String book_name=resultSet.getString("book_name");
                String book_publishers=resultSet.getString("book_publishers");
                String book_author=resultSet.getString("book_author");
                buffer.append("\t\t"+id+"\t\t\t\t\t\t\t "+book_name+" \t\t\t\t\t\t\t"+book_publishers+" \t\t\t\t\t\t"+book_author+" \t\t\t\t\t\t"+System.lineSeparator());
            }
            System.out.println(buffer.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            close(connection,statement,resultSet);
        }
    }


    private  void  close(Connection connection,Statement statement,ResultSet resultSet){
        try{
            if (statement!=null){
                statement.close();
            }
            if(connection!=null){
                connection.close();
            }
            if (resultSet!=null){
                resultSet.close();
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static void  main(String[] args){
        JDBCCs demo=new JDBCCs();
        demo.bestFindAllData();
        demo.findAllDataFormatOutput();
        Scanner scaner=new Scanner(System.in);
        while(true){
            System.out.println("================================================================================");
            System.out.println("                                 欢迎使用人工智能系统                          ");
            System.out.println("================================================================================");
            System.out.println("      1.添加数据     2.修改数据     3.删除数据     4.模糊查询     5.退出系统    ");
            System.out.println("请选择你的操作。。。。");
            int select=0;
            select=scaner.nextInt();
            while(select < 1 || select > 5){
                System.out.println("选择的操作，系统不能够识别，请重新输入。。。。。");
                select=scaner.nextInt();
            }
            String value=null;
            JDBCCs jdbcCs=new JDBCCs();
            if(select==1){
                //添加数据
                System.out.println("请输入要添加的书名，出版社和作者，中间用逗号分隔。举例：C语言,电子科大出版社,郑丽");
                value = scaner.next();
                String[] values = value.split(",");
                jdbcCs.insertData((int) System.currentTimeMillis(),
                        values[0], values[1],values[2]);
            }else if(select==2){
                //修改数据
                System.out.println("请输入要修改的ID，书名，出版社，作者，中间用逗号分隔。");
                value=scaner.next();
                String [] values=value.split(",");
                jdbcCs.updateData(Integer.parseInt( values[0]),values[1],values[2],values[3]);
            }else if(select==3){
                //删除数据
                System.out.println("请输入要删除的id");
                value=scaner.next();
                jdbcCs.deleteData(Integer.parseInt(value));
            } else if (select!=4) {
                //退出系统
                System.out.println("退出系统" );
                System.exit(-1);
            } else {
                //查看全部数据
                System.out.println("请输入要查询的关键字。");
                value=scaner.next();
                jdbcCs.findBookDataLikeKey("");
            }
        }
    }
}


