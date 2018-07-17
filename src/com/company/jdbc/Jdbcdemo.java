package com.company.jdbc;

import com.sun.javafx.binding.StringFormatter;
import java.sql.*;
import java.util.Scanner;

/**
 * Created by qy on 2018/7/14.
 * 演示通过jbdc连接到数据库和进行增、删改、查的操作
 */
public class Jdbcdemo {
    //获取一个数据库连接对象
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
    void insertData(int id, String user, String password) {
        Statement statement=null;
        Connection connection = null;
        try {
            //创建数据库的链接
            connection = getConnection();
            //构建添加数据的SQL语句
            String sql = "insert into account " +
                    " VALUES (" + id + ",'" + user + "','" + password + "')";
            //执行SQL语句
           statement = connection.createStatement();
            //得到执行结果，确定是否添加成功
            int rows = statement.executeUpdate(sql);
            System.out.println("插入的行数为：" + rows);
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            close(connection,statement,null);
        }
    }

    //删除
    private void deleteData(int id) {
        Statement statement=null;
        Connection connection = null;
        try {
            //创建数据库链接
           connection = getConnection();
            //构建删除的SQL语句
            String sql = "DELETE  from account where id="+id;
            //执行删除语句
            statement = connection.createStatement();
            //获取执行所影响的行数，判断是否执行成功
            int rows = statement.executeUpdate(sql);
            System.out.println("有" + rows + "被删除,删除成功！");
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            close(connection,statement,null);
        }
    }

    //更新
    private void update(int id, String user,String password) {
        Statement statement=null;
        Connection connection = null;
        try {
            //创建数据库链接
            connection = getConnection();
            //创建updat语句
            String sql = "Update account set user='" + user +"',password='"+password+"' where id="+id;
            //执行SQL语句
            statement = connection.createStatement();
            //获取执行所影响的行数，判断是否执行成功
            int rows = statement.executeUpdate(sql);
            System.out.println("所更新的结果为：" + (rows > 0));
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            close(connection,statement,null);
        }
    }


