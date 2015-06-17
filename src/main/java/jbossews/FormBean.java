package jbossews;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import coveredcallscreener.converters.GoogleConverter;
import coveredcallscreener.domain.json.option.Expiration;
import coveredcallscreener.domain.json.option.GoogleOptionsJson;
import coveredcallscreener.domain.json.stock.GoogleStockJson;
import coveredcallscreener.domain.OptionQuote;
import coveredcallscreener.domain.StockQuote;
import coveredcallscreener.filters.CallOptionsFilter;
import coveredcallscreener.readers.GoogleStockReader;
import coveredcallscreener.readers.TsxOptionsReader;
import coveredcallscreener.writers.CsvWriter;

public class FormBean {
	private final static Logger LOGGER = Logger
			.getLogger(Logger.GLOBAL_LOGGER_NAME);
	CallOptionsFilter callOptionsFilter = new CallOptionsFilter();
	private String symbLst = "";
	private String delGroup = "N";
	private String zeroint = "N";
	private String groupName = "";
	private String selectedGroup = "";
	private String selectedGroupPr = "";
	private String symbDb = "";
	private List<String> msg = new ArrayList<String>();
	private boolean ready = false;
	private String noStrikeBelowCurrent = "N";
	private boolean putOption = false;
	private String unique = "N";
	private String expMonthFrom = "";
	private String expMonthTo = "";
	private ByteArrayOutputStream out;
	private String btn1;
	private String btn2;
	private String errMsg;

	public String getDelGroup() {
		LOGGER.log(Level.FINE, "getDelGroup");
		return delGroup;
	}

	public void setDelGroup(String delGroup) {
		LOGGER.log(Level.FINE, "setDelGroup");
		this.delGroup = delGroup;
	}

	public String getSelectedGroupPr() {
		LOGGER.log(Level.FINE, "getSelectedGroupPr");
		return selectedGroupPr;
	}

	public void setSelectedGroupPr(String selectedGroupPr) {
		LOGGER.log(Level.FINE, "setSelectedGroupPr");
		this.selectedGroupPr = selectedGroupPr;
	}

	public List<String> getGroupNameExist() {
		MongoSrv mongoSrv = new MongoSrv();
		return mongoSrv.readGroup();
	}

	public String getErrMsg() {
		LOGGER.log(Level.FINE, "getErrMsg");
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		LOGGER.log(Level.FINE, "setErrMsg");
		this.errMsg = errMsg;
	}

	public String getBtn2() {
		LOGGER.log(Level.FINE, "getBtn2");
		return btn2;
	}

	public String getSelectedGroup() {
		LOGGER.log(Level.FINE, "getSelectedGroup");
		return selectedGroup;
	}

	public void setSelectedGroup(String selectedGroup) {
		LOGGER.log(Level.FINE, "setSelectedGroup");
		this.selectedGroup = selectedGroup;
	}

	public void setBtn2(String btn2) {
		LOGGER.log(Level.FINE, "setBtn2");
		MongoSrv mongoSrv = new MongoSrv();
		LOGGER.log(Level.FINE, "mongoSrv instanciated: " + mongoSrv);
		this.btn2 = btn2;
		if (!this.symbDb.isEmpty() && !this.groupName.isEmpty()) {
			LOGGER.log(Level.FINE, "Adding data");
			String[] symArray = symbDb.split("\n");
			errMsg = mongoSrv.addData(groupName, symArray);
			this.symbDb = "";
			this.groupName = "";
			return;
		}
		if (!this.selectedGroup.isEmpty()
				&& this.delGroup.equalsIgnoreCase("Y")) {
			LOGGER.log(Level.FINE, "Deleting data");
			mongoSrv.deleteGroup(this.selectedGroup);
			this.selectedGroup = "";
			this.delGroup = "N";
			return;
		}
		if (!this.selectedGroup.isEmpty() && !this.symbDb.isEmpty()) {
			LOGGER.log(Level.FINE, "Updating data");
			groupName = this.selectedGroup;
			mongoSrv.deleteGroup(this.selectedGroup);
			String[] symArray = symbDb.split("\n");
			errMsg = mongoSrv.addData(groupName, symArray);
			this.symbDb = "";
			this.groupName = "";
			return;
		}

		if (!this.selectedGroup.isEmpty()) {
			LOGGER.log(Level.FINE, "Reading data");
			symbDb = "";
			List<String> symbLst = mongoSrv.readData(this.getSelectedGroup());
			for (String s : symbLst) {
				symbDb = symbDb.concat(s + "\n");
			}
		}
	}

