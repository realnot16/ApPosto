# ApPosto

ApPosto è un sistema smart per il monitoraggio e la gestione dei parcheggi nel centro cittadino. Permette agli utenti di visualizzare i posti disponibili, effettuare prenotazioni e gestire i pagamenti tramite una piattaforma digitale.

## Caratteristiche principali
- **Monitoraggio in tempo reale**: Sensori basati su Arduino per rilevare la disponibilità dei posti.
- **Prenotazioni smart**: Sistema di prenotazione con notifiche push.
- **Gestione wallet**: Ricarica e pagamento tramite API di PayPal.
- **Mappe interattive**: Visualizzazione e navigazione verso i posti disponibili.
- **Storico e preferiti**: Memorizzazione delle prenotazioni passate e gestione delle aree preferite.

## Architettura del sistema
### 1. Dispositivi IoT
- **Arduino** con sensori di prossimità per rilevare la presenza di veicoli.
- Comunicazione dei dati tramite rete verso il server centrale.

### 2. Backend
- **Firebase Cloud**: Gestione utenti e notifiche push.
- **Altervista DB**: Memorizzazione delle prenotazioni correnti e concluse.
- **API REST**: Comunicazione tra dispositivi, app mobile e database.

### 3. Frontend
- **App Mobile (Android)**:
  - **Login e Registrazione**: Autenticazione tramite Firebase.
  - **MapsActivity**: Core dell'app per visualizzare posti disponibili e gestire prenotazioni.
  - **Gestione Wallet**: Ricarica e visualizzazione del saldo tramite PayPal.

## Setup del progetto

### 1. Dispositivi IoT
- Configurare i sensori su Arduino utilizzando il codice in `Arduino/SensorsCode/`.
- Caricare il firmware sui dispositivi e collegarli alla rete.

### 2. Backend
- Configurare un database su Altervista e importare lo schema da `Backend/Database/Schema.sql`.
- Configurare Firebase Cloud per le notifiche push.
- Avviare il server API (`Backend/API/app.js`).

### 3. Frontend
- Clonare il repository e aprire il progetto Android Studio.
- Aggiornare i file di configurazione con le credenziali Firebase.
- Compilare e avviare l'app sul dispositivo.

## Contributori
- Benedetto Padula
- Flavia Costanza
- Giacomo Ciccarelli
- Eugenio Simone Errigo

## Nota
Questo progetto è stato sviluppato come parte di un corso universitario. Per eventuali dubbi o suggerimenti, contatta il responsabile del progetto tramite la sezione "Issues" su GitHub.
