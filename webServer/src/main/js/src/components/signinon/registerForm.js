import React from 'react';

class RegisterForm extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            username: "",
            password: "",
            confirmPassword: "",
            info: ""
        };
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleInputChange = this.handleInputChange.bind(this);
    };


    handleSubmit(event) {
        let reg = "(?=.*\\d)(?=.*[A-Za-z])(?=.*[@#$%./]).{5,10}";
        let password = this.state.password;
        let userName = this.state.username;
        let confirmPassword = this.state.confirmPassword;

        if(userName === "") {
            this.setState({info: "Please Input Username"});
        }
        else if(password !== confirmPassword) {
            this.setState({info: "Please input same password"});
        }
        else if(!new RegExp(reg).test(password)) {
            this.setState({info: "Invalid Password, the length is between 5 to 10 characters,\n" +
                    "contains at least one number, one letter and one special\n" +
                    "character"});
        }
        else {
            this.register(this.state.username, this.state.password);
        }

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

    register(name, password) {
        let para = 'name=' + name + '&password=' + password;
        this.requestTGT(para)
            .then( res => {
                console.log("res", res);
                this.requestTicket(res, name, password)
                    .then(res => {
                        console.log("res", res);
                        this.requestSSRegister(res, name, password)
                            .then(data => {
                                let res = JSON.parse(data);
                                this.props.onResultChange(res.success);
                                if(res.success !== true) {
                                    this.setState({info: "Register Fail"});
                                }
                                console.log("info", this.state.info);
                            })
                    })
            })
    }

    requestTGT(para) {
        console.log(process.env.REACT_APP_ID_API);
        return fetch(process.env.REACT_APP_ID_API + 'registration', {
            method: 'POST',
            mode: 'cors',
            headers: { 'Content-type': 'application/x-www-form-urlencoded' },
            body: para
        })
            .then(result => {
                console.log("result", result);
                return result.text();
            })
    }

    requestTicket(res, name, clientKey) {
        let response = JSON.parse(res);
        let tgt = response.TGT;
        let sessionCypher = response.TGSSessionKey;
        let sessionKey = this.getSessionKey(sessionCypher, clientKey);
        let authenContent = '{"userName": ' + name + '}';
        let requestPara = "service=Travel Helper&authenticator="
            + this.getAuthenticator(sessionKey, authenContent) + "&TGT=" + tgt;

        console.log("response", response);
        console.log("requestPara", requestPara);

        return fetch(process.env.REACT_APP_ID_API + 'ticket', {
            method: 'POST',
            mode: 'cors',
            headers: { 'Content-type': 'application/x-www-form-urlencoded' },
            body: requestPara
        })
            .then(result => {
                console.log("result", result);
                return result.text();
            })
    }

    requestSSRegister(res, name, clientKey) {
        let response = JSON.parse(res);
        let ticket = response.ticket;
        let sessionCypher = response.SSSessionKey;
        let sessionKey = this.getSessionKey(sessionCypher, clientKey);
        let authenContent = '{"userName": ' + name + '}';
        let requestPara = "service=Travel Helper&authenticator="
            + this.getAuthenticator(sessionKey, authenContent) + "&ticket=" + ticket;

        return fetch(process.env.REACT_APP_WEB_API + 'registration', {
            method: 'POST',
            credentials: 'include',
            mode: 'cors',
            headers: { 'Content-type': 'application/x-www-form-urlencoded' },
            body: requestPara
        })
            .then(result => {
                console.log("result", result);
                return result.text();
            })
    }

    getAuthenticator(sessionKey, content) {
        //TODO: encrypt content use AES by sessionKey
        return content;
    }

    getSessionKey(sessionCypher, clientKey) {
        //TODO: decrypt sessionCypher use AES by clientKey
        return sessionCypher;
    }


    render() {
        return (
            <div>
                <div className="form">
                    <form className="form-row" onSubmit={this.handleSubmit}>
                        <label className="form-label">User Name</label> <br/>
                        <input
                            type="text"
                            name="username"
                            className="form-control"
                            value={this.state.username}
                            onChange={this.handleInputChange} /> <br/>
                        <br/>
                        <label className="form-label">Password</label> <br/>
                        <input
                            type="password"
                            name="password"
                            className="form-control"
                            value={this.state.password}
                            onChange={this.handleInputChange} /> <br/>
                        <br/>
                        <label className="form-label">Confirm Password</label> <br/>
                        <input
                            type="password"
                            name="confirmPassword"
                            className="form-control"
                            value={this.state.confirmPassword}
                            onChange={this.handleInputChange} /> <br/>
                        <br/>
                        <input className="btn btn-primary mb-2" type="submit" value="submit" />
                    </form>
                    <p>{this.state.info}</p>
                </div>
            </div>
        );
    }
}

export default RegisterForm


