// Inloggen: bij succes wordt het token bewaard in localStorage.

document.getElementById('form').onsubmit = async (event) => {
  event.preventDefault();
  const form = event.target;
  try {
    const result = await api('/users/login', 'POST', {
      username: form.username.value,
      password: form.password.value,
    });
    saveLogin(result.token, result.username);
    location.href = 'index.html';
  } catch (error) {
    document.getElementById('message').className = 'error';
    document.getElementById('message').textContent = error.message;
  }
};
