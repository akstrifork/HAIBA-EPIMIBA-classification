CREATE TABLE Tabmicroorganism (
	   TabmicroorganismId BIGINT NOT NULL PRIMARY KEY,
       Banr varchar(50),
       Text varchar(300)
);

CREATE TABLE TabLabSection (
	   TabLabSectionId BIGINT NOT NULL PRIMARY KEY,
       Avd varchar(50),
       Text varchar(300)
);

CREATE TABLE TabOrganization (
	   TabOrganizationId BIGINT NOT NULL PRIMARY KEY,
       Mgkod varchar(300),
       Text varchar(300)
);

CREATE TABLE TabAnalysis (
	   TabAnalysisId BIGINT NOT NULL PRIMARY KEY,
       Qtnr varchar(300),
       Text varchar(300)
);

CREATE TABLE TabInvestigation (
	   TabInvestigationId BIGINT NOT NULL PRIMARY KEY,
       Usnr varchar(50),
       Text varchar(300)
);

CREATE TABLE TabLocation (
	   TabLocationId BIGINT NOT NULL PRIMARY KEY,
       Alnr varchar(200),
       Text varchar(300)
);

CREATE TABLE EpimibaImporterStatus (
    Id BIGINT IDENTITY NOT NULL PRIMARY KEY,
    StartTime DATETIME NOT NULL,
    EndTime DATETIME,
    Outcome VARCHAR(20),
    ErrorMessage VARCHAR(200)
);

CREATE TABLE EpimibaTransaction (
	   EpimibaTransactionId BIGINT IDENTITY NOT NULL PRIMARY KEY,
       TransactionId BIGINT NOT NULL,
       TransactionProcessed DATETIME NOT NULL,
       TransactionType BIGINT NOT NULL
);

-- other database
CREATE TABLE Anvendt_Klass_microorganism (
    TabmicroorganismId BIGINT NOT NULL PRIMARY KEY,
    Banr varchar(50) NOT NULL,
    Text varchar(300) NULL,
    H_BAKT_MICRO float NULL
);

CREATE TABLE Anvendt_Klass_Location (
    TabLocationId BIGINT NOT NULL PRIMARY KEY,
    Alnr varchar(50) NOT NULL,
    Text varchar(300) NULL,
    H_SAAR_LOC float NULL
);