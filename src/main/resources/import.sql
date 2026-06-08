INSERT INTO role (id, nome) VALUES (1, 'ROLE_ADMIN');
INSERT INTO role (id, nome) VALUES (2, 'ROLE_USER');

INSERT INTO usuario (id, usuario, senha) VALUES (1, 'admin', '$2a$10$tbs4UMB6IouZDQjyy/lE/OArOO1iafF.oXLuAEMS5LIgi2Lxr17yu');
INSERT INTO usuario (id, usuario, senha) VALUES (2, 'thiago', '$2a$10$57O3OKPPf/cjGhhSmfrgUuj8ofq4UOT.KDYMNYIdF4QW5IwYZgpi6');

INSERT INTO usuario_roles (usuario_id, role_id) VALUES (1, 1);
INSERT INTO usuario_roles (usuario_id, role_id) VALUES (2, 2);

INSERT INTO produto (id, descricao, valor, imagem) VALUES (1, 'Notebook Dell', 4500.00, 'notebook_dell.jpg');
INSERT INTO produto (id, descricao, valor, imagem) VALUES (2, 'Mouse Logitech', 150.00, 'mouse_logitech.jpg');
INSERT INTO produto (id, descricao, valor, imagem) VALUES (3, 'Monitor LG 24 Pol', 900.00, 'monitorlg_24pol.jpg');

INSERT INTO pessoa (id, tipo_pessoa, nome, cpf, email, telefone, usuario_id) VALUES (1, 'PF', 'João Silva', '123.456.789-00', 'joao@email.com', '9999-9999', null);
INSERT INTO pessoa (id, tipo_pessoa, nome, cpf, email, telefone, usuario_id) VALUES (2, 'PF', 'Thiago Medeiros', '321.456.345-12', 'thiago@email.com', '1234-5678', 2);