	public String getGroupName() {
		LOGGER.log(Level.FINE, "getGroupName");
		return groupName;
	}

	public void setGroupName(String groupName) {
		LOGGER.log(Level.FINE, "setGroupName");
		this.groupName = groupName;
	}

	public String getSymbDb() {
		LOGGER.log(Level.FINE, "getSymbDb");
		return symbDb;
	}

	public void setSymbDb(String symbDb) {
		LOGGER.log(Level.FINE, "setSymbDb");
		this.symbDb = symbDb;
	}

	public String getZeroint() {
		LOGGER.log(Level.FINE, "getZeroint");
		return zeroint;
	}

	public void setZeroint(String zeroint) {
		LOGGER.log(Level.FINE, "setZeroint");
		this.zeroint = zeroint;
	}

	public String getBtn1() {
		LOGGER.log(Level.FINE, "getBtn1");
		return btn1;
	}

	public void setBtn1(String btn1) {
		LOGGER.log(Level.FINE, "setBtn1");
		this.btn1 = btn1;
		if (!this.selectedGroupPr.isEmpty()) {
			MongoSrv mongoSrv = new MongoSrv();
			List<String> res = mongoSrv.readData(this.selectedGroupPr);
			String[] arr = new String[res.size()];
			arr = res.toArray(arr);
			processData(arr);
			return;
		}
		if (!this.symbLst.isEmpty()) {
			processData(symbLst.split("\n"));
		}
	}

	public FormBean() {
		LOGGER.log(Level.FINE, "In FormBean constructor");
		LOGGER.log(Level.INFO, "Level info activated");
		LOGGER.log(Level.FINE, "Level fine activated");
	}

	public List<String> getMsg() {
		LOGGER.log(Level.FINE, "getMsg");
		return msg;
	}

	public void setMsg(List<String> msg) {
		LOGGER.log(Level.FINE, "setMsg");
		this.msg = msg;
	}

	public String getUnique() {
		LOGGER.log(Level.FINE, "getUnique");
		return unique;
	}

	public void setUnique(String unique) {
		LOGGER.log(Level.FINE, "setUnique");
		this.unique = unique;
	}

	public String getNoStrikeBelowCurrent() {
		LOGGER.log(Level.FINE, "getNoStrikeBelowCurrent");
		return noStrikeBelowCurrent;
	}

	public void setNoStrikeBelowCurrent(String noStrikeBelowCurrent) {
		LOGGER.log(Level.FINE, "setNoStrikeBelowCurrent");
		this.noStrikeBelowCurrent = noStrikeBelowCurrent;
	}

	public String getExpMonthFrom() {
		LOGGER.log(Level.FINE, "getExpMonthFrom");
		return expMonthFrom;
	}

	public void setExpMonthFrom(String expMonthFrom) {
		LOGGER.log(Level.FINE, "setExpMonthFrom");
		this.expMonthFrom = expMonthFrom;
	}

	public String getExpMonthTo() {
		LOGGER.log(Level.FINE, "getExpMonthTo");
		return expMonthTo;
	}

	public void setExpMonthTo(String expMonthTo) {
		LOGGER.log(Level.FINE, "setExpMonthTo");
		this.expMonthTo = expMonthTo;
	}

	public ByteArrayOutputStream getOut() {
		LOGGER.log(Level.FINE, "getOut");
		return out;
	}

	public void setOut(ByteArrayOutputStream out) {
		LOGGER.log(Level.FINE, "setOut");
		this.out = out;
	}

	public boolean getReady() {
		LOGGER.log(Level.FINE, "getReady " + symbLst);
		return ready;
	}

	public void setReady(boolean ready) {
		LOGGER.log(Level.FINE, "setReady");
		this.ready = ready;
	}

	public String getSymbLst() {
		LOGGER.log(Level.FINE, "getSymbLst");
		return symbLst;
	}

	public void setSymbLst(String symbLst) {
		LOGGER.log(Level.FINE, "setSymbLst");
		this.symbLst = symbLst;
	}

	private void loadData() {
		MongoSrv mongoSrv = new MongoSrv();
	}

