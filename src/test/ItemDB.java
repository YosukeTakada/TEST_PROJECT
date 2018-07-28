package test;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 作業項目データベースへのアクセステストクラス。
 */
public class ItemDB {
	
	/**
	 * テーブル名。
	 */
	private static final String TABLE_NAME = "TODO_ITEM";

	/**
	 * テスト処理を実行します。
	 * @param args
	 */
	public static void main(String[] args) {
		ItemDB itemDB = new ItemDB();
		
		try{
			// オブジェクトを生成
			itemDB.create();
			
			// データ操作
			itemDB.execute(args);
		}catch(Throwable t) {
			t.printStackTrace();
		}finally{
			// オブジェクトを破棄
			itemDB.close();
		}
	}
	
	/**
	 * Connectionオブジェクトを保持します。
	 */
	private Connection _connection;
	
	/**
	 * Statementオブジェクトを保持します。
	 */
	private Statement _statement;
	
	/**
	 * 構築します。
	 */
	public ItemDB() {
		_connection = null;
		_statement = null;
	}
	
	/**
	 * オブジェクトを生成します。
	 */
	public void create()
		throws ClassNotFoundException, SQLException{
		// 下準備
		Class.forName("org.h2.Driver");
		_connection = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/test", "sa", "");
		_statement = _connection.createStatement();
	}
	
	/**
	 * 各種オブジェクトを閉じます。
	 */
	public void close() {
		if(_statement != null) {
			try{
				_statement.close();
			}catch(SQLException e) {
				;
			}
			_statement = null;
		}
		if(_connection != null) {
			try{
				_connection.close();
			}catch(SQLException e) {
				;
			}
			_connection = null;
		}
	}
	
	/**
	 * 実行します。
	 * @param args
	 * @throws SQLException
	 */
	public void execute(String[] args)
		throws SQLException {
		String command = args[0];
		if("select".equals(command)) {
			executeSelect();
		}else if("insert".equals(command)) {
			// FinishedDateは任意。存在しなければnull扱い
			String finishedDate = null;
			if(args.length > 4) {
				finishedDate = args[4];
			}
			executeInsert(args[1], args[2], args[3], finishedDate);
		}else if("update".equals(command)) {
			// FinishedDateは任意。存在しなければnull扱い
			String finishedDate = null;
			if(args.length > 5) {
				finishedDate = args[5];
			}
			executeUpdate(args[1], args[2], args[3], args[4], finishedDate);
		}else if("delete".equals(command)) {
			executeDelete(args[1]);
		}
	}
	
	/**
	 * SELECT処理を実行します。
	 */
	private void executeSelect()
		throws SQLException{
		ResultSet resultSet = _statement.executeQuery("SELECT * FROM " + TABLE_NAME);
		try{
			boolean br = resultSet.first();
			if(br == false) {
				return;
			}
			do{
				String id = resultSet.getString("ID");
				String name = resultSet.getString("NAME");
				String user = resultSet.getString("USER");
				Date expireDate = resultSet.getDate("EXPIRE_DATE");
				Date finishedDate = resultSet.getDate("FINISHED_DATE");
				
				System.out.println("id: " + id + ", name: " + name + ", user: " + user + ", expireDate: " + expireDate + ", finishedDate: " + finishedDate);
			}while(resultSet.next());
		}finally{
			resultSet.close();
		}
	}
	
	/**
	 * INSERT処理を実行します。
	 * @param id
	 * @param name
	 * @param password
	 */
	private void executeInsert(String name, String user, String expireDate, String finishedDate)
		throws SQLException{
		// SQL文を発行
		String finishedDateValue = "null";
		if(finishedDate != null) {
			finishedDateValue = "'" + finishedDate + "'";
		}
		int updateCount = _statement.executeUpdate("INSERT INTO " + TABLE_NAME + " (NAME,USER,EXPIRE_DATE,FINISHED_DATE) VALUES ('"+name+"','"+user+"','"+expireDate+"', " + finishedDateValue + ")");
		System.out.println("Insert: " + updateCount);
	}
	
	/**
	 * UPDATE処理を実行します。
	 * @param id
	 * @param name
	 * @param password
	 */
	private void executeUpdate(String id, String name, String user, String expireDate, String finishedDate)
		throws SQLException{
		// SQL文を発行
		String finishedDateValue = "null";
		if(finishedDate != null) {
			finishedDateValue = "'" + finishedDate + "'";
		}
		int updateCount = _statement.executeUpdate("UPDATE " + TABLE_NAME + " SET NAME='"+name+"', USER='"+user+"', EXPIRE_DATE='"+expireDate+"', FINISHED_DATE="+finishedDateValue+" WHERE ID='" + id + "'");
		System.out.println("Update: " + updateCount);
	}
	
	/**
	 * DELETE処理を実行します。
	 * @param id
	 */
	private void executeDelete(String id)
		throws SQLException{
		// SQL文を発行
		int updateCount = _statement.executeUpdate("DELETE FROM " + TABLE_NAME + " WHERE ID='" + id + "'");
		System.out.println("Delete: " + updateCount);
	}
	
}
