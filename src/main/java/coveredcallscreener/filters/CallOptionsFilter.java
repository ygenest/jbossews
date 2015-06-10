/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package coveredcallscreener.filters;

import coveredcallscreener.domain.OptionQuote;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The filter class determine if a quote must be written to the outputstream
 * @author Yves
 */
public class CallOptionsFilter {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private  boolean noZeroInterest = false;
    private  int percentageAboveStrike = 0;
    private   boolean noStrikeBelowCurrent = false;
    private  Calendar expDateFrom=null;
    private  Calendar expDateTo=null;

    public  boolean filter(OptionQuote optionQuote, boolean put) {
    	LOGGER.log(Level.FINE, "Filter in effect: NoStrikeBelowCurrent="+noStrikeBelowCurrent);
         if (expDateFrom != null && expDateTo != null) {
        	 
             Date d1 = expDateFrom.getTime();
             Date d3 = expDateTo.getTime();
             Date d2 = optionQuote.getExparyDate();
             LOGGER.log(Level.FINE, "Filtering by expDate d1="+d1+" d2="+d2+" d3="+d3);
             if (!((d2.after(d1)|| (d2.equals(d1))) && (d2.before(d3))|| d2.equals(d3))) {          	 
                 return false;
             }
         }

        
        if (isNoZeroInterest() && optionQuote.getOpenInt() < 1) {
            return false;
        }
        if (percentageAboveStrike > 0) {

        }
        if (put && noStrikeBelowCurrent && optionQuote.getStrike() >= optionQuote.getStockPrice()) {
        	LOGGER.log(Level.FINE, "Filtering strike price below current price on put options");
            return false;
        }
        if (!put && noStrikeBelowCurrent && optionQuote.getStrike() <= optionQuote.getStockPrice()) {
        	LOGGER.log(Level.FINE, "Filtering strike price below current price");
            return false;
        }
        return true;
    }

    /**
     * @return the noZeroInterest
     */
    public   boolean isNoZeroInterest() {
        return this.noZeroInterest;
    }

    /**
     * @param aNoZeroInterest the noZeroInterest to set
     */
    public  void setNoZeroInterest(boolean aNoZeroInterest) {
    	LOGGER.log(Level.FINE, "Filtering zero interest options");
        this.noZeroInterest = aNoZeroInterest;
    }

    /**
     * @return the noStrikeBelowCurrent
     */
    public   boolean isNoStrikeBelowCurrent() {
        return this.noStrikeBelowCurrent;
    }

    /**
     * @param aNoStrikeBelowCurrent the noStrikeBelowCurrent to set
     */
    public  void setNoStrikeBelowCurrent(boolean aNoStrikeBelowCurrent) {
        this.noStrikeBelowCurrent = aNoStrikeBelowCurrent;
    }

    /**
     * @return the percentageAboveStrike
     */
    public  int getPercentageAboveStrike() {
        return this.percentageAboveStrike;
    }

    /**
     * @param percentageAboveStrike the percentageAboveStrike to set
     */
    public  void setPercentageAboveStrike(int percentageAboveStrike) {
        this.percentageAboveStrike = percentageAboveStrike;
    }
    public void setExparyMonth(String expMonth) {
        
    }
    
    public void setExpMonthFrom(String sdate) {
    	expDateFrom = convMonth(sdate);
    }
    
    public void setExpMonthTo(String sdate) {
    	expDateTo = convMonth(sdate);
    }
    
     public Calendar  convMonth(String sdate) {
         int year = Integer.parseInt(sdate.substring(0, 4));
         int month = Integer.parseInt(sdate.substring(4, 6)) - 1;
         Calendar c = new GregorianCalendar(year, month, 1);
         int sat3 = 0;
         while (sat3 != 3) {
             if (c.get(Calendar.DAY_OF_WEEK) == 6) {
                 ++sat3;
             }
             c.add(Calendar.DATE, 1);
         }
         c.add(Calendar.DATE, -1);
         return c;

     }


}
