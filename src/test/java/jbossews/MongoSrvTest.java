package jbossews;

import static org.junit.Assert.*;

import org.junit.Test;

public class MongoSrvTest {

	@Test
	public void testAddData() {
		String [] data={"aaa","bbb","ccc"};
		MongoSrv mongoSrv=new MongoSrv();
		String res=mongoSrv.addData("optGroup", data);
		assertNull(res);
	}

}
