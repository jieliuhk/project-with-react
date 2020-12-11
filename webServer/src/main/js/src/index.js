import React from 'react';
import ReactDOM from "react-dom";
import SignInOnPage from "./signInOnPage";
import HotelPage from "./HotelPage";
import HotelDetailPage from "./HotelDetailPage";
import NavigationBar from "./components/navigation/NavigationBar";
import UserPage from "./UserPage";

class IndexPage extends React.Component {

    constructor(props) {
        super(props);
        require('dotenv').config();


        this.state = {
            mode: 0, //0: blank page 1: identification page 2: hotels page 3: hotel Detail page 4: user page
            preMode: 0,
            user: {},
            curHotel: {},
            hotels: []
        };
        this.initial = this.initial.bind(this);
        this.handleVerify = this.handleVerify.bind(this);
        this.handleRequestHotelDetail = this.handleRequestHotelDetail.bind(this);
        this.handleLogout = this.handleLogout.bind(this);
        this.handleNavigation = this.handleNavigation.bind(this);
        this.handleHotelsChange = this.handleHotelsChange.bind(this);
    };

    componentDidMount() {
        this.initial();
    }

    handleVerify(success) {
        if(success) {
            this.initial();
        }
        else {
            this.setState({preMode: 1});
            this.setState({mode: 1});
        }
    }

    handleHotelsChange(hotels) {
        this.setState({hotels: hotels});
    }

    handleLogout() {
        this.setState({
            mode: 1,
            preMode: 1,
            user: {},
            curHotel: {},
            hotels: []
        });
    }

    handleNavigation(target) {
        let curMode = this.state.mode;
        if(curMode !== target) {
            this.setState({preMode: curMode});
        }
        this.setState({mode: target});
    }

    handleRequestHotelDetail(hotel) {
        this.handleNavigation(3);
        this.setState({curHotel:  hotel});
    }

    initial() {
        fetch(process.env.REACT_APP_WEB_API + 'users', {
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
                    this.setState({mode: 2})
                    this.setState({preMode: 2})
                    this.setState({user: response.userInfo})
                }
                else {
                    this.setState({preMode: 1})
                    this.setState({mode: 1})
                }
            })
    }

    render() {
        let mode = this.state.mode;
        let page;
        let navigationBar = <NavigationBar
            onLogout={this.handleLogout}
            onRedirect={this.handleNavigation}
            curMode={this.state.mode}
            preMode={this.state.preMode}
            userInfo={this.state.user}/>;

        if(mode === 1) {
            navigationBar = <div> <br /> <br /> </div>;
            page = <SignInOnPage onStateChange={this.handleVerify}/>
        }
        else if (mode === 2) {
            page = <HotelPage
                hotels={this.state.hotels}
                onHotelClick={this.handleRequestHotelDetail}
                onHotelsChange={this.handleHotelsChange} />
        }
        else if (mode === 3) {
            page = <HotelDetailPage hotelInfo={this.state.curHotel}/>
        }
        else if (mode === 4) {
            page = <UserPage userInfo={this.state.user} />
        }
        else {
            navigationBar = <div />;
            page = <div />
        }

        console.log(process.env);

        return (
            <div>
                {navigationBar}
                <div className="col-sm-2 sidenav" />
                <div className = "col-sm-8 text-center" > {page} </div>
                <div className="col-sm-2 sidenav" />
            </div>
        );
    }
}

ReactDOM.render(
    <IndexPage />,
    document.getElementById('root')
);