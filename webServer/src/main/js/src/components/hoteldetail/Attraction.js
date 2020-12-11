import React from 'react';

class Attraction extends React.Component {
    constructor(props) {
        super(props);
    };

    render() {
        return (
            <div>
                <h1> {this.props.info.name} </h1>
                <label> Address:  </label> <label> {this.props.info.formatted_address} </label>
            </div>
        );
    }
}

export default Attraction