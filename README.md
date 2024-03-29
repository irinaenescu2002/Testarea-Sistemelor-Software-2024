# Testarea-Sistemelor-Software-2024
Prezentarea unui framework de testare unitară din Java pentru a testa funcționalitățile unui proiect. 

Ilustrarea strategiilor de generare de teste prezentate la curs (partiționare în clase de
echivalență, analiza valorilor de frontieră, acoperire la nivel de instrucțiune, decizie, condiție,
circuite independente, analiză raport creat de generatorul de mutanți, teste suplimentare pentru a
omorî 2 dintre mutanții neechivalenți rămași în viață).

# Running

**Requirements:** Docker, Java 21, JWT key pair, OpenAI key  

**Initial setup:**

- Crearea unui fisier `.env` configurat cu secrets si API keys:

```sh
POSTGRES_DB=example_db
POSTGRES_USER=example_user
POSTGRES_PASSWORD=example_password
OPENAI_KEY=your_openai_key
```

- Generarea unei perechi de chei pentru JWT authentication:

```sh
openssl genrsa -out src/main/resources/jwt.key 4096
openssl rsa -in src/main/resources/jwt.key -pubout -outform PEM -out src/main/resources/jwt.pub
```

- Start baza de date:

```sh
docker-compose up
```

- Start Spring server:

```sh
./gradlew bootRun
```
