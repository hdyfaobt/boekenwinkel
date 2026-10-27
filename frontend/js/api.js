/**
 * Gedeelde functies voor alle pagina's: praten met de backend, login bijhouden
 * en de navigatiebalk tonen.
 */

/** Adres van de backend. Aanpassen als de backend ergens anders draait. */
const API_URL = 'http://localhost:8080/api';

/** @returns {string|null} het token van de ingelogde gebruiker */
function getToken() {
  return localStorage.getItem('token');
}

/** @returns {string|null} de gebruikersnaam van de ingelogde gebruiker */
function getUsername() {
  return localStorage.getItem('username');
}

/** Bewaart het token na het inloggen. */
function saveLogin(token, username) {
  localStorage.setItem('token', token);
  localStorage.setItem('username', username);
}

function logout() {
  localStorage.removeItem('token');
  localStorage.removeItem('username');
  location.href = 'index.html';
}

/**
 * Stuurt een request naar de backend en geeft de JSON terug.
 * Als de backend een fout geeft, wordt er een Error gegooid met de foutmelding.
 *
 * @param {string} path bv. '/books'
 * @param {string} [method] GET of POST
 * @param {object} [body] wordt als JSON verstuurd
 */
async function api(path, method = 'GET', body = undefined) {
  const headers = { 'Content-Type': 'application/json' };
  if (getToken()) {
    headers.Authorization = 'Bearer ' + getToken();
  }

  let response;
  try {
    response = await fetch(API_URL + path, {
      method,
      headers,
      body: body ? JSON.stringify(body) : undefined,
    });
  } catch {
    throw new Error('De backend is niet bereikbaar. Draait hij op poort 8080?');
  }

  const text = await response.text();
  const data = text ? JSON.parse(text) : null;
  if (!response.ok) {
    if (response.status === 401 && getToken()) {
      // token is niet meer geldig
      localStorage.removeItem('token');
    }
    throw new Error(data?.detail || 'Er ging iets mis (' + response.status + ')');
  }
  return data;
}

/** Zet tekst om zodat ze veilig in HTML getoond kan worden. */
function esc(text) {
  const div = document.createElement('div');
  div.textContent = text ?? '';
  return div.innerHTML;
}

function formatPrice(price) {
  return '€ ' + Number(price).toFixed(2).replace('.', ',');
}

function formatDate(date) {
  return new Date(date).toLocaleDateString('nl-BE');
}

/** Toont de navigatiebalk bovenaan elke pagina. */
function showNav() {
  const nav = document.getElementById('nav');
  if (getToken()) {
    nav.innerHTML = `
      <a href="index.html">Catalogus</a>
      <a href="profile.html">Mijn profiel (${esc(getUsername())})</a>
      <a href="#" id="logout">Uitloggen</a>`;
    document.getElementById('logout').onclick = (e) => { e.preventDefault(); logout(); };
  } else {
    nav.innerHTML = `
      <a href="index.html">Catalogus</a>
      <a href="login.html">Inloggen</a>
      <a href="register.html">Registreren</a>`;
  }
}

showNav();
