CREATE TABLE produto (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL
);

CREATE TABLE funcionario (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL
);

CREATE TABLE ponto (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL
);

CREATE TABLE estoque (
    produto_id INT REFERENCES produto(id),
    ponto_id INT REFERENCES ponto(id),
    quantidade INT DEFAULT 0,
    PRIMARY KEY (produto_id, ponto_id)
);

CREATE TABLE movimentacao (
    id SERIAL PRIMARY KEY,
    produto_id INT REFERENCES produto(id),
    ponto_id INT REFERENCES ponto(id),
    funcionario_id INT REFERENCES funcionario(id),
    quantidade INT NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    data_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE reposicao (
    id SERIAL PRIMARY KEY,
    produto_id INT REFERENCES produto(id),
    ponto_id INT REFERENCES ponto(id),
    funcionario_id INT REFERENCES funcionario(id),
    quantidade INT NOT NULL,
    data_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE conferencia (
    id SERIAL PRIMARY KEY,
    produto_id INT REFERENCES produto(id),
    ponto_id INT REFERENCES ponto(id),
    funcionario_id INT REFERENCES funcionario(id),
    estoque_inicial INT DEFAULT 0,
    reposicoes_recebidas INT DEFAULT 0,
    saidas_realizadas INT DEFAULT 0,
    estoque_esperado INT DEFAULT 0,
    estoque_fisico INT DEFAULT 0,
    divergencia INT DEFAULT 0,
    data TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);