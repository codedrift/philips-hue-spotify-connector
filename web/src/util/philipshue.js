export const fetchLightStatus = () => {
	return fetch('/api/philipshue/status', {mode: 'cors'})
		.then(res => res.json())
		.catch((e) => console.log('Error fetching light status', e))
};

export const setLightMatching = (enabled) => {
	return fetch('/api/philipshue/matching',{
		method: 'POST',
		body: JSON.stringify({ enabled })
	})
		.catch((e) => console.log('Error setting light matching', e))
};

export const shuffleLights = () => {
	return fetch('/api/philipshue/shuffle',{
		method: 'POST'
	})
		.catch((e) => console.log('Error shuffeling lights matching', e))
};

export const fetchConfig = () => {
	return fetch('/api/config',{
	})
		.then(res => res.json())
		.catch((e) => console.log('Error fetching config', e))
};

export const createClient = (clientName, bridgeIp) => {
	return fetch('/api/philipshue/client',
		{
			method: 'POST',
			body: JSON.stringify({
				clientName,
				bridgeIp
			})
		})
		.then(res => res.json())
		.catch((e) => console.log('Error creating api key', e))
};