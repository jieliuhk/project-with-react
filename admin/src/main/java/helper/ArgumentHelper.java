package helper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;


/**
 * Application controller class, use for data preparation and logic control
 */
public class ArgumentHelper {
    private final String LINE_SEPARATOR = System.lineSeparator();
    private List<String> arguments;
    private final static Logger log = LogManager.getRootLogger();

    public ArgumentHelper(String[] args) {
        arguments = Arrays.asList(args);
    }

    /**
     * get hotel's path
     * @return String hotel's path
     */
    public String hotelPath() {
        int hotelFlagIndex = arguments.indexOf("-hotels");
        if (arguments.size() != 4 || !arguments.contains("-hotels") || (hotelFlagIndex != 0 && hotelFlagIndex != 2)) {
            log.error("Invalid argument syntax");
            return "";
        } else {
            return arguments.get(hotelFlagIndex + 1);
        }
    }

    /**
     * get reviews' path
     * @return String reviews' path
     */
    public String reviewsPath() {
        int reviewsFlagIndex = arguments.indexOf("-reviews");
        if (arguments.size() != 4 || !arguments.contains("-reviews") || (reviewsFlagIndex != 0 && reviewsFlagIndex != 2)) {
            log.error("Invalid argument syntax");
            return "";
        } else {
            return arguments.get(reviewsFlagIndex + 1);
        }
    }
}
