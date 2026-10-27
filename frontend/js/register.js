// Registreren: na een geslaagde registratie gaan we naar de loginpagina.

document.getElementById('form').onsubmit = async (event) => {
  event.preventDefault();
  const form = event.target;
  try {
    await api('/users/register', 'POST', {
      username: form.username.value,
      email: form.email.value,
      password: form.password.value,
    });
    alert('Account aangemaakt, je kan nu inloggen.');
    location.href = 'login.html';
  } catch (error) {
    document.getElementById('message').className = 'error';
    document.getElementById('message').textContent = error.message;
  }
};
