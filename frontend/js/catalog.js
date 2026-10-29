// Catalogus: toont alle boeken met prijs en stock, met een zoekbalk.

let allBooks = [];

async function loadBooks() {
  try {
    allBooks = await api('/books');
    showBooks(allBooks);
  } catch (error) {
    document.getElementById('message').textContent = error.message;
  }
}

/** Toont de gegeven boeken als kaarten in de grid. */
function showBooks(books) {
  document.getElementById('message').textContent = books.length === 0 ? 'Geen boeken gevonden.' : '';
  document.getElementById('books').innerHTML = books.map((book) => `
    <article class="card">
      <h3><a href="book.html?isbn=${book.isbn}">${esc(book.title)}</a></h3>
      <p class="muted">${esc(book.authors.join(', '))}</p>
      <p>${formatPrice(book.price)}</p>
      <p class="${book.stock > 0 ? 'ok' : 'error'}">
        ${book.stock > 0 ? book.stock + ' in stock' : 'Niet op voorraad'}
      </p>
    </article>`).join('');
}

/** Filtert de boeken op titel, auteur of ISBN (hoofdletters maken niet uit). */
function searchBooks() {
  const term = document.getElementById('search').value.trim().toLowerCase();
  const result = allBooks.filter((book) =>
    book.title.toLowerCase().includes(term)
    || book.authors.join(' ').toLowerCase().includes(term)
    || book.isbn.includes(term));
  showBooks(result);
}

document.getElementById('search').addEventListener('input', searchBooks);

loadBooks();
