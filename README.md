# Boekenwinkel

Project voor het vak Application Architecture (EhB). Een webapplicatie voor een fictieve boekenwinkel:
een catalogus met boeken en stock, gebruikers die zich registreren en inloggen, boeken reserveren
en reviews schrijven over boeken die ze gekocht hebben.

## Structuur

Het project bestaat uit twee aparte deelprojecten:

```
boekenwinkel
backend    Spring Boot REST API (Java 17, Maven, JPA, H2)
frontend   HTML, CSS en JavaScript, praat met de backend via fetch
```

```
frontend (browser)  HTTP/JSON -->  backend (Spring Boot :8080)  JPA -->  H2-databank
```

De backend is opgedeeld in lagen:

- `controller`: de REST-endpoints, nemen requests aan en geven DTO's terug
- `service`: de logica (stock aanpassen, reservaties, reviews, login)
- `repository`: toegang tot de databank via Spring Data JPA (DAO-pattern)
- `model`: de JPA-entiteiten `Book`, `User`, `Reservation`, `ReservationItem`, `Review`
- `dto`: de objecten die over de API gaan, zodat de entiteiten niet rechtstreeks naar buiten gaan

Spring injecteert de repositories in de services en de services in de controllers via de constructor
(dependency injection / inversion of control).

## Opstarten

### Backend

Vereist: JDK 17 of hoger.

```bash
cd backend
./mvnw spring-boot:run
```

Of in IntelliJ: open de map `backend` en run `BoekenwinkelApplication`.
De API draait op http://localhost:8080. Bij de eerste start worden er 9 boeken in de databank gezet.
De data staat in `backend/data/` (H2-bestand), dus ze blijft bewaard na een herstart.

### Frontend

De frontend moet via een webserver geopend worden (niet als `file://`), anders blokkeert de browser
de requests. Twee mogelijkheden:

- In IntelliJ: rechtsklik op `frontend/index.html` > Open In > Browser
- Of in een terminal:
  ```bash
  cd frontend
  python3 -m http.server 5500
  ```
  en surf naar http://localhost:5500

De backend moet draaien. Het adres van de backend staat bovenaan in `frontend/js/api.js`.

## Endpoints

Beveiligde endpoints verwachten de header `Authorization: Bearer`. Het token krijg je bij het inloggen.
Bij een fout geeft de API een JSON-object terug met de foutmelding in het veld `detail`.

| Methode | URL                         | Login | Wat                                                                                                  |
|---------|-----------------------------|-------|------------------------------------------------------------------------------------------------------|
| GET     | `/api/books`                | nee   | Alle boeken met prijs en stock                                                                       |
| GET     | `/api/books/{isbn}`         | nee   | Eén boek met zijn reviews en gemiddelde score                                                        |
| POST    | `/api/books/{isbn}/stock`   | nee   | Stock aanvullen bij een levering. Body: `{"quantity": 5}`                                            |
| POST    | `/api/books/{isbn}/reviews` | ja    | Review schrijven. Body: `{"score": 4, "text": "..."}`. Enkel voor gekochte boeken, één keer per boek |
| POST    | `/api/users/register`       | nee   | Account maken. Body: `{"username", "email", "password"}`                                             |
| POST    | `/api/users/login`          | nee   | Inloggen. Body: `{"username", "password"}`. Geeft `{"token", "username"}` terug                      |
| GET     | `/api/users/me`             | ja    | Profiel met reservaties en gekochte boeken                                                           |
| POST    | `/api/reservations`         | ja    | Reserveren. Body: `{"items": [{"isbn": "...", "quantity": 1}]}`. Geeft de afhaaldatum terug          |

Belangrijkste statuscodes:
`400` ongeldige invoer, 
`401` niet ingelogd of fout wachtwoord,
`403` review op een boek dat je niet gekocht hebt, 
`404` boek bestaat niet, 
`409` te weinig stock of al bestaand.

Hoe het reserveren werkt: de stock gaat meteen omlaag in de catalogus en je krijgt een datum
(1 dag later) vanaf wanneer je de boeken mag afhalen en betalen. Volgens de opgave gaan we ervan uit
dat alles wat gereserveerd is ook afgehaald wordt, dus een gereserveerd boek komt bij je gekochte boeken.

## Testen

```bash
cd backend
./mvnw test
```

| Testklasse               | Soort                      | Wat wordt getest                                                                                                                                                                                                            |
|--------------------------|----------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `BookTest`               | unit                       | Stock verlagen en verhogen, laatste exemplaar, meer dan de stock, ongeldige aantallen                                                                                                                                       |
| `ReservationServiceTest` | unit (Mockito)             | Reservatie verlaagt de stock en geeft een afhaaldatum, te weinig stock geeft ERROR 409 en er wordt niets opgeslagen, onbestaand boek geeft ERROR 404                                                                        |
| `BoekenwinkelApiTest`    | functioneel (MockMvc + H2) | Na een reservatie toont de catalogus minder stock en staat het boek in het profiel, review verschijnt op de detailpagina, review zonder aankoop wordt geweigerd, reserveren zonder login en fout wachtwoord geven ERROR 401 |

## Planning

| Week | Taak                                                                                              |
|------|---------------------------------------------------------------------------------------------------|
| 2-3  | Opgave lezen, datamodel en endpoints uittekenen, Maven-project opzetten                           |
| 3    | Backend: entiteiten, repositories, services en controllers voor boeken, gebruikers en reservaties |
| 3    | Backend: reviews en tests. Frontend: catalogus, detailpagina, login en registratie                |
| 4    | Frontend: profiel en reviews. Alles samen testen, README schrijven, opkuisen                      |
| 5    | Voorbereiden mondelinge verdediging                                                               |

## Bronnen en gebruik van AI-tools

**AI-tools**

| Tool                                                           | Waarvoor gebruikt                                                                                                                                                     |
|----------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Claude (Anthropic), via claude.ai, op 5, 25 en 27 oktober 2026 | Gebruikt voor de projectstructuur, voor het debuggen en herschrijven van code die niet correct werkte, om de applicatie te testen en voor de structuur van de README. |

Ik heb de code nagelezen en getest, en kan uitleggen hoe alles werkt.

**Andere bronnen**

- Cursusmateriaal Application Architecture (EhB): slides over Maven, ORM, REST met Spring en testing
- Spring Boot documentatie: https://docs.spring.io/spring-boot/
- Spring Data JPA documentatie: https://docs.spring.io/spring-data/jpa/reference/
- JUnit en Mockito: https://docs.junit.org/ en https://site.mockito.org/
- MDN Web Docs (fetch, localStorage): https://developer.mozilla.org/
- Enkele Youtube-tutorials over Spring Boot en REST APIs
