import React, { Component } from "react";
import { createClient as createPhilipsHueClient } from "../util/philipshue";

class PhilipsHueConnectDialog extends Component {
	state = {
		client: {},
		clientName: "",
		userName: "",
		bridgeIp: ""
	};

	close = () => this.props.onClose();

	connectHueBridge = () => {
		const clientName = this.state.userName ? this.state.name : `clientId-${Math.random()}`;
		createPhilipsHueClient(clientName, this.state.bridgeIp, this.state.userName).then(client => {
			console.log("createPhilipsHueClient", client);
			this.setState({
				client
			});
		});
	};

	componentWillReceiveProps(nextProps) {
		this.setState({
			userName: nextProps.config.philipsHue.userName,
			bridgeIp: nextProps.config.philipsHue.bridgeIp
		});
	}

	handleBridgeIpChange = event => {
		this.setState({ bridgeIp: event.target.value });
	};

	handleClientNameChange = event => {
		this.setState({ clientName: event.target.value });
	};

	handleUserNameChange = event => {
		this.setState({ username: event.target.value });
	};

	render() {
		return (
			<div className={`modal ${this.props.visible ? "is-active" : ""}`}>
				<div onClick={this.close} className="modal-background" />
				<div className="modal-content">
					<div className="content">
						<div className="notification">
							<h3>Connect Philips Hue Bridge</h3>
							<p>
								To connect this application you will need connect it with your hue bridge. Press the
								link button on the Philips Hue Bridge and click the <strong>Connect</strong> button.
								Make sure to save the username and copy it to the service config for future uses.
							</p>
							<div className="field">
								<label className="label">Bridge IP address</label>
								<div className="control">
									<input
										className="input"
										type="text"
										placeholder="1.2.3.4"
										value={this.state.bridgeIp}
										onChange={this.handleBridgeIpChange}
									/>
								</div>
							</div>
							<div className="field">
								<label className="label">Client name (optional)</label>
								<div className="control">
									<input
										className="input"
										type="text"
										placeholder="my awesome client"
										value={this.state.clientName}
										onChange={this.handleClientNameChange}
									/>
								</div>
							</div>
							<div className="field">
								<label className="label">Username / Api secret)</label>
								<div className="control">
									<input
										className="input"
										type="text"
										placeholder="##########"
										value={this.state.userName}
										onChange={this.handleUserNameChange}
									/>
								</div>
							</div>
							<pre>{JSON.stringify(this.state.client, null, 2)}</pre>
							<a className={"button is-primary"} onClick={this.connectHueBridge}>
								Connect
							</a>
						</div>
					</div>
				</div>
				<button onClick={this.close} className="modal-close is-large" aria-label="close" />
			</div>
		);
	}
}

export default PhilipsHueConnectDialog;
