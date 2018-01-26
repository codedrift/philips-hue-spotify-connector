import React from 'react';
import ReactDOM from 'react-dom';
import './style/style.css';
import App from './components/App';
import 'bulma/css/bulma.css';
import registerServiceWorker from './util/registerServiceWorker';

ReactDOM.render(<App />, document.getElementById('root'));
registerServiceWorker();
