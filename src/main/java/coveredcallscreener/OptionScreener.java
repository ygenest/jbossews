package coveredcallscreener;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import coveredcallscreener.converters.GoogleConverter;
import coveredcallscreener.domain.OptionQuote;
import coveredcallscreener.domain.StockQuote;
import coveredcallscreener.domain.json.option.Expiration;
import coveredcallscreener.domain.json.option.GoogleOptionsJson;
import coveredcallscreener.domain.json.stock.GoogleStockJson;
import coveredcallscreener.filters.CallOptionsFilter;
import coveredcallscreener.readers.GoogleStockReader;
import coveredcallscreener.readers.TsxOptionsReader;
import coveredcallscreener.writers.CsvWriter;

public class OptionScreener {
	private final static Logger LOGGER = Logger
			.getLogger(Logger.GLOBAL_LOGGER_NAME);
	private CallOptionsFilter callOptionsFilter = new CallOptionsFilter();

	private List<String> msg = new ArrayList<String>();

	private boolean noStrikeBelowCurrent = false;
	private boolean putOption = false;
	private boolean unique = false;
	private String expMonthFrom = "";
	private String expMonthTo = "";
	private boolean zeroint = false;
	private int nbLine=0;

	public int getNbLine() {
		return nbLine;
	}

	public OptionScreener(boolean noStrikeBelowCurrent,boolean putOption, boolean unique, boolean zeroint, String expMonthFrom,String expMonthTo) {
		this.noStrikeBelowCurrent=noStrikeBelowCurrent;
		this.putOption = false;
		this.unique=unique;
		this.zeroint=zeroint;
		this.expMonthFrom=expMonthFrom;
		this.expMonthTo=expMonthTo;
	}
	
	public ByteArrayOutputStream processData(List<String> symArray) {
		LOGGER.log(Level.FINE, "processData");
		if (!expMonthFrom.isEmpty() && expMonthTo.isEmpty()) {
			expMonthTo = expMonthFrom;
		}
		callOptionsFilter.setNoStrikeBelowCurrent(noStrikeBelowCurrent);
		callOptionsFilter.setNoZeroInterest(zeroint);
		if (!expMonthFrom.isEmpty()) {

			LOGGER.log(Level.FINE, "Setting expMonth of filter at "
					+ expMonthFrom);

			callOptionsFilter.setExpMonthFrom(expMonthFrom);
			callOptionsFilter.setExpMonthTo(expMonthTo);
		}
		// ready=true;
		return readQuotes(symArray);
		
	}
	
	private ByteArrayOutputStream readQuotes(List<String> symbols) {
		LOGGER.log(Level.FINE, "readQuotes");
	
		GoogleStockReader googleStockReader = new GoogleStockReader();
		TsxOptionsReader tsxOptionsReader = new TsxOptionsReader(putOption);
		GoogleConverter googleConverter = new GoogleConverter();
		GoogleStockJson googleStockJson;
		List<StockQuote> stockQuotes = new ArrayList<StockQuote>();
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
		return csvWriter.write(stockQuotes);

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
				if (unique && optionQuote.getExparyDate().compareTo(prev) != 0) {
					LOGGER.log(Level.FINE, "Adding unique quote");
					stockQuote.getOptionQuotes().add(optionQuote);
					count++;

				} else {
					if (!unique) {
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
