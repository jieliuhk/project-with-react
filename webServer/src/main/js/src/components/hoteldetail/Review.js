import React from 'react';

class Review extends React.Component {
    constructor(props) {
        super(props);
    };

    render() {
        let title = this.props.info.title;
        if(title.toString().trim() === "") {
            title = "No Title"
        }
        return (
            <div>
                <h3> {title} </h3>
                <label> By:  </label> <label> {this.props.info.nameuser + ", At :"} </label>
                <label> {this.props.info.posttime} </label> <br/>
                <label> Rating:  </label> <label> {this.props.info.rating} </label> <br/>
                <p> {this.props.info.text} </p>
            </div>
        );
    }
}

export default Review