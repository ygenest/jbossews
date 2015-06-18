/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package coveredcallscreener;



import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Yves
 */
public class Main {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    
	private static boolean noStrikeBelowCurrent = false;
	private static boolean putOption = false;
	private static boolean unique = false;
	private static String expMonthFrom = "";
	private static String expMonthTo = "";
	private static boolean zeroint = false;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        LOGGER.setLevel(Level.WARNING);
        System.out.println("Processisng...");
        boolean invalidArg = false;
        String fname = null;
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("-")) {
                switch (args[i].charAt(1)) {
                    case 'd':
                        LOGGER.setLevel(Level.FINE);
                        LOGGER.log(Level.FINE, "In debugging mode");
                        break;
                    case 'f':
                    	expMonthFrom= args[i].toString().substring(2);
                        break;
                    case 't':
                    	expMonthTo= args[i].toString().substring(2);
                        break;

                    case 'z':
                        zeroint=true;
                        break;
                    case 'p':
                        putOption = true;
                        break;
                    case 's':
                    	noStrikeBelowCurrent=true;
                        break;
                    case 'u':
                        unique=true;
                        break;
                    default:
                        invalidArg = true;
                }
            } else {
                fname = args[i];
            }
        }
        if (fname == null || invalidArg) {
            System.out.println("USAGE stockscreener <options> <source file>");
            System.out.println("Where possible options include");
            System.out.println("\t-d\tActivate debug mode");
            System.out.println("\t-s\tignore share price above strike price");
            System.out.println("\t-z\tignore zero open interest quotes");
            System.out.println("\t-p\tshow put options quotes");
            System.out.println("\t-e\tShow only options for expiry date YYYYMM");
            System.out.println("\t-u\tShow only one option. Should be used with -s");
            return;
        }
        File file = new File(fname.replace(".txt", ".csv"));
        try {
            org.apache.commons.io.FileUtils.touch(file);

        } catch (IOException e) {
            System.out.println("The file " + file.getName() + " is already opened");
            return;
        }
        // load all symbols from file
        List<String> symbols = loadData(fname);
        OptionScreener os= new OptionScreener(noStrikeBelowCurrent,putOption, unique,zeroint,expMonthFrom, expMonthTo);
        ByteArrayOutputStream out=os.processData(symbols);

        try {
			OutputStream outputStream = new FileOutputStream (file);
			try {
				out.writeTo(outputStream);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
        System.out.println(" option quotes written to file " + file.getName());
    }



    private static List<String> loadData(String fname) {
        List<String> symbols = new ArrayList<String>();
        File file = new File(fname);
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader br = new BufferedReader(fileReader);

            String line = br.readLine();
            while (line != null) {
                if (!line.isEmpty() && !line.startsWith("#")) {
                    symbols.add(line);
                }
                line = br.readLine();
            }
        } catch (FileNotFoundException ex) {
            System.out.println("File not found " + fname);
            return null;
        } catch (IOException ex) {
            System.out.println("Problem reading file " + fname);
        }
        return symbols;
    }
}
