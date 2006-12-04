CREATE TABLE mitglied 
(
  id            INTEGER     default UNIQUEKEY('mitglied'), 
  anrede        VARCHAR(10),
  titel         VARCHAR(10),
  name          VARCHAR(40) NOT NULL, 
  vorname       VARCHAR(40) NOT NULL, 
  strasse       VARCHAR(40) NOT NULL, 
  plz           VARCHAR(5)  NOT NULL, 
  ort           VARCHAR(40) NOT NULL, 
  blz           VARCHAR(8),
  konto         VARCHAR(10),
  kontoinhaber  VARCHAR(27),
  geburtsdatum  DATE,
  geschlecht    CHAR(1),
  telefonprivat VARCHAR(15),
  telefondienstlich VARCHAR(15),
  email         VARCHAR(50),
  eintritt      DATE,
  beitragsgruppe INTEGER,
  austritt      DATE,
  kuendigung    DATE,
  eingabedatum  DATE,
  UNIQUE        (id), 
  PRIMARY KEY   (id)
);

CREATE TABLE beitragsgruppe
(
  id            INTEGER       default UNIQUEKEY('beitragsgruppe'),
  bezeichnung   VARCHAR(30) NOT NULL,
  betrag		DOUBLE,
  UNIQUE        (id),
  PRIMARY KEY   (id)
);

ALTER TABLE mitglied ADD CONSTRAINT fkMitglied1 FOREIGN KEY (beitragsgruppe) REFERENCES beitragsgruppe (id) DEFERRABLE;

CREATE TABLE zusatzabbuchung
(
  id            INTEGER default UNIQUEKEY('zusatzabbuchung'),
  mitglied      INTEGER NOT NULL,
  faelligkeit   DATE NOT NULL,
  buchungstext  VARCHAR(27) NOT NULL,
  betrag        DOUBLE NOT NULL,
  ausfuehrung   DATE,
  UNIQUE        (id),
  PRIMARY KEY   (id)
);

ALTER TABLE zusatzabbuchung ADD CONSTRAINT fkZusatzabbuchung1 FOREIGN KEY (mitglied) REFERENCES mitglied (id) DEFERRABLE;

CREATE TABLE stammdaten
(
  id			INTEGER		default UNIQUEKEY('stammdaten'),
  name			VARCHAR(30) NOT NULL,
  blz			VARCHAR(8)  NOT NULL,
  konto         VARCHAR(10) NOT NULL,
  altersgruppen VARCHAR(50),
  UNIQUE        (id),
  PRIMARY KEY   (id)
);

CREATE TABLE buchung
(
  id            INTEGER      default UNIQUEKEY('buchungen'),
  umsatzid      STRING       NOT NULL,
  konto         VARCHAR(10)  NOT NULL,
  name          VARCHAR(100),
  betrag        DOUBLE       NOT NULL,
  zweck         VARCHAR(35),
  zweck2        VARCHAR(35),
  datum         DATE         NOT NULL,
  saldo         DOUBLE       NOT NULL,
  art           VARCHAR(100),
  kommentar     LONGVARCHAR,
  buchungsart   INTEGER,
  UNIQUE        (id),
  PRIMARY KEY   (id)
);  

CREATE TABLE buchungsart
(
  id            INTEGER       default UNIQUEKEY('buchungsart'),
  nummer        INTEGER,
  bezeichnung   VARCHAR(30),
  UNIQUE        (id),
  UNIQUE        (nummer),
  PRIMARY KEY   (id)
);

ALTER TABLE buchung ADD CONSTRAINT fkBuchung1 FOREIGN KEY (buchungsart) REFERENCES buchungsart (id) DEFERRABLE;

COMMIT; 

