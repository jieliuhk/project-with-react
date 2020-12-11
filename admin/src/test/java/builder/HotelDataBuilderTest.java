package builder;

import appdata.threadsafe.ThreadSafeHotelData;
import dataclass.Hotel;
import dataclass.Review;
import org.junit.Test;

import java.nio.file.Paths;

public class HotelDataBuilderTest {

    @Test
    public void testLoadAllFiles() {
        ThreadSafeHotelData data = new ThreadSafeHotelData();
        HotelDataBuilder builder = new HotelDataBuilder(data, 10);
        builder.loadHotelInfo("input/hotels.json");
        builder.loadReviews(Paths.get("input/reviews"));
        String[] hotelsHaveDescriptions = new String[] {"1047", "10323", "12539", "16955"};

        for(Hotel h : data.findHotels()) {
            System.out.println(h);

        }

        int max = 0;
        for(Review r : data.findReviews()) {
            max  = Math.max(max, r.getText().length());
            System.out.println(max);
        }
    }
}