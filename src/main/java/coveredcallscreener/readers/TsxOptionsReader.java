/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package coveredcallscreener.readers;

import coveredcallscreener.domain.OptionQuote;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Yves
 */
public class TsxOptionsReader {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private final static String URLTSX = "http://www.m-x.ca/nego_cotes_fr.php?symbol={0}";
    private boolean put = false;

    public TsxOptionsReader(boolean putOption) {
        this.put = putOption;
    }

    public List<OptionQuote> readOptionQuote(String symbol) {
        String surl = MessageFormat.format(URLTSX, symbol);
        LOGGER.log(Level.FINE, "URLTSX=" + surl);
        List<OptionQuote> optionQuotes = new ArrayList<OptionQuote>();
        org.jsoup.nodes.Document doc;
        try {

            doc = Jsoup.connect(surl)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com")
                    .get();

        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Problem accessing TSX", ex);
            return optionQuotes;
        }
        Elements tds = doc.getElementsByTag("TD");
        if (tds.size() == 0) {
            LOGGER.log(Level.FINE, "No TD tag on this page. returning...");
            return optionQuotes;
        }
        int count = 0;
        OptionQuote op = null;
        String li = null;
        //chain = new OptionsChain();
        for (Element td : tds) {
            if (td.hasClass("left")) {
                op = null;
                Element a = td.child(0).child(0);
                li = a.attr("title");
                LOGGER.log(Level.FINE, "title attr=" + li);
                String optype = (li.substring(12, 13));
                if (optype.equals("C") && put) {
                    continue;
                }
                if (optype.equals("P") && !put) {
                    break;
                }
                op = new OptionQuote();
                String price = li.substring(13).replace(",", ".");

                op.setStrike(Double.parseDouble(price.replace("(Hebdomadaire)", "")));
                String dts = li.substring(6, 12);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd");
                Date dateExp = null;
                try {
                    dateExp = dateFormat.parse(dts);
                } catch (ParseException ex) {
                    LOGGER.log(Level.SEVERE, "Invalid date format", ex);
                    continue;
                }
                op.setExparyDate(dateExp);

            } else if (op != null) {
                if (count == 0) {
                    op.setBid(Double.parseDouble(td.text().replace(',', '.')));
                    count++;
                } else if (count == 1) {
                    op.setAsk(Double.parseDouble(td.text().replace(',', '.')));
                    count++;
                } else if (count == 2) {
                    op.setLast(Double.parseDouble(td.text().replace(',', '.')));
                    count++;
                } else if (count == 3) {
                    //op.setChange(Double.parseDouble(td.text().replace(',', '.')));
                    count++;
                } else if (count == 4) {
                    String opint = td.text().replace(" ", "");
                    op.setOpenInt(Long.parseLong(opint));
                    count++;
                } else if (count == 5) {
                    op.setVolume(Long.parseLong(td.text().replace(" ", "")));
                    optionQuotes.add(op);
                    count = 0;
                }
            }
        }

        return optionQuotes;
    }
}
