import React, {Component} from 'react';
import {emojify} from 'react-emojione';

class SpotifyInfoCard extends Component {

	renderColor = (color) => (
		<div className={"color-display-item"} key={color} style={{backgroundColor: color}}>
			<div className="level">
				<div className="level-left">
					<div className="level-item">
						<span className={"tag"}>{color.toUpperCase()}</span>
					</div>
				</div>
			</div>
		</div>
	);

	renderColors = () => {
		const colors = this.props.status.colors;
		return colors.length ? (
			<div className={"color-display-container"}>
				{colors.map(color => this.renderColor(color.hex))}
			</div>
		) : null;
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

	renderSongInfo = (album, trackName, artwork) => {
		const {name, artists} = album;
		const artistsString = artists.map((artist) => artist.name).join(", ");
		return (
			<div className="columns">
				<div className="column">
					<img alt={name} src={artwork} className={"spotify-album-cover"}/>
				</div>
				<div className="column">
					<span className="tag is-white">Track</span>
					<h4 className="subtitle is-4">{trackName}</h4>
					<span className="tag is-white">Album</span>
					<h5 className="subtitle is-5">{name}</h5>
					<span className="tag is-white">Artist</span>
					<h5 className="subtitle is-5">{artistsString}</h5>
				</div>
			</div>
		);
	};

	render() {
		// return null;
		const {hue_color, artwork, status: {item: {album, name}}} = this.props.status;
		return (
			<div className="content">
				{this.renderSongInfo(album, name, artwork)}
				<hr/>
				<h4>Dominant Colors</h4>
				{this.renderColors()}
				<hr/>
				<h4>{"Chosen color"}</h4>
				{hue_color ? this.renderColor(hue_color) : this.renderColorNotMatched()}
			</div>
		);
	}
}

export default SpotifyInfoCard;
