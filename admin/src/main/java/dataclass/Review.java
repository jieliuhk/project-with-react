package dataclass;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import dataclass.exception.InvalidRatingException;
import dataclass.exception.ParseException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Data class store reviews information
 */
public class Review implements Comparable<Review> {
    private final String LINE_SEPARATOR = System.lineSeparator();
    private final String DASH_LINE = "--------------------";
    private final String USER_NAME = "%s";
    private final String RATING = "%d";
    private final String REVIEW_TITLE = "%s";
    private final String REVIEW_TEXT = "%s";
    private final String SUBMIT_TIME = "%s";
    private final String REVIEW_DISPLAY_FORMAT = DASH_LINE + LINE_SEPARATOR + "Review by "
            + USER_NAME + " on " + SUBMIT_TIME + LINE_SEPARATOR + "Rating: " + RATING + LINE_SEPARATOR
            + REVIEW_TITLE + LINE_SEPARATOR + REVIEW_TEXT + LINE_SEPARATOR;

    private String hotelId;
    @Expose
    private String reviewId;
    private int avgRating;
    @Expose
    private String title;
    @Expose
    @SerializedName("reviewText")
    private String text;
    @Expose
    @SerializedName("user")
    private String nickname;
    @Expose
    private Date postTime;
    private boolean isRecom;

    /**
     * get avgRating
     *
     * @return avgRating
     */
    public int getAvgRating() {
        return avgRating;
    }

    /**
     * get title
     *
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * get text
     *
     * @return text
     */
    public String getText() {
        return text;
    }

    /**
     * get nickname
     *
     * @return nickname
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * get postTime
     *
     * @return postTime
     */
    public Date getPostTime() {
        return postTime;
    }

    /**
     * @param parameters parameters for review, include:
     *                   Hotel Id
     *                   Review Id
     *                   Average Rating
     *                   Review Title
     *                   Review Text
     *                   User Nick Name
     *                   Review Submission Time
     */
    public Review(Parameters parameters) {
        hotelId = parameters.hotelId;
        reviewId = parameters.reviewId;
        avgRating = parameters.averageRating;
        title = parameters.reviewTitle;
        text = parameters.reviewText;
        nickname = parameters.userNickName;
        postTime = parameters.reviewSubmissionTime;
        isRecom = parameters.isRecom;
    }

    public static class Parameters {
        private String hotelId;
        private String reviewId;
        private int averageRating;
        private String reviewTitle;
        private String reviewText;
        private String userNickName;
        private Date reviewSubmissionTime;
        private boolean isRecom;

        /**
         * Set Hotel Id of the review
         *
         * @param hotelId Hotel Id
         */
        public void setHotelId(String hotelId) {
            this.hotelId = hotelId;
        }

        /**
         * Set the review Id
         *
         * @param reviewId review ID
         */
        public void setReviewId(String reviewId) {
            this.reviewId = reviewId;
        }

        /**
         * Set average rating for the hotel in this review
         *
         * @param averageRating overall average rating
         */
        public void setAverageRating(double averageRating) {
            if (averageRating > 5 || averageRating < 1) {
                throw new InvalidRatingException();
            }
            this.averageRating = (int) Math.round(averageRating);
        }

        /**
         * Set title of the review
         *
         * @param reviewTitle the review title
         */
        public void setReviewTitle(String reviewTitle) {
            this.reviewTitle = reviewTitle;
        }

        /**
         * Set the review content
         *
         * @param reviewText the review content
         */
        public void setReviewText(String reviewText) {
            this.reviewText = reviewText;
        }

        /**
         * Set review poster's nick name
         *
         * @param userNickName poster's nick name
         */
        public void setUserNickName(String userNickName) {
            this.userNickName = userNickName;
        }

        /**
         * Set review's post time
         *
         * @param reviewSubmissionTime submission time
         */
        public void setReviewSubmissionTime(Date reviewSubmissionTime) {
            this.reviewSubmissionTime = reviewSubmissionTime;
        }

        /**
         * Set review's post time
         *
         * @param reviewSubmissionTime submission time string
         */
        public void setReviewSubmissionTime(String reviewSubmissionTime) {
            try {
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                this.reviewSubmissionTime = format.parse(reviewSubmissionTime);
            } catch (java.text.ParseException e) {
                throw new ParseException();
            }
        }

        /**
         * Set if review recommend the hotel
         *
         * @param isRecom is review recommend the hotel
         */
        public void setIsRecom(boolean isRecom) {
            this.isRecom = isRecom;
        }

        /**
         * get HotelId
         *
         * @return hotel ID
         */
        public String getHotelId() {
            return hotelId;
        }

        /**
         * @return string representative of the class
         */
        @Override
        public String toString() {
            return "(" + hotelId + "," + reviewId + "）： " + reviewTitle;
        }
    }

    /**
     * @return hotel Id
     */
    public String getHotelId() {
        return hotelId;
    }

    /**
     * @return review Id
     */
    public String getReviewId() {
        return reviewId;
    }

    @Override
    public String toString() {
        SimpleDateFormat df = new SimpleDateFormat("E LLL dd HH:mm:ss zzz yyyy");
        String user = nickname.equals("") ? "Anonymous" : nickname;
        return String.format(REVIEW_DISPLAY_FORMAT,
                user, df.format(postTime), avgRating, title, text);
    }

    @Override
    public int compareTo(Review o) {
        if (this.postTime.after(o.postTime)) {
            return -1;
        } else if (this.postTime.before(o.postTime)) {
            return 1;
        } else {
            if (this.nickname.compareTo(o.nickname) > 0) {
                return 1;
            } else if (this.nickname.compareTo(o.nickname) < 0) {
                return -1;
            } else {
                return this.reviewId.compareTo(o.getReviewId());
            }
        }
    }
}
