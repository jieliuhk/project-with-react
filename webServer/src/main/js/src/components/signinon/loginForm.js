import React from 'react';

class LoginForm extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            username: "",
            password: "",
            info: ""
        };
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleInputChange = this.handleInputChange.bind(this);
    };


    handleSubmit(event) {
        let reg = "(?=.*\\d)(?=.*[A-Za-z])(?=.*[@#$%./]).{5,10}";
        let password = this.state.password;
        let userName = this.state.username;

        if(userName === "") {
            this.setState({info: "Please Input Username"});
        }
        else if(!new RegExp(reg).test(password)) {
            this.setState({info: "Invalid Password, the length is between 5 to 10 characters,\n" +
                    "contains at least one number, one letter and one special\n" +
                    "character"});
        }
        else {
            this.login(this.state.username, this.state.password);
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

    login(name, password) {
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
                                    this.setState({info: "Login Fail"});
                                }
                            })
                    })
            })
    }

    requestTGT(para) {
        console.log("TGT para", para);
        return fetch(process.env.REACT_APP_ID_API + 'tgs', {
            method: 'POST',
            mode: 'cors',
            headers: { 'Content-type': 'application/x-www-form-urlencoded' },
            body: para
        })
            .then(result => {
                console.log("resultTGT", result);
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
        console.log("ticket requestPara", requestPara);

        return fetch(process.env.REACT_APP_ID_API + 'ticket', {
            method: 'POST',
            mode: 'cors',
            headers: { 'Content-type': 'application/x-www-form-urlencoded' },
            body: requestPara
        })
            .then(result => {
                console.log("resultTicket", result);
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

        console.log("SS para", requestPara);

        return fetch(process.env.REACT_APP_WEB_API + 'verify', {
            method: 'POST',
            credentials: 'include',
            mode: 'cors',
            headers: { 'Content-type': 'application/x-www-form-urlencoded' },
            body: requestPara
        })
            .then(result => {
                console.log("resultSPReg", result);
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
                        <label>User Name</label> <br/>
                        <input
                            type="text"
                            name="username"
                            value={this.state.username}
                            className="form-control"
                            onChange={this.handleInputChange} /> <br/>
                        <br/>
                        <label>Password</label> <br/>
                        <input
                            type="password"
                            name="password"
                            value={this.state.password}
                            className="form-control"
                            onChange={this.handleInputChange} /> <br/>
                        <input className="btn btn-primary mb-2" type="submit" value="submit" />
                    </form>
                    <p>{this.state.info}</p>
                </div>
            </div>
        );
    }
}

export default LoginForm


