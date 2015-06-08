package jbossews;

import java.io.ByteArrayOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
	private String symbLst;
	private String[] symArray;
	private boolean putOption = false;
	private String unique = "N";
	public String getUnique() {
		return unique;
	}

	public void setUnique(String unique) {
		this.unique = unique;
	}



	private boolean ready = false;
	private String noStrikeBelowCurrent="N";
	private CallOptionsFilter callOptionsFilter=new CallOptionsFilter();



	public String getNoStrikeBelowCurrent() {
		return noStrikeBelowCurrent;
	}

	public void setNoStrikeBelowCurrent(String noStrikeBelowCurrent) {
		this.noStrikeBelowCurrent = noStrikeBelowCurrent;
	}



	private String expMonth="";
	
	public String getExpMonth() {
		return expMonth;
	}

	public void setExpMonth(String expMonth) {
		this.expMonth = expMonth;
	}



	private ByteArrayOutputStream out;

	public ByteArrayOutputStream getOut() {
		return out;
	}

	public void setOut(ByteArrayOutputStream out) {
		this.out = out;
	}

	public boolean getReady() {
		return ready;
	}

	public void setReady(boolean ready) {
		this.ready = ready;
	}

	public String getSymbLst() {
		return symbLst;
	}

	public void setSymbLst(String symbLst) {
		this.symbLst = symbLst;
		if (!symbLst.isEmpty()) {
			processData();

		}
	}

	private void processData() {
		symArray = symbLst.split("\n");

		
		callOptionsFilter.setNoStrikeBelowCurrent(noStrikeBelowCurrent.equalsIgnoreCase("Y"));
		if (!expMonth.isEmpty()) {
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
					System.out.println("Skipping unknown TSX symbol " + symbol);
					continue;
				}
				stockQuote.setSymbol(googleStockJson.getSymbol() + ":"
						+ googleStockJson.getExchange());
				List<OptionQuote> optionQuotes = tsxOptionsReader
						.readOptionQuote(symbol.replace(".TO", ""));
				if (optionQuotes == null) {
					System.out.println("No option defined for TSX symbol "
							+ symbol);
					continue;
				} else {
					nbLine += addOptionQuote(optionQuotes, stockQuote,
							putOption);
				}
			} else {
				// process symbols for US exchanges
				googleStockJson = googleStockReader.readStockQuote(symbol);
				if (googleStockJson == null) {
					System.out.println("Skipping unknown US symbol " + symbol);
					continue;
				}
				stockQuote = googleConverter.convertStock(googleStockJson);

				List<Expiration> expirations = googleStockReader
						.readOptionExpiration(symbol);
				if (expirations == null) {
					System.out.println("No option defined for US symbol "
							+ symbol);
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
		// System.out.println(nbLine + " option quotes written to file " +
		// file.getName());
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
