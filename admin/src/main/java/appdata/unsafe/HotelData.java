package appdata.unsafe;

import dataclass.Hotel;
import dataclass.Review;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public abstract class HotelData {
    private final static Logger log = LogManager.getRootLogger();
    private Map<String, Hotel> hotels = new HashMap<>();
    private Map<String, Set<Review>> reviews = new HashMap<>();
    private Set<String> hotelIds = new TreeSet<>();

    /**
     * Overload of addHotel(String hotelId, String hotelName, String city, String state,
     * String streetAddress, double lat, double lon), accept single parameters object
     *
     * @param parameters parameters use for construct hotel
     */
    protected void addHotel(Hotel.Parameters parameters) {
        Hotel newHotel = new Hotel(parameters);
        hotels.put(newHotel.getHotelId(), newHotel);
        hotelIds.add(String.valueOf(newHotel.getHotelId()));
    }

    /**
     * Overload of addReview(String hotelId, String reviewId, int rating, String reviewTitle, String review,
     * boolean isRecom, String date, String username), accept single parameters object
     *
     * @param parameters parameters use for construct review
     */
    protected boolean addReview(Review.Parameters parameters) {
        if (!hotelIds.contains(String.valueOf(parameters.getHotelId()))) {
            return false;
        }

        try {
            Set<Review> tmp = reviews.getOrDefault(parameters.getHotelId(), new TreeSet<>());
            tmp.add(new Review(parameters));
            reviews.put(parameters.getHotelId(), tmp);
            return true;
        } catch (RuntimeException e) {
            log.error(e);
            return false;
        }
    }

    /**
     * return the String represent the hotel
     *
     * @return hotel Array
     */
    protected Hotel[] findHotels() {
        return hotels.values().toArray(new Hotel[0]);
    }

    /**
     * return the String represent the hotel's reviews
     * @return review String
     */
    protected Review[] findReviews() {
        List<Review> res = new LinkedList<>();
        for(Set<Review> s : reviews.values()) {
            res.addAll(s);
        }
        return res.toArray(new Review[0]);
    }
}
