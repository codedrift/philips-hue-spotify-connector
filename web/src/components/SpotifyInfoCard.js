import React, { Component } from "react";
import { emojify } from "react-emojione";
import { withStyles } from "material-ui/styles";
import ExpansionPanel, {
	ExpansionPanelSummary,
	ExpansionPanelDetails
} from "material-ui/ExpansionPanel";
import Typography from "material-ui/Typography";
import Card, { CardContent, CardMedia } from "material-ui/Card";
import ExpandMoreIcon from "material-ui-icons/ExpandMore";

const styles = theme => ({
	root: {
		flexGrow: 1,
		marginTop: 20
	},
	heading: {
		fontSize: theme.typography.pxToRem(15),
		fontWeight: theme.typography.fontWeightRegular
	},
	expansionPanel: {
		marginTop: 10
	},
	card: {},
	media: {
		height: 0,
		paddingTop: "56.25%" // 16:9
	}
});

class SpotifyInfoCard extends Component {
	renderColor = color => (
		<div
			className={"color-display-item"}
			key={`color_${Math.random()}`}
			style={{ backgroundColor: color }}
		>
			<div className="level">
				<div className="level-left">
					<div className="level-item">
						<span className={"tag"}>{color.toUpperCase()}</span>
					</div>
				</div>
			</div>
		</div>
	);

	renderColors = colors => {
		return colors.length ? (
			<div className={"color-display-container"}>
				{colors.map(color => this.renderColor(color.hex))}
			</div>
		) : (
			<div />
		);
	};

	renderColorNotMatched = () => (
		<div className={"color-display-item"}>
			<div className="level">
				<div className="level-left">
					<div className="level-item">
						<span>{emojify("No color was good enough 😭")}</span>
					</div>
				</div>
			</div>
		</div>
	);

	renderSongInfo = (album, song, artists, artwork) => {
		const { classes } = this.props;
		return (
			<Card className={classes.card}>
				<CardMedia className={classes.media} image={artwork} title={song} />
				<CardContent>
					<h4 className="subtitle is-4">{song}</h4>
					<h5 className="subtitle is-5">{album}</h5>
					<h5 className="subtitle is-5">{artists}</h5>
				</CardContent>
			</Card>
		);
	};

	render() {
		const {
			artworkUrl,
			album,
			artists,
			song,
			colors,
			eligibleColors,
			lightColors
		} = this.props.status;
		const { classes } = this.props;
		return (
			<div className={classes.root}>
				{this.renderSongInfo(album, song, artists, artworkUrl)}
				<ExpansionPanel className={classes.expansionPanel}>
					<ExpansionPanelSummary expandIcon={<ExpandMoreIcon />}>
						<Typography className={classes.heading}>Dominant Colors</Typography>
					</ExpansionPanelSummary>
					<ExpansionPanelDetails>{this.renderColors(colors)}</ExpansionPanelDetails>
				</ExpansionPanel>
				<ExpansionPanel defaultExpanded={false}>
					<ExpansionPanelSummary expandIcon={<ExpandMoreIcon />}>
						<Typography className={classes.heading}>Eligible Colors</Typography>
					</ExpansionPanelSummary>
					<ExpansionPanelDetails>
						{this.renderColors(eligibleColors.map(color => color.color))}
					</ExpansionPanelDetails>
				</ExpansionPanel>
				<ExpansionPanel defaultExpanded>
					<ExpansionPanelSummary expandIcon={<ExpandMoreIcon />}>
						<Typography className={classes.heading}>Light Colors</Typography>
					</ExpansionPanelSummary>
					<ExpansionPanelDetails>
						{lightColors.length > 0 ? this.renderColors(lightColors) : this.renderColorNotMatched()}
					</ExpansionPanelDetails>
				</ExpansionPanel>
			</div>
		);
	}
}

export default withStyles(styles)(SpotifyInfoCard);
