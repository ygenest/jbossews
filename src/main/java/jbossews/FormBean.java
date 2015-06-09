package jbossews;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
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
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	private String symbLst="";
	private String msg="";
	private boolean ready = false;
	private String noStrikeBelowCurrent="N";
	private String[] symArray;
	private boolean putOption = false;
	private String unique = "N";
	private String expMonth="";
	private ByteArrayOutputStream out;
	CallOptionsFilter callOptionsFilter=new CallOptionsFilter();
	
	FormBean() {
		LOGGER.setLevel(Level.INFO);
	}
	
	public String getMsg() {
		LOGGER.log(Level.FINE, "getMsg");
		return msg;
	}

	public void setMsg(String msg) {
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
	
	public String getExpMonth() {
		LOGGER.log(Level.FINE, "getExpMonth");
		return expMonth;
	}

	public void setExpMonth(String expMonth) {
		LOGGER.log(Level.FINE, "setExpMonth "+expMonth);
		this.expMonth = expMonth;
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
		LOGGER.log(Level.FINE, "getReady "+symbLst);
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
		if (!this.symbLst.isEmpty()) {
			processData();
		}
	}

	private void processData() {
		symArray = symbLst.split("\n");
		callOptionsFilter.setNoStrikeBelowCurrent(noStrikeBelowCurrent.equalsIgnoreCase("Y"));
		if (!expMonth.isEmpty()) {
			LOGGER.log(Level.FINE, "Setting expMonth of filter at "+expMonth);
			callOptionsFilter.setExpMonth(expMonth);
		}
		readQuotes();
		setReady(true);
	}

	public void readQuotes() {
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
				stockQuote = googleConverter.convertStock(googleStockJson);

				if (stockQuote == null) {
					msg=msg+"Skipping unknown TSX symbol "+symbol;
					continue;
				}
				stockQuote.setSymbol(googleStockJson.getSymbol() + ":"
						+ googleStockJson.getExchange());
				List<OptionQuote> optionQuotes = tsxOptionsReader
						.readOptionQuote(symbol.replace(".TO", ""));
				if (optionQuotes == null) {
					msg=msg+"No option defined for TSX symbol "+symbol;
					continue;
				} else {
					nbLine += addOptionQuote(optionQuotes, stockQuote,
							putOption);
				}
			} else {
				// process symbols for US exchanges
				googleStockJson = googleStockReader.readStockQuote(symbol);
				if (googleStockJson == null) {
					msg=msg+"Skipping unknown US symbol "+symbol;
					continue;
				}
				stockQuote = googleConverter.convertStock(googleStockJson);

				List<Expiration> expirations = googleStockReader
						.readOptionExpiration(symbol);
				if (expirations == null) {
					msg=msg+"No options defined for US symbol "+symbol;
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
		out = csvWriter.write(stockQuotes, unique.equalsIgnoreCase("Y"));

	}

	private  int addOptionQuote(List<OptionQuote> optionQuotes,
			StockQuote stockQuote, boolean putOption) {
		int count = 0;
		for (OptionQuote optionQuote : optionQuotes) {
			optionQuote.setStockPrice(stockQuote.getLast());
			if (callOptionsFilter.filter(optionQuote, putOption)) {
				stockQuote.getOptionQuotes().add(optionQuote);
				count++;
			}
		}
		return count;
	}
}
