package appdata.threadsafe;

import appdata.unsafe.HotelData;
import customLock.ReentrantReadWriteLock;
import dataclass.Hotel;
import dataclass.Review;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

/**
 * Class ThreadSafeHotelData - extends class HotelData (rename your class from project 1 as needed).
 * Thread-safe, uses ReentrantReadWriteLock to synchronize access to all data structures.
 */
public class ThreadSafeHotelData extends HotelData {
    private final static Logger log = LogManager.getRootLogger();

    private ReentrantReadWriteLock lock;

    /**
     * Default constructor.
     */
    public ThreadSafeHotelData() {
        super();
        lock = new ReentrantReadWriteLock();
    }

    /**
     * Overload of addHotel(String hotelId, String hotelName, String city, String state,
     * String streetAddress, double lat, double lon), accept single parameters object
     *
     * @param parameters parameters use for construct hotel
     */
    public void addHotel(Hotel.Parameters parameters) {
        try {
            lock.lockWrite();
            super.addHotel(parameters);
        } catch (Exception e) {
            log.error(e);
        } finally {
            lock.unlockWrite();
        }
    }

    /**
     * Overrides addReview method from HotelData class to make it thread-safe; uses the lock.
     * <p>
     * Overload of addReview(String hotelId, String reviewId, int rating, String reviewTitle, String review,
     * boolean isRecom, String date, String username), accept single parameters object
     *
     * @param parameters parameters use for construct review
     */
    @Override
    public boolean addReview(Review.Parameters parameters) {
        try {
            lock.lockWrite();
            return super.addReview(parameters);
        } catch (Exception e) {
            log.error(e);
            return false;
        } finally {
            lock.unlockWrite();
        }
    }

    /**
     * thread-safe add multiple reviews
     *
     * @param parameters parameters of multiple reviews
     */
    public void addReviews(List<Review.Parameters> parameters) {
        try {
            lock.lockWrite();
            for (Review.Parameters p : parameters) {
                this.addReview(p);
            }
        } catch (Exception e) {
            log.error(e);
        } finally {
            lock.unlockWrite();
        }
    }

    /**
     * Overrides a method of the parent class to make it thread-safe.
     * return the String represent the hotel's reviews
     *
     * @return review String
     */
    public Hotel[] findHotels() {
        try {
            lock.lockRead();
            return super.findHotels();
        } catch (Exception e) {
            log.error(e);
            return null;
        } finally {
            lock.unlockRead();
        }
    }

    /**
     * Overrides a method of the parent class to make it thread-safe.
     * return the String represent the hotel's reviews
     *
     * @return review String
     */
    public Review[] findReviews() {
        try {
            lock.lockRead();
            return super.findReviews();
        } catch (Exception e) {
            log.error(e);
            return null;
        } finally {
            lock.unlockRead();
        }
    }
}
