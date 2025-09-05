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
    email VARCHAR(150) NOT NULL,
    id_state INT NOT NULL,
    id_loan_type INT NOT NULL,
    FOREIGN KEY (id_state) REFERENCES states(id_state),
    FOREIGN KEY (id_loan_type) REFERENCES loan_type(id_loan_type)
);




INSERT INTO states (name, description)
VALUES
    ('Pendiente', 'La solicitud ha sido registrada pero aún no procesada'),
    ('En revisión', 'La solicitud está en proceso de validación'),
    ('Aprobada', 'La solicitud fue aprobada satisfactoriamente'),
    ('Rechazada', 'La solicitud no cumplió con los requisitos'),
    ('Cancelada', 'El solicitante canceló la solicitud antes de ser procesada');


INSERT INTO loan_type (name, min_amount, max_amount, interest_rate, automatic_validation)
VALUES
    ('Préstamo Personal', 1500000, 20000000, 18.50, TRUE),
    ('Préstamo Hipotecario', 30000000, 500000000, 12.75, FALSE),
    ('Préstamo Vehicular', 10000000, 120000000, 14.20, FALSE),
    ('Crédito de Consumo', 1500000, 10000000, 22.00, TRUE),
    ('Préstamo Empresarial', 20000000, 400000000, 13.50, FALSE);