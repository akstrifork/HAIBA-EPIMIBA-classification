CREATE DATABASE IF NOT EXISTS HAIBA;
USE HAIBA;

CREATE TABLE IF NOT EXISTS Class_TabMicroorganism (
	   TabmicroorganismId BIGINT(15) NOT NULL PRIMARY KEY,
       Banr varchar(50),
       Text varchar(300)
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS Class_TabLabSection (
	   TabLabSectionId BIGINT(15) NOT NULL PRIMARY KEY,
       Avd varchar(50),
       Text varchar(300)
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS Class_TabOrganization (
	   TabOrganizationId BIGINT(15) NOT NULL PRIMARY KEY,
       Mgkod varchar(300),
       Text varchar(300)
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS Class_TabAnalysis (
	   TabAnalysisId BIGINT(15) NOT NULL PRIMARY KEY,
       Qtnr varchar(300),
       Text varchar(300)
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS Class_TabInvestigation (
	   TabInvestigationId BIGINT(15) NOT NULL PRIMARY KEY,
       Usnr varchar(50),
       Text varchar(300)
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS Class_TabLocation (
	   TabLocationId BIGINT(15) NOT NULL PRIMARY KEY,
       Alnr varchar(200),
       Text varchar(300)
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS EpimibaImporterStatus (
    Id BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
    StartTime DATETIME NOT NULL,
    EndTime DATETIME,
    Outcome VARCHAR(20),
    ErrorMessage VARCHAR(200),

    INDEX (StartTime)
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS EpimibaTransaction (
	   EpimibaTransactionId BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
       TransactionId BIGINT(15) NOT NULL,
       TransactionProcessed DATETIME NOT NULL,
       TransactionType BIGINT(15) NOT NULL
) ENGINE=InnoDB COLLATE=utf8_bin;

-- other database
CREATE TABLE IF NOT EXISTS Class_dynamic_microorganism (
    TabmicroorganismId BIGINT(15) NOT NULL PRIMARY KEY,
    Banr varchar(50) NOT NULL,
    Text varchar(300) NULL,
    H_BAKT_MICRO float NULL
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS Class_dynamic_location (
    TabLocationId BIGINT(15) NOT NULL PRIMARY KEY,
    Alnr varchar(200) NOT NULL,
    Text varchar(300) NULL,
    H_SAAR_LOC float NULL
) ENGINE=InnoDB COLLATE=utf8_bin;