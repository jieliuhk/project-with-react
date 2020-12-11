import React from 'react';
import BasicInfo from "./components/hoteldetail/BasicInfo";
import ReviewList from "./components/hoteldetail/ReviewList";
import AttractionList from "./components/hoteldetail/AttractionList";
import Map from "./components/hoteldetail/Map";


class HotelDetailPage extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            reviews: [],
            attractions: [],
            attractionRadius: 20
        };
        this.handleReviewsRefresh = this.handleReviewsRefresh.bind(this);
        this.handleAttractionsRefresh = this.handleAttractionsRefresh.bind(this);
        this.handleInputChange = this.handleInputChange.bind(this);
        this.handleReviewsRefresh();
        this.handleAttractionsRefresh();
    };


    handleInputChange(event) {
        const target = event.target;
        const value = target.value;
        const name = target.name;

        this.setState({
            [name]: value
        });
    }

    handleReviewsRefresh() {
        fetch(process.env.REACT_APP_WEB_API + 'reviews?hotelId=' + this.props.hotelInfo.id, {
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
                let response = JSON.parse(data);
                console.log("response", response);
                console.log("success", response.success);
                console.log("reviews",  response.reviews);
                if(response.success === true) {
                    this.setState({reviews: response.reviews});
                }
            })
    }

    handleAttractionsRefresh() {
        let url = process.env.REACT_APP_WEB_API + 'attractions?hotelId='
            + this.props.hotelInfo.id + "&radius=" + this.state.attractionRadius;
        console.log("url", url);

        fetch(url, {
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
                let response = JSON.parse(data);
                console.log("attraction response", response);
                console.log("success", response.status);
                if(response.status === 'OK') {
                    this.setState({attractions: response});
                }
            })
    }

    render() {
        const config = {
            lat: parseFloat(this.props.hotelInfo.latitude),
            lng: parseFloat(this.props.hotelInfo.longitude)
        };

        return (
            <div>
                <BasicInfo info={this.props.hotelInfo} />
                <Map config={config}/>
                <br />
                <br />
                <label> Attractions in Near &nbsp; </label>
                <input value={this.state.attractionRadius}
                       name="attractionRadius"
                       onChange={this.handleInputChange} />
                <label> &nbsp; Miles: &nbsp; </label>
                <button className="btn btn-primary" onClick={this.handleAttractionsRefresh}> Show: </button>
                <br />
                <AttractionList attractions={this.state.attractions} />
                <br />
                <ReviewList reviews={this.state.reviews}
                            hotelId={this.props.hotelInfo.id}
                            onReviewChange={this.handleReviewsRefresh}/>
            </div>
        );
    }
}

export default HotelDetailPage