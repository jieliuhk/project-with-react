import React from 'react';
import SearchForm from "./components/hotel/SearchForm";
import HotelList from "./components/hotel/HotelList";
import Map from "./components/hotel/Map";

class HotelPage extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            cities: []
        };
        this.handleHotelsChange = this.handleHotelsChange.bind(this);
        this.initial = this.initial.bind(this);
    };

    componentDidMount() {
        this.initial();
    }

    handleHotelsChange(hotels) {
        this.props.onHotelsChange(hotels);
    }

    initial() {
        fetch(process.env.REACT_APP_WEB_API + 'hotels?hotelCities=true', {
            method: 'GET',
            mode: 'cors',
            credentials: 'include',
            headers: { 'Content-type': 'application/x-www-form-urlencoded' },
        })
            .then(result => {
                console.log("result", result);
                return result.text();
            })
            .then(data => {
                console.log("data", data);
                let response = JSON.parse(data);
                console.log("response", response);
                if(response.success === true) {
                    this.setState({cities: response.cities});
                }
            })
    }


    render() {

        const markers = this.props.hotels.map( hotel => (
            {
                id: hotel.id,
                lat:  parseFloat(hotel.latitude),
                lng:  parseFloat(hotel.longitude)
            })
        );

        console.log("markers111", markers)

        const center = {
            lat: parseFloat("37.766977"),
            lng: parseFloat("-122.449013")
        };

        return (
            <div>
                <Map markers={markers} center={center}/>
                <br />
                <br />
                <SearchForm onHotelsChange={this.handleHotelsChange} cities={this.state.cities}/>
                <br />
                <HotelList hotels={this.props.hotels} onHotelClick={this.props.onHotelClick}/>
            </div>
        );
    }
}

export default HotelPage