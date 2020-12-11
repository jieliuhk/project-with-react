import React from 'react';

class EditableReview extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            isEdit: false,
            title: this.props.info.title,
            text: this.props.info.text,
            rating: this.props.info.rating
        };
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handlerModeChange = this.handlerModeChange.bind(this);
        this.handleInputChange = this.handleInputChange.bind(this);
        this.handlerDelete = this.handlerDelete.bind(this);
    };

    handleSubmit(event) {
        const cur = new Date();
        const newReview = {
            id: this.props.info.id,
            title: this.state.title,
            text: this.state.text,
            rating: this.state.rating,
            posttime: cur.toISOString()
        };
        fetch(process.env.REACT_APP_WEB_API + 'reviews', {
            method: 'PUT',
            credentials: 'include',
            mode: 'cors',
            body: JSON.stringify(newReview)
        })
            .then(result => {
                console.log("result", result);
                return result.text();
            }).then(data => {
            let res = JSON.parse(data);
            console.log("res", res);
            if(res.success === true) {
                this.props.onReviewChage();
            }
        })
        event.preventDefault();
    }

    handlerDelete() {
        const reviewId = {
            id: this.props.info.id,
        };
        fetch(process.env.REACT_APP_WEB_API + 'reviews', {
            method: 'DELETE',
            credentials: 'include',
            mode: 'cors',
            body: JSON.stringify(reviewId)
        })
            .then(result => {
                console.log("result", result);
                return result.text();
            }).then(data => {
            console.log("data", data);
            let res = JSON.parse(data);
            console.log("res", res);
            if(res.success === true) {
                this.props.onReviewChage();
            }
        })
    }

    handlerModeChange() {
        let curState = this.state.isEdit;
        this.setState({isEdit: !curState});
    }

    handleInputChange(event) {
        const target = event.target;
        const value = target.value;
        const name = target.name;

        this.setState({
            [name]: value
        });
    }

    render() {
        let title = this.state.title;
        let editButtonDisplay = this.state.isEdit ? "Cancel" : "Edit";
        let review;

        if(title.toString().trim() === "") {
            title = "No Title"
        }

        if(this.state.isEdit) {
            review = <div>
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
        }
        else {
            review = <div>
                        <h3> {title} </h3>
                        <p> {this.state.text} </p>
                        <label> Rating:  </label> <label> {this.state.rating} </label> <br/>
                    </div>
        }

        return (
            <div>
                {review}
                <br/>
                <button className="btn btn-secondary" onClick={this.handlerModeChange}> {editButtonDisplay} </button>
                <label>&nbsp; &nbsp; &nbsp;</label>
                <button className="btn btn-secondary" onClick={this.handlerDelete}> Delete </button>
                <br/>
            </div>
        );

    }
}

export default EditableReview