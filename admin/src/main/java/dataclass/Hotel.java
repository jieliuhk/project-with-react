package dataclass;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Data class store hotel information
 */
public class Hotel {
    private final String LINE_SEPARATOR = System.lineSeparator();
    private final String HOTEL_NAME = "%s";
    private final String HOTEL_ID = "%s";
    private final String ADDRESS = "%s";
    private final String CITY = "%s";
    private final String STATE = "%s";
    private final String HOTEL_DISPLAY_FORMAT = HOTEL_NAME + ": " + HOTEL_ID
            + LINE_SEPARATOR + ADDRESS + LINE_SEPARATOR + CITY + ", " + STATE + LINE_SEPARATOR;

    @Expose
    private String hotelId;
    @Expose
    @SerializedName("name")
    private String hotelName;
    @Expose
    @SerializedName("addr")
    private String address;
    @Expose
    private String city;
    @Expose
    private String state;
    private Location location;
    @Expose
        private double latitude;
    @Expose
    private double longitude;

    /**
     * @return hotelId
     */
    public String getHotelId() {
        return hotelId;
    }

    /**
     * @return address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @return state
     */
    public String getState() {
        return state;
    }

    /**
     * @return hotel Name
     */
    public String getName() {
        return hotelName;
    }

    /**
     * @return hotel city
     */
    public String getCity() {
        return city;
    }

    /**
     * @return hotel latitude
     */
    public double getLatitude() {
        return location.lat;
    }

    /**
     * @return hotel longitude
     */
    public double getLongitude() {
        return location.lng;
    }


    /**
     * @param parameters hotel parameters include name, Id, location and address
     */
    public Hotel(Parameters parameters) {
        hotelName = parameters.name;
        hotelId = parameters.hotelId;
        location = parameters.location;
        address = parameters.address;
        city = parameters.city;
        state = parameters.state;
        this.latitude = location.lat;
        this.longitude = location.lng;
    }

    /**
     * Inner class store location info of hotel
     */
    public static class Location {
        @Expose
        private double lat;
        @Expose
        private double lng;

        public Location(double latitude, double longitude) {
            lat = latitude;
            lng = longitude;
        }

        /**
         * @return latitude of the location
         */
        double getLatitude() {
            return lat;
        }

        /**
         * @return longitude of the location
         */
        double getLongitude() {
            return lng;
        }

    }

    /**
     * Inner class use consolidate hotel's parameters
     */
    public static class Parameters {
        private String name;
        private String hotelId;
        private String address;
        private String city;
        private String state;
        private Location location;

        /**
         * Set hotel name
         *
         * @param name Hotel name
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * Set Hotel Id
         *
         * @param id Hotel Id
         */
        public void setHotelId(String id) {
            this.hotelId = id;
        }

        /**
         * Set hotel address
         *
         * @param address Hotel Address
         */
        public void setAddress(String address) {
            this.address = address;
        }

        /**
         * Set hotel city
         *
         * @param city Hotel city
         */
        public void setCity(String city) {
            this.city = city;
        }

        /**
         * Set hotel state
         *
         * @param state Hotel state
         */
        public void setState(String state) {
            this.state = state;
        }

        /**
         * Aet hotel location
         *
         * @param location Hotel Location
         */
        public void setLocation(Location location) {
            this.location = location;
        }
    }

    /**
     * @return String representative of the hotel
     */
    @Override
    public String toString() {
        return String.format(HOTEL_DISPLAY_FORMAT, hotelName, hotelId, address, city, state);
    }

}
