package builder;

import appdata.threadsafe.ThreadSafeHotelData;
import dataclass.Review;
import helper.JsonFileParseHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * Class HotelDataBuilder. Loads hotel info from input files to ThreadSafeHotelData (using multithreading).
 */
public class HotelDataBuilder {
    private ThreadSafeHotelData hdata; // the "big" ThreadSafeHotelData that will contain all hotel and reviews info
    private ExecutorService exec;
    private final static Logger log = LogManager.getRootLogger();

    /**
     * Constructor for class HotelDataBuilder.
     *
     * @param data object use to store hotel data
     **/
    public HotelDataBuilder(ThreadSafeHotelData data) {
        hdata = data;
        exec = Executors.newFixedThreadPool(1);
    }

    /**
     * Constructor for class HotelDataBuilder that takes ThreadSafeHotelData and
     * the number of threads to create as a parameter.
     *
     * @param data       thread safe data store
     * @param numThreads max number of thread to be used
     */
    public HotelDataBuilder(ThreadSafeHotelData data, int numThreads) {
        hdata = data;
        exec = Executors.newFixedThreadPool(numThreads);
    }

    /**
     * s shutdown the thread pool
     */
    public void shutDown() {
        exec.shutdown();
        try {
            exec.awaitTermination(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Read the json file with information about the hotels and load it into the
     * appropriate data structure(s).
     *
     * @param jsonFilename json file store hotel data
     */
    public void loadHotelInfo(String jsonFilename) {
        JsonFileParseHelper.parseHotels(jsonFilename, hdata);
    }

    /**
     * Loads reviews from json files. Recursively processes subfolders.
     * Each json file with reviews should be processed concurrently (you need to create a new runnable job for each
     * json file that you encounter)
     *
     * @param dir directory store review files
     */
    public void loadReviews(Path dir) {
        try {
            Stream<Path> pathStream = Files.walk(dir);
            pathStream.filter(Files::isRegularFile)
                    .forEach(
                            path -> exec.submit(new ReviewParser(path.toString()))
                    );
        } catch (IOException e) {
            log.error(e);
        }
    }

    private class ReviewParser implements Runnable {
        private String file;
        private List<Review.Parameters> parameters;

        public ReviewParser(String reviewFile) {
            file = reviewFile;
            parameters = new ArrayList<>();
        }

        @Override
        public void run() {
            log.debug("reading reviews from file " + file);
            parameters = JsonFileParseHelper.parseReview(file);
            log.debug("combining reviews to grand HotelData");
            hdata.addReviews(parameters);
        }
    }
}
