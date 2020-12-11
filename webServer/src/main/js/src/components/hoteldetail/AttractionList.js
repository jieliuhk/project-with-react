import React from 'react';
import Attraction from "./Attraction";

class AttractionList extends React.Component {
    constructor(props) {
        super(props);
        console.log("attractions in  AttractionList", props.attractions)
    };

    render() {
        return (
                <div className="list-group list-group-horizontal">
                    <Attractions attractions={this.props.attractions} />
                </div>
        );
    }
}


function Attractions(props) {
    console.log("attractions", props.attractions);
    const attractions = props.attractions.results;

    if(attractions === undefined) {
        return <a />
    }
    else {
        const listItems = attractions.map((attraction) =>{
            return  <a href="#" className="list-group-item list-group-horizontal flex-column align-items-start">
                        <Attraction info={attraction} />
                    </a>;
        });
        return (
            <div> {listItems} </div>
        );
    }
}

export default AttractionList