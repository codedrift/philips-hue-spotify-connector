import React, { Component } from "react";
import NavBar from "./NavBar";
import SpotifyInfoCard from "./SpotifyInfoCard";
import { authenticate as authenticateSpotify, fetchPlayStatus } from "../util/spotify";
import { fetchLightStatus, fetchConfig, setLightMatching, shuffleLights } from "../util/philipshue";
import PhilipsHueInfoCard from "./PhilipsHueInfoCard";
import PhilipsHueConnectDialog from "./PhilipsHueConnectDialog";
import { withStyles } from "material-ui/styles";
import Card, { CardContent, CardHeader } from "material-ui/Card";
import Button from "material-ui/Button";

const styles = theme => ({
	card: {
		marginTop: 20
	},
	button: {
		marginRight: 10
	}
});

class App extends Component {
	state = {
		spotify: {
			colors: [],
			eligibleColors: [],
			lightColors: [],
			mainColor: null,
			artists: "",
			album: "",
			song: "",
			artworkUrl: ""
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
		setInterval(() => this.update(), 1000);
	}

	update = () => {
		this.updateConfig();
		this.updatePlayStatus();
		this.updateLightStatus();
	};

	toggleLightMatching = () => {
		let newState = !this.state.config.lightMatching;
		setLightMatching(newState).then(this.updateConfig);
	};

	loginWithSpotify = () => {
		authenticateSpotify(this.state.config.spotify.clientId, () =>
			setTimeout(() => this.update(), 2000)
		);
	};

	updatePlayStatus = () => {
		fetchPlayStatus().then(res => {
			res &&
				this.setState({
					spotify: {
						...this.state.spotify,
						hue_color: null,
						...res
					}
				});
		});
	};

	updateLightStatus = () => {
		fetchLightStatus().then(light_status => {
			light_status &&
				this.setState({
					...this.state.light_status,
					light_status
				});
		});
	};

	handleShuffle = () => {
		shuffleLights();
	};

	updateConfig = () => {
		fetchConfig().then(config => {
			config && this.setState({ config });
		});
	};

	togglePhilipsHueDialog = (dialogOpen = !this.state.dialogOpen) => {
		this.setState({
			view: {
				...this.state.view,
				dialogOpen: dialogOpen
			}
		});
	};

	handleClosePhilipsHueDialog = () => {
		this.togglePhilipsHueDialog(false);
	};

	handleOpenPhilipsHueDialog = () => {
		this.togglePhilipsHueDialog(true);
	};

	renderControls = () => {
		const { classes } = this.props;
		return (
			<Card className={classes.card}>
				<CardHeader title="Setup" />
				<CardContent>
					<Button
						variant="raised"
						color="primary"
						size="medium"
						className={classes.button}
						onClick={this.loginWithSpotify}
					>
						Connect Spotify
					</Button>
					<Button
						variant="raised"
						color={"primary"}
						size="medium"
						className={classes.button}
						onClick={this.handleOpenPhilipsHueDialog}
					>
						Connect Hue Bridge
					</Button>
				</CardContent>
			</Card>
		);
	};

	renderSpotifyInfo = () => <SpotifyInfoCard status={this.state.spotify} />;

	renderLightInfo = () => {
		const { lights } = this.state.light_status;
		const { classes } = this.props;
		return (
			<Card className={classes.card}>
				<CardHeader title="Hue Lights" />
				<CardContent>
					<PhilipsHueInfoCard lights={lights} />
				</CardContent>
			</Card>
		);
	};

	render() {
		console.log("state", this.state);
		const { classes } = this.props;
		return (
			<div>
				<NavBar />
				<div className="content-wrapper">
					<div className="content-container">
						{this.renderControls()}
						<Card className={classes.card}>
							<CardHeader title="Actions" />
							<CardContent>
								<Button
									variant="raised"
									className={classes.button}
									color="primary"
									size="large"
									onClick={this.handleShuffle}
								>
									Shuffle Lights
								</Button>
								<Button
									variant="raised"
									color={this.state.config.lightMatching ? "secondary" : "primary"}
									size="large"
									className={classes.button}
									onClick={this.toggleLightMatching}
								>
									{this.state.config.lightMatching ? "Disable" : "Enable"} light matching
								</Button>
							</CardContent>
						</Card>
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

export default withStyles(styles)(App);
