# Testarea-Sistemelor-Software-2024
Prezentarea unui framework de testare unitară din Java pentru a testa funcționalitățile unui proiect. 

Ilustrarea strategiilor de generare de teste prezentate la curs (partiționare în clase de
echivalență, analiza valorilor de frontieră, acoperire la nivel de instrucțiune, decizie, condiție,
circuite independente, analiză raport creat de generatorul de mutanți, teste suplimentare pentru a
omorî 2 dintre mutanții neechivalenți rămași în viață).

# Prezentarea Proiectului

> Documentatia proiectului se regaseste in sectiunea [wiki](https://github.com/irinaenescu2002/Testarea-Sistemelor-Software-2024/wiki).

>Raportul despre folosirea unui tool de AI care ajută în timpul testării software poate fi accesat [aici](https://github.com/irinaenescu2002/Testarea-Sistemelor-Software-2024/wiki/03.-Raport-despre-folosirea-unui-tool-AI-%E2%80%90-chatGPT).

>Prezentarea poate fi accesata [aici](https://github.com/irinaenescu2002/Testarea-Sistemelor-Software-2024/blob/main/Testarea-Sistemelor-Software-2024-Prezentare.pdf).

# Demo 

https://github.com/irinaenescu2002/Testarea-Sistemelor-Software-2024/assets/69342294/e4da27d2-eda4-4b34-b4ad-a88b35849228


https://github.com/irinaenescu2002/Testarea-Sistemelor-Software-2024/assets/69342294/cae0c05a-856e-4a51-8539-506e8025bd42


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
