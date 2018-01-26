import React, {Component} from 'react';

class PhilipsHueInfoCard extends Component {

	renderLight = ({ name, bri, hex}) => {
		const style = {
			backgroundColor: hex,
			height: 50,
		};
		return (
			<div key={name} className="column">
				<h6 className="title is-6">{name}</h6>
				<div className={"color-display-item"} style={style}>
					<div className="level">
						<div className="level-left">
							<div className="level-item">
								<span className={"tag"}>{hex}</span>
							</div>
						</div>
					</div>
				</div>
				<span>{`Brightness ${Math.floor(100 * (bri / 255))}%`}</span>
			</div>
		)
	};

	render() {
		const lights = this.props.lights
			.map(({name, state: {hex, bri}}) => ({name, hex, bri}));
		return (
			<div>
				<div className="columns">
					{lights.map(this.renderLight)}
				</div>
			</div>
		);
	}
}

export default PhilipsHueInfoCard;
