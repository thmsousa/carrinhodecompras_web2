-- Produtos
INSERT INTO produto (descricao, valor, imagem) VALUES ('Notebook Dell', 4500.00, 'notebook_dell.jpg');
INSERT INTO produto (descricao, valor, imagem) VALUES ('Mouse Logitech', 150.00, 'mouse_logitech.jpg');
INSERT INTO produto (descricao, valor, imagem) VALUES ('Monitor LG 24 Pol', 900.00, 'monitorlg_24pol.jpg');

-- CLIENTES
INSERT INTO pessoa (tipo_pessoa, nome, cpf, email, telefone) VALUES ('PF', 'João Silva', '123.456.789-00', 'joao@email.com', '9999-9999');
INSERT INTO pessoa (tipo_pessoa, nome, cpf, email, telefone) VALUES ('PF', 'Thiago Medeiros', '321.456.345-12', 'thiago@email.com', '1234-5678');
INSERT INTO pessoa (tipo_pessoa, razao_social, cnpj, email, telefone) VALUES ('PJ', 'Tech Solutions LTDA', '00.123.456/0001-99', 'contato@tech.com', '8888-8888');

-- Vendas associadas aos Clientes (pessoa_id)
INSERT INTO venda (data, pessoa_id) VALUES (CURRENT_TIMESTAMP, 1);
INSERT INTO venda (data, pessoa_id) VALUES (CURRENT_TIMESTAMP, 2);
INSERT INTO venda (data, pessoa_id) VALUES (CURRENT_TIMESTAMP, 1);
INSERT INTO venda (data, pessoa_id) VALUES (CURRENT_TIMESTAMP, 2);
INSERT INTO venda (data, pessoa_id) VALUES (CURRENT_TIMESTAMP, 1);
INSERT INTO venda (data, pessoa_id) VALUES (CURRENT_TIMESTAMP, 1);
INSERT INTO venda (data, pessoa_id) VALUES (CURRENT_TIMESTAMP, 3);


-- ITENS
INSERT INTO item (quantidade, produto_id, venda_id) VALUES (1, 1, 1);

INSERT INTO item (quantidade, produto_id, venda_id) VALUES (1, 2, 2);

INSERT INTO item (quantidade, produto_id, venda_id) VALUES (3, 1, 3);

INSERT INTO item (quantidade, produto_id, venda_id) VALUES (3, 2, 4);
INSERT INTO item (quantidade, produto_id, venda_id) VALUES (3, 2, 4);

INSERT INTO item (quantidade, produto_id, venda_id) VALUES (2.0, 1, 5);
INSERT INTO item (quantidade, produto_id, venda_id) VALUES (1.0, 2, 5);

INSERT INTO item (quantidade, produto_id, venda_id) VALUES (2.0, 3, 6);

INSERT INTO item (quantidade, produto_id, venda_id) VALUES (3.0, 3, 7);
INSERT INTO item (quantidade, produto_id, venda_id) VALUES (1.0, 2, 7);

