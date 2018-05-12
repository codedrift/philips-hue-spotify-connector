import React, { Component } from "react";

class Card extends Component {
	render() {
		return (
			<div className="card content-card">
				<header className="card-header">
					<p className="card-header-title">{this.props.title}</p>
				</header>
				<div className="card-content">{this.props.children}</div>
			</div>
		);
	}
}

export default Card;
