package com.frameworkset.sqlexecutor;

import java.sql.SQLException;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.frameworkset.common.poolman.SQLExecutor;
import com.frameworkset.common.poolman.util.SQLUtil;

public class CustomPoolTest {

	public CustomPoolTest() {
		// TODO Auto-generated constructor stub
	}
	@BeforeClass
	public static void init()
	{
		SQLUtil.startPool("test",//数据源名称
				"oracle.jdbc.driver.OracleDriver",//oracle驱动
				"jdbc:oracle:thin:@(description=(address_list=(load_balance=off)(failover=on)"
				+ "(address=(protocol=tcp)(host=192.168.1.226)(port=1521))(address=(protocol=tcp)(host=192.168.1.227)(port=1521)))"
				+ "(connect_data=(service_name=PF1)(failover_mode=(type=select)(method=basic)(RETRIES=180)(DELAY=5))))",//oracle rac链接串
				"mdm","123456",//数据库账号和口令
				 "select 1 from dual" //数据库连接校验sql
				);//初始化数据源
	}
	@Test
	public void initdata() throws SQLException
	{
		List<String> data = SQLExecutor.queryListWithDBName(String.class,"test","select MODULE_ID from TB_MODULE_INFO where MODULE_URL like ?","%/CrmAccount/%");
		int aid  =SQLExecutor.queryObjectWithDBName(int.class, "test","select max(to_number(AUTH_ID)) from TB_MODULE_AUTH");
		aid ++;
		for(String mid:data)
		{
			SQLExecutor.insertWithDBName("test", "insert into TB_MODULE_AUTH(AUTH_ID,ADMIN_ID,MODULE_ID) values(?,'23',?)", aid+"",mid);//'22'是管理账号的id，对应于TD_USER_ADMIN表的SEQNO的值
			aid = aid+1;
		}
		
		SQLExecutor.insertWithDBName("test", "insert into TB_MODULE_AUTH(AUTH_ID,ADMIN_ID,MODULE_ID) values(?,'23',?)", aid+"","11");///SanyUIMPC/page/SanyAccount/orgTree.jsp
	}
	
	public void testsqlite(String dbpath)
	{
		//启动sqlite数据源gencode
		SQLUtil.startPool("gencode","org.sqlite.JDBC","jdbc:sqlite://"+dbpath,"root","root",
				 "select 1"				
	        		);
		//判断数据源gencode中对于的表BBOSS_GENCODE是否存在，不存在则创建
		String exist = "select 1 from BBOSS_GENCODE";
		
		try {
			SQLExecutor.queryObjectWithDBName(int.class,"gencode", exist);
		} catch (Exception e) {
			String tsql = "create table BBOSS_GENCODE (ID string,TABLENAME string,DBNAME string,FIELDINFOS TEXT,AUTHOR string,"
				       + "COMPANY string,"
				       + "CREATETIME number(10),"
				       + "UPDATETIME number(10),"
				       + "CONTROLPARAMS TEXT,"
				       + "  PRIMARY KEY (ID))";
			 
			try {
				SQLExecutor.updateWithDBName("gencode",tsql);
				 
			} catch (SQLException e1) {
				 
				e1.printStackTrace();
			}
		}
	}

}