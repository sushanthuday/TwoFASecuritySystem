import React, { Component } from 'react';
import { Button, FormGroup, FormControl, ControlLabel } from "react-bootstrap";
import axios from 'axios';
import logo from './logo.svg';
import './App.css';
import Modal from './Modal';

class App extends Component {
  constructor(props) {
    super(props);

    this.state = {
      username: "hello@mikka.se",
      password: "asdfgh",
      verificationCode: "",
      isOpenModal: false,
      message: ''
    };

  }

  validateForm() {
    return this.state.username.length > 0 && this.state.password.length > 0 && this.state.verificationCode.length > 0;
  }

  handleChange = event => {
    this.setState({
      [event.target.id]: event.target.value
    });
  }

  toggleModal = () => {
    this.setState({
      isOpenModal: !this.state.isOpenModal,
    });
  }

  handleSubmit = event => {
    event.preventDefault();

    axios.post('http://localhost:8080/api/v1/authorize', {
      username: this.state.username,
      password: this.state.password,
      verificationCode: this.state.verificationCode
    })
    .then(response => {
      this.setState({
        message: 'Logged in successfully!'
      });
      this.toggleModal();
      console.log(response);
    })
    .catch(error => {
      this.setState({
        message: 'Logged in failed!' + error
      });
      this.toggleModal();
      console.log(error);
    });
  }

  render() {
    return (
      <div className="App">
        <header className="App-header">
          <img src={logo} className="App-logo" alt="logo" />
          <h1 className="App-title">Welcome to the demonstration of 2-factor authentication</h1>
        </header>
        <h1 className="App-title">Registration - Pre-registered Account</h1>
        <p className="App-intro">
        Username: hello@mikka.se <br/>
        Password: asdfgh <br/>
        QR Code (Scan this with Google Authenticator app): <br/>
        <img alt="Google Authenticator QR Code" src="https://chart.googleapis.com/chart?chs=200x200&chld=M%%7C0&cht=qr&chl=otpauth%3A%2F%2Ftotp%2FMiKKaDemoApplication%3Anguyenbeo%3Fsecret%3DAHLOJ6UVMPRFTLJW%26issuer%3DMiKKaDemoApplication"/>
        </p>
        <hr />
        <h1 className="App-title">Login</h1>
        <p className="App-intro">
          Open up your Google Authenticator app to grap Time-based OTP or so-called Verification Code below. Along with pre-defined username and password pair above.
        </p>
        <div className="Login">
        <form onSubmit={this.handleSubmit}>
          <FormGroup controlId="username" bsSize="large">
            <ControlLabel>Username</ControlLabel>
            <FormControl
              autoFocus
              type="email"
              value={this.state.username}
              onChange={this.handleChange}
            />
          </FormGroup>
          <FormGroup controlId="password" bsSize="large">
            <ControlLabel>Password</ControlLabel>
            <FormControl
              value={this.state.password}
              onChange={this.handleChange}
              type="password"
            />
          </FormGroup>
          <FormGroup controlId="verificationCode" bsSize="large">
            <ControlLabel>Verification Code</ControlLabel>
            <FormControl
              autoFocus
              type="Text"
              value={this.state.verificationCode}
              onChange={this.handleChange}
            />
          </FormGroup>
          <Button
            block
            bsSize="large"
            disabled={!this.validateForm()}
            type="submit"
          >
            Login
          </Button>
        </form>
      </div>
        <Modal show={this.state.isOpenModal}
          onClose={this.toggleModal}>
          <h2>{this.state.message}</h2>
        </Modal>
      </div>
    );
  }
}

export default App;
