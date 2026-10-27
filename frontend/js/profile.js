// Profiel: reservaties met afhaaldatum en gekochte boeken met een reviewformulier.

if (!getToken()) {
  location.href = 'login.html';
}

async function loadProfile() {
  try {
    const profile = await api('/users/me');
    document.getElementById('info').textContent = profile.username + ' - ' + profile.email;

    document.getElementById('reservations').innerHTML = profile.reservations.length === 0
      ? '<p class="muted">Nog geen reservaties.</p>'
      : profile.reservations.map((r) => `
          <div class="card">
            <strong>Reservatie #${r.id}</strong> van ${formatDate(r.createdAt)}<br>
            Afhalen en betalen vanaf <strong>${formatDate(r.pickupDate)}</strong>
            <ul>${r.items.map((i) => `<li>${i.quantity} x ${esc(i.title)}</li>`).join('')}</ul>
          </div>`).join('');

    document.getElementById('purchased').innerHTML = profile.purchasedBooks.length === 0
      ? '<p class="muted">Je hebt nog geen boeken gekocht.</p>'
      : profile.purchasedBooks.map((book) => `
          <div class="card">
            <a href="book.html?isbn=${book.isbn}">${esc(book.title)}</a>
            <form class="review-form" data-isbn="${book.isbn}">
              <select name="score">
                <option value="5">5 - uitstekend</option>
                <option value="4">4 - goed</option>
                <option value="3">3 - oké</option>
                <option value="2">2 - matig</option>
                <option value="1">1 - slecht</option>
              </select>
              <input name="text" placeholder="Je review" required maxlength="2000">
              <button>Review plaatsen</button>
              <span class="message"></span>
            </form>
          </div>`).join('');

    document.querySelectorAll('.review-form').forEach((form) => {
      form.onsubmit = (event) => sendReview(event, form);
    });
  } catch (error) {
    document.getElementById('info').className = 'error';
    document.getElementById('info').textContent = error.message;
  }
}

async function sendReview(event, form) {
  event.preventDefault();
  const message = form.querySelector('.message');
  try {
    await api('/books/' + form.dataset.isbn + '/reviews', 'POST', {
      score: Number(form.score.value),
      text: form.text.value,
    });
    message.className = 'message ok';
    message.textContent = 'Bedankt voor je review!';
    form.querySelector('button').disabled = true;
  } catch (error) {
    message.className = 'message error';
    message.textContent = error.message;
  }
}

loadProfile();
