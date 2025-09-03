CREATE DATABASE request;

-- Table: states
CREATE TABLE states (
    id_state SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255)
);

-- Table: loan_type
CREATE TABLE loan_type (
    id_loan_type SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    min_amount NUMERIC(15,2) NOT NULL,
    max_amount NUMERIC(15,2) NOT NULL,
    interest_rate NUMERIC(5,2) NOT NULL,
    automatic_validation BOOLEAN NOT NULL
);

-- Table: application
CREATE TABLE application (
    id_application SERIAL PRIMARY KEY,
    amount NUMERIC(15,2) NOT NULL,
    term INT NOT NULL,
    document_number varchar(50) NOT NULL,
    -- email VARCHAR(150) NOT NULL,
    id_state INT NOT NULL,
    id_loan_type INT NOT NULL,
    FOREIGN KEY (id_state) REFERENCES states(id_state),
    FOREIGN KEY (id_loan_type) REFERENCES loan_type(id_loan_type)
);
