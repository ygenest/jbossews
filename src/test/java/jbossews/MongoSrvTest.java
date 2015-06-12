package jbossews;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class MongoSrvTest {

	@BeforeClass
	public static void prepareData() {
		String [] data={"aaa","bbb","ccc"};
		MongoSrv mongoSrv=new MongoSrv();
		String res=mongoSrv.addData("optGroupIni", data);
		assertNull("unexpected error in prepareData",res);
	}
	
	@AfterClass
	public static void DeleteData() {
		MongoSrv mongoSrv=new MongoSrv();
		mongoSrv.deleteGroup("optGroup");
		mongoSrv.deleteGroup("optGroupIni");
	}
	
	@Test
	public void testAddData() {
		String [] data={"aaa","bbb","ccc"};
		MongoSrv mongoSrv=new MongoSrv();
		String res=mongoSrv.addData("optGroup", data);
		assertNull("Error in testAddData",res);
	}
	
	@Test
	public void testReadData() {
		MongoSrv mongoSrv=new MongoSrv();
		List<String> res = mongoSrv.readData("optGroupIni");
		assertEquals("unexpected number of symbols returned by teatRedData",3, res.size());
	}

}
