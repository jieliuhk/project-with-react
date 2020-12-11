package helper;

import appdata.threadsafe.ThreadSafeHotelData;
import com.google.gson.stream.JsonReader;
import dataclass.Hotel;
import dataclass.Review;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper Class for parse Json file to Java Data Class
 */
public class JsonFileParseHelper {
    private final static Logger log = LogManager.getRootLogger();

    /**
     * parse Json file to {@link dataclass.Hotel Hotel} and
     * store in {@link appdata.unsafe.HotelData HotelData} object
     *
     * @param hotelsFile to hotel Json file path
     * @param data       hotel data need to add hotels
     */
    public static void parseHotels(String hotelsFile, ThreadSafeHotelData data) {

        FileReader fr = null;
        try {
            fr = new FileReader(hotelsFile);
        } catch (FileNotFoundException e) {
            log.error(e);
        }

        if (fr != null) {
            try (JsonReader reader = new JsonReader(fr)) {
                reader.beginObject();
                while (reader.hasNext()) {
                    String jsonName = reader.nextName();
                    if (jsonName.equals("sr")) {
                        break;
                    } else {
                        reader.nextString();
                    }
                }
                reader.beginArray();
                Hotel.Parameters parameters = new Hotel.Parameters();
                while (reader.hasNext()) {
                    reader.beginObject();
                    while (reader.hasNext()) {
                        addHotelAttributeOrSkip(reader, parameters);
                    }
                    reader.endObject();
                    data.addHotel(parameters);
                }
                reader.endArray();
                reader.endObject();
            } catch (IOException e) {
                log.error(e);
            }
        }
    }

    private static void addHotelAttributeOrSkip(JsonReader reader, Hotel.Parameters parameters) throws IOException {
        String hotelFieldName = reader.nextName();
        switch (hotelFieldName) {
            case "id":
                parameters.setHotelId(reader.nextString());
                break;
            case "f":
                parameters.setName(reader.nextString());
                break;
            case "ll":
                reader.beginObject();
                reader.nextName();
                double lat = Double.parseDouble(reader.nextString());
                reader.nextName();
                double lon = Double.parseDouble(reader.nextString());
                parameters.setLocation(new Hotel.Location(lat, lon));
                reader.endObject();
                break;
            case "ad":
                parameters.setAddress(reader.nextString());
                break;
            case "ci":
                parameters.setCity(reader.nextString());
                break;
            case "pr":
                parameters.setState(reader.nextString());
                break;
            default:
                reader.nextString();
                break;
        }
    }


    /**
     * parse Json file to {@link dataclass.Review Hotel} and
     * store in {@link appdata.unsafe.HotelData HotelData} object
     *
     * @param reviewFile reviews Json file contains reviews
     * @return reviews' parameter, can be used to add review to HotelData
     */
    public static List<Review.Parameters> parseReview(String reviewFile) {
        List<Review.Parameters> res = new ArrayList<>();

        try (JsonReader reader = new JsonReader(new FileReader(reviewFile))) {
            reader.beginObject(); //object
            reader.nextName();
            reader.beginObject(); //reviewDetails
            while (reader.hasNext()) {
                String jsonName = reader.nextName();
                if (jsonName.equals("reviewCollection")) {
                    reader.beginObject(); //reviewCollection
                    break;
                } else {
                    reader.skipValue();
                }
            }

            reader.nextName();//review
            reader.beginArray();

            while (reader.hasNext()) {
                Review.Parameters parameters = new Review.Parameters();
                reader.beginObject(); //0, 1, 2, ....
                while (reader.hasNext()) {
                    addReviewAttributeOrSkip(reader, parameters);
                }
                reader.endObject(); //0, 1, 2, ...
                res.add(parameters);
            }
        } catch (IOException e) {
            log.error(e);
        }

        return res;
    }

    private static void addReviewAttributeOrSkip(JsonReader reader, Review.Parameters parameters) throws IOException {
        String reviewFieldName = reader.nextName();
        switch (reviewFieldName) {
            case "hotelId":
                parameters.setHotelId(reader.nextString());
                break;
            case "reviewId":
                parameters.setReviewId(reader.nextString());
                break;
            case "ratingOverall":
                parameters.setAverageRating(reader.nextDouble());
                break;
            case "userNickname":
                String name = reader.nextString();
                parameters.setUserNickName(name.equals("") ? "Anonymous" : name);
                break;
            case "title":
                parameters.setReviewTitle(reader.nextString());
                break;
            case "reviewText":
                parameters.setReviewText(reader.nextString());
                break;
            case "reviewSubmissionTime":
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                try {
                    parameters.setReviewSubmissionTime(format.parse(reader.nextString()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            case "isRecommended":
                String recom = reader.nextString();
                if (recom.equals("YES")) {
                    parameters.setIsRecom(true);
                } else {
                    parameters.setIsRecom(false);
                }
                break;
            default:
                reader.skipValue();
                break;
        }
    }

    /**
     * Parse api related config from json file
     *
     * @param configPath file path of config.json
     */
    public static Map<String, String> parseConfig(String configPath) {
        Map<String, String> res = new HashMap<>();

        FileReader fr = null;
        try {
            fr = new FileReader(configPath);
        } catch (FileNotFoundException e) {
            log.error(e);
        }

        if (fr != null) {
            try (JsonReader reader = new JsonReader(fr)) {
                reader.beginObject();
                while (reader.hasNext()) {
                    String jsonName = reader.nextName();
                    String jsonValue = reader.nextString();
                    res.put(jsonName, jsonValue);
                }
                reader.endObject();
            } catch (IOException e) {
                log.error(e);
            }
        }
        return res;
    }
}
