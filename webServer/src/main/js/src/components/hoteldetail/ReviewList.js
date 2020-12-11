import React from 'react';
import Review from "./Review";
import ReviewSubmit from "./ReviewSubmit";

class ReviewList extends React.Component {
    constructor(props) {
        super(props);
        console.log("reviews in  ReviewList", props.reviews)
    };

    render() {
        const reviews = this.props.reviews;
        let listItems;
        console.log("reviews", reviews);
        if(reviews === undefined) {
            listItems = <li />;
        }
        else {
            listItems = reviews.map((review) =>{
                return <li key={review.id}> <Review info={review} /> </li>;
            });
        }

        return (
            <div>
                <div>
                    <ReviewSubmit hotelId={this.props.hotelId} onReviewSubmit={this.props.onReviewChange}/>
                </div>
                <div>
                    <h1> Reviews for this Hotel: </h1>
                    <ul>{listItems}</ul>
                </div>
            </div>
        );
    }
}

export default ReviewList