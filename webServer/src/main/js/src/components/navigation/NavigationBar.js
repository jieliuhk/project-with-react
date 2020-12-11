import React from 'react';

class NavigationBar extends React.Component {
    constructor(props) {
        super(props);
        this.handleLogoutClicked = this.handleLogoutClicked.bind(this);
        this.handleBackClicked = this.handleBackClicked.bind(this);
        this.handleUserNameClicked = this.handleUserNameClicked.bind(this);
        this.handleHomeClicked = this.handleHomeClicked.bind(this);
    };

    handleLogoutClicked() {
        fetch(process.env.REACT_APP_WEB_API + 'logout', {
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
                if(response.success === true) {
                   this.props.onLogout();
                }
            })
    }

    handleBackClicked() {
        this.props.onRedirect(this.props.preMode);
    }

    handleHomeClicked() {
        this.props.onRedirect(2);
    }

    handleUserNameClicked() {
        this.props.onRedirect(4);
    }

    render() {
        return (
            <div>
                <nav className="navbar navbar-inverse">
                    <div className="container-fluid">
                        <div className="navbar-header">
                            <a className="navbar-brand" href="#" onClick={this.handleHomeClicked}> Home </a>
                        </div>
                        <div className="collapse navbar-collapse" id="myNavbar">
                            <ul className="nav navbar-nav">
                                <li><a href="#"> <span className="glyphicon glyphicon-arrow-left"></span> </a></li>
                            </ul>
                            <ul className="nav navbar-nav navbar-right">
                                <li><a href='#' onClick={this.handleUserNameClicked}>{this.props.userInfo.name}</a></li>
                                <li><a>You last login is {this.props.userInfo.lastLogin}</a></li>
                                <li><a href="#" onClick={this.handleLogoutClicked}><span className="glyphicon glyphicon-log-out"></span> Logout </a></li>
                            </ul>
                        </div>
                    </div>
                </nav>
            </div>
        );
    }
}


export default NavigationBar