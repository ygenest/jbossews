package jbossews;

public class FormBean {
	private String symbLst;

	public String getSymbLst() {
		return symbLst;
	}

	public void setSymbLst(String symbLst) {
		this.symbLst = symbLst;
		if (!symbLst.isEmpty()) {
		System.out.println("processing ...");
		}
	}
}