	private void processData(String... symArray) {
		LOGGER.log(Level.FINE, "processData");
		if (!expMonthFrom.isEmpty() && expMonthTo.isEmpty()) {
			expMonthTo = expMonthFrom;
		}
		callOptionsFilter.setNoStrikeBelowCurrent(noStrikeBelowCurrent
				.equalsIgnoreCase("Y"));
		callOptionsFilter.setNoZeroInterest(zeroint.equalsIgnoreCase("Y"));
		if (!expMonthFrom.isEmpty()) {

			LOGGER.log(Level.FINE, "Setting expMonth of filter at "
					+ expMonthFrom);

			callOptionsFilter.setExpMonthFrom(expMonthFrom);
			callOptionsFilter.setExpMonthTo(expMonthTo);
		}
		readQuotes(symArray);
		setReady(true);
	}

	public void readQuotes(String... symArray) {
		LOGGER.log(Level.FINE, "readQuotes");
		List<String> symbols = Arrays.asList(symArray);
		GoogleStockReader googleStockReader = new GoogleStockReader();
		TsxOptionsReader tsxOptionsReader = new TsxOptionsReader(putOption);
		GoogleConverter googleConverter = new GoogleConverter();
		GoogleStockJson googleStockJson;
		List<StockQuote> stockQuotes = new ArrayList<StockQuote>();
		int nbLine = 0;
		for (String symbol : symbols) {
			symbol = symbol.toUpperCase().trim();
			StockQuote stockQuote = null;
			if (symbol.endsWith(".TO")) {
				// process symbols for TSX exchange
				googleStockJson = googleStockReader.readStockQuote("TSE:"
						+ symbol.replace(".TO", ""));
				if (googleStockJson == null) {
					msg.add("Skipping unknown TSX symbol " + symbol);
					continue;
				}
				stockQuote = googleConverter.convertStock(googleStockJson);
				stockQuote.setSymbol(googleStockJson.getSymbol() + ":"
						+ googleStockJson.getExchange());
				List<OptionQuote> optionQuotes = tsxOptionsReader
						.readOptionQuote(symbol.replace(".TO", ""));
				if (optionQuotes == null) {
					msg.add("No option defined for TSX symbol " + symbol);

					continue;
				} else {
					nbLine += addOptionQuote(optionQuotes, stockQuote,
							putOption);
				}
			} else {
				// process symbols for US exchanges
				googleStockJson = googleStockReader.readStockQuote(symbol);
				if (googleStockJson == null) {
					msg.add("Skipping unknown US symbol " + symbol);
					continue;
				}
				stockQuote = googleConverter.convertStock(googleStockJson);

				List<Expiration> expirations = googleStockReader
						.readOptionExpiration(symbol);
				if (expirations == null) {
					msg.add("No options defined for US symbol " + symbol);
					continue;
				}
				for (Expiration expiration : expirations) {
					GoogleOptionsJson googleOptionsJson = googleStockReader
							.readOptionQuote(symbol, expiration);
					List<OptionQuote> optionQuotes = googleConverter
							.convertOption(googleOptionsJson, expiration);
					nbLine += addOptionQuote(optionQuotes, stockQuote,
							putOption);
				}
			}
			stockQuotes.add(stockQuote);
		}

		CsvWriter csvWriter = new CsvWriter();
		LOGGER.log(Level.FINE, "Quotes ready");
		out = csvWriter.write(stockQuotes);

	}

	private int addOptionQuote(List<OptionQuote> optionQuotes,
			StockQuote stockQuote, boolean putOption) {
		LOGGER.log(Level.FINE, "addOptionQuote");
		int count = 0;
		Date prev = new Date();
		for (OptionQuote optionQuote : optionQuotes) {
			optionQuote.setStockPrice(stockQuote.getLast());
			if (callOptionsFilter.filter(optionQuote, putOption)) {
				LOGGER.log(
						Level.FINE,
						"expDat != prev= "
								+ (optionQuote.getExparyDate().compareTo(prev)));
				if (unique.equalsIgnoreCase("Y")
						&& optionQuote.getExparyDate().compareTo(prev) != 0) {
					LOGGER.log(Level.FINE, "Adding unique quote");
					stockQuote.getOptionQuotes().add(optionQuote);
					count++;

				} else {
					if (!unique.equalsIgnoreCase("Y")) {
						LOGGER.log(Level.FINE, "Adding non unique quote");
						stockQuote.getOptionQuotes().add(optionQuote);
						count++;
					}
				}
				prev.setTime((optionQuote.getExparyDate().getTime()));
			}

		}
		return count;
	}
}
