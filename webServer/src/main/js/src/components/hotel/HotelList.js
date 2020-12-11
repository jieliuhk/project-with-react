import React from 'react';

class HotelList extends React.Component {
    constructor(props) {
        super(props);
    };

    render() {
        return (
            <div className="list-group">
                <Hotels hotels={this.props.hotels} onHotelNameClick={this.props.onHotelClick}/>
            </div>
        );
    }
}


function Hotels(props) {
    const hotels = props.hotels;
    console.log("hotels", hotels);
    if(hotels === undefined) {
        return <a />
    }
    else {
        return hotels.map((hotel) =>{
            let rating = hotel.averageRating === "null" ? "N/A" : parseFloat(hotel.averageRating);
            return <a href="#" className="list-group-item list-group-item-action"
                       onClick={props.onHotelNameClick.bind(this, hotel)}>
                        {hotel.name + ", Rating: " + rating}
                    </a>
        });
    }
}

export default HotelList


