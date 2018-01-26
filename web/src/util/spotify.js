import queryString from "query-string";

const SCOPES = 'user-read-private user-read-email user-read-playback-state';
const REDIRECT_URI = window.location.href;
const CLIENT_ID = process.env.REACT_APP_SPOTIFY_CLIENT_ID;

const generateRandomString = (length) => {
	let text = '';
	let possible = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
	for (let i = 0; i < length; i++) {
		text += possible.charAt(
			Math.floor(Math.random() * possible.length));
	}
	return text;
};

const buildLoginUrl = () => {
	const state = generateRandomString(16);
	let url = 'https://accounts.spotify.com/authorize';
	url += '?response_type=code';
	url += '&client_id=' + encodeURIComponent(CLIENT_ID);
	url += '&scope=' + encodeURIComponent(SCOPES);
	url += '&redirect_uri=' + encodeURIComponent(REDIRECT_URI);
	url += '&state=' + encodeURIComponent(state);
	return url;
};

const authenticateServer = ({code, state}) => {
	return fetch('/api/spotify/auth',
		{
			method: 'POST',
			body: JSON.stringify({
				code,
				state,
				redirectUri: REDIRECT_URI
			})
		})
};

export const fetchPlayStatus = () => {
	return fetch('/api/spotify/status', {mode: 'cors'})
		.then(res => res.json())
		.catch((e) => console.log('Error fetching spotify status', e))
};

export const authenticate = (onAuthenticated) => {
		const url = buildLoginUrl();
		const windowName = "Spotify Login";
		const positionFromTop = 200;
		const positionFromLeft = window.innerWidth / 2;
		const popUp = window.open(
			url,
			windowName,
			`height=600,width=400,left=${positionFromLeft},top=${positionFromTop}`
		);
		const tokenPoller = setInterval(() => {
			try {
				const query = queryString.parse(popUp.location.search);
				const code = query.code;
				if(code){
					popUp.close();
					authenticateServer(query).then((res) => {
						onAuthenticated(res);
						clearInterval(tokenPoller)
					});
				}
			} catch (e) {
				// intentionally left blank
			}
		}, 300)
	};