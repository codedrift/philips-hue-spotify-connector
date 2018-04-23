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

	renderColors = (colors) => {
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

	renderSongInfo = (album, song, artists, artwork) => {
		return (
			<div className="columns">
				<div className="column">
					<img alt={album} src={artwork} className={"spotify-album-cover"}/>
				</div>
				<div className="column">
					<span className="tag is-white">Track</span>
					<h4 className="subtitle is-4">{song}</h4>
					<span className="tag is-white">Album</span>
					<h5 className="subtitle is-5">{album}</h5>
					<span className="tag is-white">Artist</span>
					<h5 className="subtitle is-5">{artists}</h5>
				</div>
			</div>
		);
	};

	render() {
		// return null;
		const {mainColor, artworkUrl, album, artists, song, colors, eligibleColors } = this.props.status;
		return (
			<div className="content">
				{this.renderSongInfo(album, song, artists, artworkUrl)}
				<hr/>
				<h4>Dominant Colors</h4>
				{this.renderColors(colors)}
				<hr/>
				<h4>Eligible Colors</h4>
				{this.renderColors(eligibleColors.map(color => color.color))}
				<hr/>
				<h4>{"Chosen color"}</h4>
				{mainColor ? this.renderColor(mainColor.hex) : this.renderColorNotMatched()}
			</div>
		);
	}
}

export default SpotifyInfoCard;
