package global;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;

public class sql_connection {
	public Statement connect() throws SQLException {
		Connection conn= DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=count_letters", "oscar", "1234");
		Statement st = conn.createStatement();
		return st;
//		ResultSet rs = st.executeQuery("select * from config_game");
//		while(rs.next()) {
//			System.out.println(rs.getString("property")+": "+rs.getString("val"));	
//		}
	}
	
}
