import React, {Component} from 'react';
import NavBar from "./NavBar";
import Card from "./Card";
import SpotifyInfoCard from "./SpotifyInfoCard";
import {authenticate as authenticateSpotify, fetchPlayStatus} from "../util/spotify";
import {fetchLightStatus, fetchConfig, setLightMatching} from "../util/philipshue";
import PhilipsHueInfoCard from "./PhilipsHueInfoCard";
import PhilipsHueConnectDialog from "./PhilipsHueConnectDialog";

class App extends Component {

	state = {
		spotify: {
			colors: [],
			eligibleColors: [],
			mainColor: null,
			artists: "",
			album: "",
			song: ""
		},
		light_status: {
			lights: []
		},
		view: {
			dialogOpen: false
		},
		config: {
			lightMatching: false,
			philipsHue: {
				userName: "",
				bridgeIp: ""
			},
			spotify: {
				clientId: "",
				clientSecret: ""
			}
		}
	};

	componentDidMount() {
		this.update();
		setInterval(() => this.update(), 1000)
	}

	update = () => {
		this.updateConfig();
		this.updatePlayStatus();
		this.updateLightStatus();
	};

	toggleLightMatching = () => {
		let newState = !this.state.config.lightMatching;
		setLightMatching(newState)
			.then(this.updateConfig);

	};

	loginWithSpotify = () => {
		authenticateSpotify(this.state.config.spotify.clientId,() =>
			setTimeout(() => this.update(), 2000)
		)
	};

	updatePlayStatus = () => {
		fetchPlayStatus().then((res) => {
			res && this.setState({
				spotify: {
					...this.state.spotify,
					hue_color: null,
					...res
				}
			});
		})
	};

	updateLightStatus = () => {
		fetchLightStatus().then((light_status) => {
			light_status && this.setState({
				...this.state.light_status,
				light_status
			});
		})
	};

	updateConfig = () => {
		fetchConfig()
			.then(config => {
				config && this.setState({ config });
			});
	};

	togglePhilipsHueDialog = (dialogOpen = !this.state.dialogOpen) => {
		this.setState({
			view: {
				...this.state.view,
				dialogOpen: dialogOpen
			}
		})
	};

	handleClosePhilipsHueDialog = () => {
		this.togglePhilipsHueDialog(false)
	};

	handleOpenPhilipsHueDialog = () => {
		this.togglePhilipsHueDialog(true)
	};

	renderControls = () => (
		<Card title={"Controls"}>
			<div className="columns">
				<div className="column is-narrow">
					<a className={"button is-success"} onClick={this.loginWithSpotify}>Connect Spotify</a>
				</div>
				<div className="column is-narrow">
					<a className={"button is-info"} onClick={this.handleOpenPhilipsHueDialog}>Connect Hue Bridge</a>
				</div>
				<div className="column is-narrow">
					<a className={"button is-primary"} onClick={this.toggleLightMatching}>
						{this.state.config.lightMatching ? "Disable" : "Enable"} light matching</a>
				</div>
			</div>
		</Card>
	);


	renderSpotifyInfo = () => (
		<Card title={"Spotify"}>
			<SpotifyInfoCard
				status={this.state.spotify}
			/>
		</Card>
	);

	renderLightInfo = () => {
		const { lights } = this.state.light_status;
		return (
			<Card title={"Philips Hue"}>
				<PhilipsHueInfoCard lights={lights}/>
			</Card>
		);
	};

	render() {
		console.log('state', this.state);
		return (
			<div>
				<NavBar/>
				<div className="content-wrapper">
					<div className="content-container">
						{this.renderControls()}
						{this.renderSpotifyInfo()}
						{this.renderLightInfo()}
					</div>
				</div>
				<PhilipsHueConnectDialog
					config={this.state.config}
					visible={this.state.view.dialogOpen}
					onClose={this.handleClosePhilipsHueDialog}
				/>
			</div>
		);
	}


}

export default App;
