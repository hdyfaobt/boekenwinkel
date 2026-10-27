// Catalogus: toont alle boeken met prijs en stock.

async function loadBooks() {
  try {
    const books = await api('/books');
    document.getElementById('books').innerHTML = books.map((book) => `
      <article class="card">
        <h3><a href="book.html?isbn=${book.isbn}">${esc(book.title)}</a></h3>
        <p class="muted">${esc(book.authors.join(', '))}</p>
        <p>${formatPrice(book.price)}</p>
        <p class="${book.stock > 0 ? 'ok' : 'error'}">
          ${book.stock > 0 ? book.stock + ' in stock' : 'Niet op voorraad'}
        </p>
      </article>`).join('');
  } catch (error) {
    document.getElementById('message').textContent = error.message;
  }
}

loadBooks();
