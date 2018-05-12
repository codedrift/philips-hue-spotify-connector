import React, { Component } from "react";
import NavLogo from "../images/spotifyxhuelogo.png";

class NavBar extends Component {
	render() {
		return (
			<div>
				<nav className="navbar" aria-label="main navigation">
					<div className="navbar-brand">
						<a className="navbar-item" href="/">
							<img src={NavLogo} alt="Connect Spotify and Philips Hue" width="112" height="28" />
						</a>
					</div>
				</nav>
			</div>
		);
	}
}

export default NavBar;
