import React from 'react';

class BasicInfo extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            saveButtonText: "Save",
            saveButtonStyle: "btn btn-primary active"
        };
        this.handleOnSave = this.handleOnSave.bind(this);
    };

    handleOnSave() {
        if(this.state === "Saved") {
            return;
        }

        const hotelId = {
            hotelId: parseInt(this.props.info.id),
        };
        console.log("hotelId", hotelId);

        fetch(process.env.REACT_APP_WEB_API + 'savedhotel', {
            method: 'POST',
            credentials: 'include',
            mode: 'cors',
            body: JSON.stringify(hotelId)
        })
            .then(result => {
                console.log("result", result);
                return result.text();
            })
            .then(data => {
            console.log("data", data);
            let res = JSON.parse(data);
            console.log("res", res);
            if(res.success === true) {
                this.setState({
                    saveButtonText: "Saved",
                    saveButtonStyle: "btn btn-primary"
                });
            }
        })
    }

    render() {
        return (
            <div>
                <div className="hotelDetail">
                    <h1> {this.props.info.name} </h1>
                    <button
                        type="button"
                        className= {this.state.saveButtonStyle}
                        onClick={this.handleOnSave}>

                        {this.state.saveButtonText}
                    </button>
                    <br />
                    <br />
                    <table class="table table-hover">
                        <tbody>
                            <tr>
                                <td>
                                    Address:
                                </td>
                                <td>
                                    {this.props.info.address}
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    City: {this.props.info.city}
                                </td>
                                <td>
                                    State: {this.props.info.state}
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    Latitude:  {this.props.info.latitude}
                                </td>
                                <td>
                                    Longitude: {this.props.info.longitude}
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        );
    }
}

export default BasicInfo


