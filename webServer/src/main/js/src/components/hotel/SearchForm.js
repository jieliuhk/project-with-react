import React from 'react';
import Review from "../hoteldetail/Review";

class SearchForm extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            city: "",
            nameKeyword: "",
            info: ""
        };
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleInputChange = this.handleInputChange.bind(this);
    };


    handleSubmit(event) {
        this.search(this.state.city, this.state.nameKeyword);
        event.preventDefault();
    }

    handleInputChange(event) {
        const target = event.target;
        const value = target.value;
        const name = target.name;

        this.setState({
            [name]: value
        });
    }

    search(city, keyword) {
        let para = 'city=' + city + "&keyword=" + keyword;
        console.log("para", para);
        fetch(process.env.REACT_APP_WEB_API + 'hotels?' + para, {
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
                this.props.onHotelsChange(response.hotels);
            })
    }

    render() {
        const cities = this.props.cities;
        let listItems;
        console.log("cities in form", cities);
        if(cities === undefined) {
            listItems = <li />;
        }
        else {
            listItems = cities.map((city) =>{
                return <option key={city.city} value={city.city}>{city.city}</option>;
            });
        }

        return (
            <div>
                    <form className="form-inline" onSubmit={this.handleSubmit}>
                        <label  className="mr-sm-2">City: &nbsp;</label>
                        <select className="form-control mb-2 mr-sm-2" name="city" onChange={this.handleInputChange}>
                            {listItems}
                        </select>
                        <label className="mr-sm-2">&nbsp; &nbsp; Keyword &nbsp; </label>
                        <input
                            className="form-control mb-2 mr-sm-2"
                            type="text"
                            name="nameKeyword"
                            value={this.state.nameKeyword}
                            onChange={this.handleInputChange} />
                        <label> &nbsp; &nbsp; </label>
                        <input className="btn btn-primary mb-2" type="submit" value="Search" />
                    </form>
            </div>
        );
    }
}

export default SearchForm


