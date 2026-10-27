// Detailpagina van een boek: info, reserveren en reviews.

const isbn = new URLSearchParams(location.search).get('isbn');

async function loadBook() {
  try {
    const detail = await api('/books/' + isbn);
    const book = detail.book;

    document.getElementById('book').innerHTML = `
      <h1>${esc(book.title)}</h1>
      <p class="muted">${esc(book.authors.join(', '))} &middot; ISBN ${book.isbn}</p>
      <p>${esc(book.description)}</p>
      <p><strong>${formatPrice(book.price)}</strong> &middot;
         ${book.stock > 0 ? book.stock + ' in stock' : 'niet op voorraad'}</p>
      <p>Gemiddelde score: ${detail.averageScore ? detail.averageScore.toFixed(1) + ' / 5' : 'nog geen reviews'}</p>`;

    showReserveForm(book);

    document.getElementById('reviews').innerHTML = detail.reviews.length === 0
      ? '<p class="muted">Nog geen reviews.</p>'
      : detail.reviews.map((review) => `
          <div class="card">
            <strong>${review.score} / 5</strong> door ${esc(review.username)}
            <span class="muted">(${formatDate(review.createdAt)})</span>
            <p>${esc(review.text)}</p>
          </div>`).join('');
  } catch (error) {
    document.getElementById('book').innerHTML = `<p class="error">${esc(error.message)}</p>`;
  }
}

function showReserveForm(book) {
  const section = document.getElementById('reserve');
  if (!getToken()) {
    section.innerHTML = '<p><a href="login.html">Log in</a> om dit boek te reserveren.</p>';
    return;
  }
  if (book.stock === 0) {
    section.innerHTML = '';
    return;
  }
  section.innerHTML = `
    <form id="reserve-form" class="inline">
      <label>Aantal <input type="number" name="quantity" min="1" max="${book.stock}" value="1"></label>
      <button>Reserveren</button>
    </form>
    <p id="reserve-message"></p>`;

  document.getElementById('reserve-form').onsubmit = async (event) => {
    event.preventDefault();
    const quantity = Number(event.target.quantity.value);
    const message = document.getElementById('reserve-message');
    try {
      const reservation = await api('/reservations', 'POST', { items: [{ isbn, quantity }] });
      await loadBook(); // stock opnieuw ophalen
      document.getElementById('reserve-message').className = 'ok';
      document.getElementById('reserve-message').textContent =
        'Gereserveerd! Je kan je boek(en) afhalen en betalen vanaf ' + formatDate(reservation.pickupDate) + '.';
    } catch (error) {
      message.className = 'error';
      message.textContent = error.message;
    }
  };
}

loadBook();
