
CREATE DATABASE IF NOT EXISTS sistema_clinica;

USE sistema_clinica;

-- Tabela de médicos
CREATE TABLE IF NOT EXISTS medicos (
                                       id INT AUTO_INCREMENT PRIMARY KEY,
                                       nome VARCHAR(100) NOT NULL,
    usuario VARCHAR(50) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    especialidade VARCHAR(100) NOT NULL,
    consultorio INT NOT NULL,
    UNIQUE (especialidade, consultorio)
    );

-- Tabela de pacientes
CREATE TABLE IF NOT EXISTS pacientes (
                                         id INT AUTO_INCREMENT PRIMARY KEY,
                                         nome VARCHAR(100) NOT NULL,
    idade INT NOT NULL,
    possui_deficiencia BOOLEAN NOT NULL DEFAULT FALSE,
    gestante BOOLEAN NOT NULL DEFAULT FALSE,
    possui_obesidade BOOLEAN NOT NULL DEFAULT FALSE,
    crianca_de_colo BOOLEAN NOT NULL DEFAULT FALSE,
    especialidade VARCHAR(100) NOT NULL,
    senha VARCHAR(10) NOT NULL
    );

-- Tabela de senhas
CREATE TABLE IF NOT EXISTS senhas (
                                      id INT AUTO_INCREMENT PRIMARY KEY,
                                      paciente_id INT NOT NULL,
                                      medico_id INT NULL,
                                      numero INT NOT NULL,
                                      tipo VARCHAR(5) NOT NULL,
    prioridade VARCHAR(30) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'AGUARDANDO',
    especialidade VARCHAR(100) NOT NULL,
    data_emissao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_chamada DATETIME NULL,

    CONSTRAINT fk_senhas_pacientes
    FOREIGN KEY (paciente_id)
    REFERENCES pacientes(id),

    CONSTRAINT fk_senhas_medicos
    FOREIGN KEY (medico_id)
    REFERENCES medicos(id)
    ON DELETE SET NULL
    );

-- Tabela de atendimentos
CREATE TABLE IF NOT EXISTS atendimentos (
                                            id INT AUTO_INCREMENT PRIMARY KEY,
                                            senha_id INT NOT NULL UNIQUE,
                                            medico_id INT NOT NULL,
                                            data_inicio DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                            data_fim DATETIME NULL,
                                            status VARCHAR(30) NOT NULL DEFAULT 'EM_ATENDIMENTO',

    CONSTRAINT fk_atendimentos_senhas
    FOREIGN KEY (senha_id)
    REFERENCES senhas(id),

    CONSTRAINT fk_atendimentos_medicos
    FOREIGN KEY (medico_id)
    REFERENCES medicos(id)
    );

-- Índices para facilitar consultas
CREATE INDEX idx_senhas_status
    ON senhas(status);

CREATE INDEX idx_senhas_especialidade
    ON senhas(especialidade);

CREATE INDEX idx_atendimentos_medico
    ON atendimentos(medico_id);
