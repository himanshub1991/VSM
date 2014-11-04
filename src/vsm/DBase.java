

package vsm;
import java.sql.*;
class DBase
{
    public DBase()
    {
    }
    //connect with mysql
    public Connection connect(String db_connect_str,String db_userid, String db_password)
    {
        Connection conn;
        try 
        {
            Class.forName("com.mysql.jdbc.Driver").newInstance();

            conn = DriverManager.getConnection(db_connect_str,db_userid, db_password);
        
        }
        catch(Exception e)
        {
            e.printStackTrace();
            conn = null;
        }

        return conn;    
    }
    //create new table
    public void createTable(Connection conn, String sql)
    {
        Statement stmt;
        try
        {
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);
            //String query = "ALTER TABLE records AUTO_INCREMENT=1";
        }catch(Exception e){
            e.printStackTrace();
            stmt = null;
        }
    }
    //insert into table from txt file
    public void importData(Connection conn,String filename)
    {
        Statement stmt;
        String query,query1;

        try
        {
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
            query = "LOAD DATA  INFILE '"+filename+ "' IGNORE INTO TABLE records fields TERMINATED BY ',' lines terminated by '\r\n' (app_no,title,abstract);";
            query1 = "LOAD DATA  INFILE '"+filename+ "' IGNORE INTO TABLE terms fields TERMINATED BY ',' lines terminated by '\r\n' (term,doc_no,frequency);";
            stmt.executeUpdate(query1);
        }catch(Exception e){
            e.printStackTrace();
            stmt = null;
        }
    }
};