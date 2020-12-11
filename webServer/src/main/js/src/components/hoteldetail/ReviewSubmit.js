import React from 'react';

class ReviewSubmit extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            title: "",
            text: "",
            rating: 5,
        };
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleInputChange = this.handleInputChange.bind(this);
        this.prepareReview = this.prepareReview.bind(this);
    };


    handleSubmit(event) {
        this.submit(this.prepareReview());
        event.preventDefault();
    }

    handleInputChange(event) {
        const target = event.target;
        const value = target.value;
        const name = target.name;

        this.setState({
            [name]: value
        });
    }

    prepareReview() {
        let cur = new Date();
        return {
                "hotelId": this.props.hotelId,
                "review": {
                     title: this.state.title,
                     text: this.state.text,
                     rating: this.state.rating,
                     posttime: cur.toISOString()
                 }
            };
    }

    submit(newReview) {
        fetch(process.env.REACT_APP_WEB_API + 'reviews', {
            method: 'POST',
            credentials: 'include',
            mode: 'cors',
            body: JSON.stringify(newReview)
        })
            .then(result => {
                console.log("result", result);
                return result.text();
            })
            .then(data => {
                console.log("data", data);
                let response = JSON.parse(data);
                if(response.success === true) {
                    this.props.onReviewSubmit();
                }
            })
    }

    render() {
        return (
                <div className="text-Left">
                    <h2>Leave a Review: </h2>
                    <form onSubmit={this.handleSubmit}>
                        <div className="form-group">
                            <label htmlFor="text">Title:</label>
                            <input type="text"
                                   className="form-control"
                                   placeholder="Enter Title"
                                   name="title"
                                   value={this.state.title}
                                   onChange={this.handleInputChange} />
                        </div>
                        <div className="form-group">
                            <label htmlFor="textarea">Comments:</label>
                            <input type="textarea"
                                   className="form-control"
                                   placeholder="Say Something...."
                                   name="text"
                                   value={this.state.text}
                                   onChange={this.handleInputChange} />
                        </div>
                        <div className="form-group">
                            <label htmlFor="select">Rating:</label>
                            <select
                                name="rating"
                                value={this.state.rating}
                                onChange={this.handleInputChange}
                                className="form-control">
                                <option value="1">1</option>
                                <option value="2">2</option>
                                <option value="3">3</option>
                                <option value="4">4</option>
                                <option value="5">5</option>
                            </select>
                        </div>
                        <button type="submit" className="btn btn-primary">Submit: </button>
                    </form>
                </div>
        );
    }
}

export default ReviewSubmit


