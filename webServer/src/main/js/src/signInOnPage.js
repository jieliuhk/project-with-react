import React from 'react';
import RegisterForm from "./components/signinon/registerForm";
import LoginForm from "./components/signinon/loginForm";

class SignInOnPage extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            isRegister: false,
            linkText: "Register",
            promptText: "New User?",
            success: false
        };
        this.handlerChangeMode = this.handlerChangeMode.bind(this);
    };

    handlerChangeMode(event) {
        let isRegister = !this.state.isRegister;

        if(isRegister) {
            this.state.promptText = "Already have account?";
            this.state.linkText = "Sign In";
        }
        else {
            this.state.promptText = "New User?";
            this.state.linkText = "Register";
        }
        this.setState({isRegister: isRegister});
        event.preventDefault();
    }

    render() {
        const isRegister = this.state.isRegister;
        let form;
        if(isRegister) {
            form = <RegisterForm onResultChange={this.props.onStateChange}/>
        }
        else {
            form = <LoginForm onResultChange={this.props.onStateChange}/>
        }

        return (
            <div>
                <div className="board-row">
                    {form}
                    <label> {this.state.promptText} </label>
                    <a href="#" onClick={this.handlerChangeMode}> {this.state.linkText} </a>
                </div>
            </div>
        );
    }
}

export default SignInOnPage


