import React from 'react';
import UserReviews from "./components/user/UserReviews";
import SavedHotels from "./components/user/SavedHotels";

class UserPage extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            mode: 0, //0: user profile, 1: reviews 2: hotels
            reviews: [],
            hotels: []
        };
        this.handleReviewsChange = this.handleReviewsChange.bind(this);
        this.loadReviews = this.loadReviews.bind(this);
        this.handleHotelsChange = this.handleHotelsChange.bind(this);
        this.loadHotels = this.loadHotels.bind(this);
    };

    handleReviewsChange() {
        this.setState({mode: 1})
        this.loadReviews();
    }

    handleHotelsChange() {
        this.setState({mode: 2})
        this.loadHotels();
    }

    loadReviews() {
        fetch(process.env.REACT_APP_WEB_API + 'reviews?userName=' + this.props.userInfo.name, {
            method: 'GET',
            credentials: 'include'
        })
            .then(result => {
                console.log("result", result);
                return result.text();
            }).then(data => {
                let res = JSON.parse(data);
                console.log("data", res);
                console.log("reviews", res.reviews);
                this.setState({reviews: res.reviews});
        })
    }

    loadHotels() {
        fetch(process.env.REACT_APP_WEB_API + 'savedhotel', {
            method: 'GET',
            credentials: 'include'
        })
            .then(result => {
                console.log("result", result);
                return result.text();
            })
            .then(data => {
            let res = JSON.parse(data);
            console.log("res", res);
            console.log("hotels", res.hotels);
            this.setState({hotels: res.hotels});
        })
    }

    render() {
        let content;
        if(this.state.mode === 1) {
            content = <UserReviews reviews={this.state.reviews} onReviewsChange={this.handleReviewsChange}/>
        }
        else if(this.state.mode === 2) {
            content = <SavedHotels hotels={this.state.hotels} onHotelsChange={this.handleHotelsChange}/>
        }

        return (
            <div>
                <div className="btn-group btn-group-lg">
                    <button  className="btn btn-primary" onClick={this.handleReviewsChange}> My Reviews </button>
                    <button  className="btn btn-primary" onClick={this.handleHotelsChange}> Saved Hotels </button>
                </div>
                <div className="user page content">
                    {content}
                </div>
            </div>
        );
    }
}

export default UserPage