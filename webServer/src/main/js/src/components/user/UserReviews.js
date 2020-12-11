import React from 'react';
import EditableReview from "./EditableReview";
import Attraction from "../hoteldetail/Attraction";

class UserReviews extends React.Component {
    constructor(props) {
        super(props);
        console.log("reviews in  ReviewList", props.reviews);
        this.handleReviewChange = this.handleReviewChange.bind(this);
    };

    handleReviewChange() {
        this.props.onReviewsChange();
    }

    render() {
        const reviews = this.props.reviews;
        let listItems;
        console.log("reviews", reviews);
        if(reviews === undefined) {
            listItems = <li />;
        }
        else {
            listItems = reviews.map((review) =>{
                return <a href="#" className="list-group-item list-group-horizontal flex-column align-items-start">
                            <EditableReview  info={review} onReviewChage={this.handleReviewChange}/>
                        </a>;
            });
        }

        return (
            <div>
                <h3> Your Reviews </h3>
                <div className="list-group list-group-horizontal">
                    {listItems}
                </div>
            </div>
        );
    }
}

export default UserReviews