package jbossews;

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
	private String [] symArray;
	private boolean putOption=false;
	private boolean unique=false;

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
        symArray=symbLst.split("\n");
        System.out.println("nbelemA="+symArray.length);
        readQuotes();
	}
	
    public void readQuotes() {
        List<String> symbols = Arrays.asList(symArray);
        GoogleStockReader googleStockReader = new GoogleStockReader();
        TsxOptionsReader tsxOptionsReader = new TsxOptionsReader(putOption);
        GoogleConverter googleConverter = new GoogleConverter();
        GoogleStockJson googleStockJson;
        List<StockQuote> stockQuotes = new ArrayList<StockQuote>();
                int nbLine = 0;
                System.out.println("ssslen5="+symbols.size());
        for (String symbol : symbols) {
            symbol = symbol.toUpperCase().trim();
            System.out.println("sss="+symbol);
            StockQuote stockQuote = null;
            if (symbol.endsWith(".TO")) {
                // process symbols for TSX exchange
                googleStockJson = googleStockReader.readStockQuote("TSE:" + symbol.replace(".TO", ""));
                stockQuote = googleConverter.convertStock(googleStockJson);
                stockQuote.setSymbol(googleStockJson.getSymbol() + ":" + googleStockJson.getExchange());
                if (stockQuote == null) {
                    System.out.println("Skipping unknown TSX symbol " + symbol);
                    continue;
                }
                List<OptionQuote> optionQuotes = tsxOptionsReader.readOptionQuote(symbol.replace(".TO", ""));
                if (optionQuotes == null) {
                    System.out.println("No option defined for TSX symbol " + symbol);
                    continue;
                } else {
                    nbLine += addOptionQuote(optionQuotes, stockQuote, putOption);
                }
            } else {
                // process symbols for US exchanges
                googleStockJson = googleStockReader.readStockQuote(symbol);
                if (googleStockJson == null) {
                    System.out.println("Skipping unknown US symbol " + symbol);
                    continue;
                }
                stockQuote = googleConverter.convertStock(googleStockJson);

                List<Expiration> expirations = googleStockReader.readOptionExpiration(symbol);
                if (expirations == null) {
                    System.out.println("No option defined for US symbol " + symbol);
                    continue;
                }
                for (Expiration expiration : expirations) {
                    GoogleOptionsJson googleOptionsJson = googleStockReader.readOptionQuote(symbol, expiration);
                    List<OptionQuote> optionQuotes = googleConverter.convertOption(googleOptionsJson, expiration);
                    nbLine += addOptionQuote(optionQuotes, stockQuote, putOption);

                }

            }
            stockQuotes.add(stockQuote);
        }
        File file = new File("test.csv");
        CsvWriter csvWriter = new CsvWriter();
        csvWriter.write(stockQuotes, file,unique);
        //System.out.println(nbLine + " option quotes written to file " + file.getName());
    }
        private static int addOptionQuote(List<OptionQuote> optionQuotes, StockQuote stockQuote, boolean putOption) {
        int count = 0;
        for (OptionQuote optionQuote : optionQuotes) {
            optionQuote.setStockPrice(stockQuote.getLast());
            if (CallOptionsFilter.filter(optionQuote, putOption)) {
                stockQuote.getOptionQuotes().add(optionQuote);
                count++;
            }
        }
        return count;
    }
}
