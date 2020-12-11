import React from 'react';

class DeletableHotel extends React.Component {
    constructor(props) {
        super(props);
        this.handlerDelete = this.handlerDelete.bind(this);
    };

    handlerDelete() {
        const hotelId = {
            hotelId: this.props.info.hotelid,
        };
        fetch(process.env.REACT_APP_WEB_API + 'savedhotel', {
            method: 'DELETE',
            credentials: 'include',
            mode: 'cors',
            body: JSON.stringify(hotelId)
        })
            .then(result => {
                console.log("result", result);
                return result.text();
            }).then(data => {
            console.log("data", data);
            let res = JSON.parse(data);
            console.log("res", res);
            if(res.success === true) {
                this.props.onHotelChage();
            }
        })
    }

    render() {
        return (
            <div>
                <br/>
                <h3> {this.props.info.name} </h3>
                <p> {this.props.info.address} </p>
                <button className="btn btn-secondary" onClick={this.handlerDelete}> Delete </button>
                <br/>
            </div>
        );
    }
}

export default DeletableHotel