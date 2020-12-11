import React from 'react';
import DeletableHotel from "./DeletableHotel";
import EditableReview from "./EditableReview";

class SavedHotels extends React.Component {
    constructor(props) {
        super(props);
        console.log("reviews in  ReviewList", props.reviews);
        this.handleHotelsChange = this.handleHotelsChange.bind(this);
        this.handlerClear = this.handlerClear.bind(this);
    };

    handleHotelsChange() {
        this.props.onHotelsChange();
    }

    handlerClear() {
        fetch(process.env.REACT_APP_WEB_API + 'savedhotel', {
            method: 'DELETE',
            credentials: 'include',
            mode: 'cors',
            body: JSON.stringify({})
        })
            .then(result => {
                console.log("result", result);
                return result.text();
            }).then(data => {
            console.log("data", data);
            let res = JSON.parse(data);
            console.log("res", res);
            if(res.success === true) {
                this.props.onHotelsChange();
            }
        })
    }

    render() {
        const hotels = this.props.hotels;
        let listItems;
        console.log("hotels", hotels);
        if(hotels === undefined) {
            listItems = <li />;
        }
        else {
            listItems = hotels.map((hotel) =>{
                console.log("hotel", hotel);
                return <a href="#" className="list-group-item list-group-horizontal flex-column align-items-start">
                        <DeletableHotel info={hotel} onHotelChage={this.handleHotelsChange}/>
                        </a>;
            });
        }

        return (
            <div>
                <div className="row-fluid">
                     <h3> Your Saved Hotels </h3>
                     <button className="btn btn-primary" onClick={this.handlerClear}> Clear </button>
                    <br/>
                    <br/>
                </div>
                <div className="list-group list-group-horizontal">
                    <ul>{listItems}</ul>
                </div>
            </div>
        );
    }
}

export default SavedHotels