    //以二维数组的形式查找数据，但是不显示
    String [][] bestFindAllData(){
        //申明一个100*3的数组，代表100行3列
        String [][] datas=new String[100][3];
        //获取数据库链接
        Connection connection=getConnection();
        Statement statement=null;
        ResultSet resultSet=null;
        //构建查询的sql
        String sql="select * from account";
        try{
            //执行sql，并获得结果集
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            //4. 遍历结果集，输出每条记录的信息。
            int index = 0;
            while(resultSet.next()) {
                int id = resultSet.getInt("id");
                String user = resultSet.getString("user");
                String password = resultSet.getString("password");
                datas[index][0] = id +"";
                datas[index][1] = user;
                datas[index][2] = password;
                index ++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(connection,statement,resultSet);
        }
        return datas;
    }

//以表的形式显示数据
void  findAllDataFormatOutput(){
       String [][] datas=bestFindAllData();
        //遍布结果集，输出每条记录的信息
       System.out.println("您全部的数据为：");
        StringBuffer buffer=new StringBuffer();
        buffer.append("======================================================================================================" + System.lineSeparator());
        buffer.append("        id                            user                             spassword"+System.lineSeparator());
        buffer.append("======================================================================================================="+System.lineSeparator());
        for(int i=0;i<datas.length;i++){
            String [] values=datas[i];
            //因为返回的数组里可能包含多余数据，所以需要过滤
            if (values[0] != null && values[1] != null && values[2] != null){
                buffer.append(
                        String .format(
                                "%s\t\t\t\t\t\t\t|%s\t\t\t\t\t\t\t|%s\t\t\t\t\t\t\t",values[0],values[1],values[2]));
                buffer.append(System.lineSeparator());
            }
        }
        System.out.println(buffer.toString());
    }

    //使用id查询用户
    void findAccoutDataById(int id){
        Connection connection = getConnection();
        Statement statement=null;
        ResultSet resultSet=null;
        //创建updat语句
        String sql = "SELECT  *  FROM account WHERE id="+id;
        try{
            //执行SQL，并获得结果集
            statement=connection.createStatement();
            resultSet=statement.executeQuery(sql);
            //遍布结果集，输出每条记录的信息
            StringBuffer buffer=new StringBuffer();
            System.out.println("您好，您对id为"+id+"查询的结果是：");
            buffer.append("======================================================================================================" + System.lineSeparator());
            buffer.append("        id                            user                             spassword"+System.lineSeparator());
            buffer.append("======================================================================================================="+System.lineSeparator());
            while(resultSet.next()){
                id = resultSet.getInt("id");
                String user=resultSet.getString("user");
                String password=resultSet.getString("password");
                buffer.append("\t\t"+id+"\t\t\t\t\t\t\t "+user+" \t\t\t\t\t\t\t"+password+" \t\t\t\t\t\t"+System.lineSeparator());
            }
            System.out.println(buffer.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            close(connection,statement,resultSet);
        }
    }

    /*
   * 模糊搜索数据，根据用户输入的关键字来模糊查询
     */
    private void findAccountDataLikeKey(String keyWord) {
        //获取数据库链接
        Connection connection = getConnection();
        Statement statement=null;
        ResultSet resultSet=null;
        //创建updat语句
        String sql ="SELECT  id,user,password  FROM account "+"WHERE user LIKE '%" + keyWord+"%' or password like'%"+keyWord+"%'";
        try{
            //执行SQL，并获得结果集
            statement=connection.createStatement();
            resultSet=statement.executeQuery(sql);
            StringBuffer buffer=new StringBuffer();
            System.out.println("关于您对关键字"+keyWord+"的查询结果为：");
            buffer.append("======================================================================================================" + System.lineSeparator());
            buffer.append("        id                            user                             spassword"+System.lineSeparator());
            buffer.append("======================================================================================================="+System.lineSeparator());
            while(resultSet.next()){
                int id = resultSet.getInt("id");
                String user=resultSet.getString("user");
                String password=resultSet.getString("password");
                buffer.append("\t\t"+id+"\t\t\t\t\t\t\t "+user+" \t\t\t\t\t\t\t"+password+" \t\t\t\t\t\t"+System.lineSeparator());
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

    public static void  main(String[] args) {
        Jdbcdemo demo = new Jdbcdemo();
        demo.bestFindAllData();
        demo.findAllDataFormatOutput();
        demo.findAccoutDataById(3);
        demo.findAccountDataLikeKey("u");
        Scanner scaner = new Scanner(System.in);
        while (true) {
            System.out.println("================================================================================================");
            System.out.println("                                           欢迎使用人工智能系统                                 ");
            System.out.println("================================================================================================");
            System.out.println("      1.添加数据     2.修改数据     3.删除数据     4.退出系统    ");
            System.out.println("请选择你的操作。。。。");
            int select = 0;
            select = scaner.nextInt();
            while (select < 1 || select > 4) {
                System.out.println("选择的操作，系统不能够识别，请重新输入。。。。。");
                select = scaner.nextInt();
            }
            String value = null;
            Jdbcdemo jdbcdemo = new Jdbcdemo();
            if (select == 1) {
                //添加数据
                System.out.println("请输入要添加的账号和密码，中间用逗号分隔。举例：dgh，68");
                value = scaner.next();
                String[] values = value.split(",");
                jdbcdemo.insertData((int) System.currentTimeMillis(),
                        values[0], values[1]);
            } else if (select == 2) {
                //修改数据
                System.out.println("请输入要修改的账号和密码、ID，中间用逗号分隔。");
                value = scaner.next();
                String[] values = value.split(",");
                jdbcdemo.update(Integer.parseInt(values[0]), values[1], values[2]);
            } else if (select == 3) {
                //删除数据
                System.out.println("请输入要删除的id");
                value = scaner.next();
                String[] values = value.split(",");
                jdbcdemo.deleteData(Integer.parseInt(value));
            } else if (select == 4) {
                //退出系统
                System.out.println("退出系统");
                System.exit(-1);
            }
        }
    }
}