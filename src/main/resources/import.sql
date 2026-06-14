-- 1. CONTROLE DE ACESSO (ROLES E USUÁRIOS)

INSERT INTO role (nome) VALUES ('ROLE_ADMIN');
INSERT INTO role (nome) VALUES ('ROLE_USER');

-- admin / senha: admin
INSERT INTO usuario (usuario, senha) VALUES ('admin', '$2a$10$tbs4UMB6IouZDQjyy/lE/OArOO1iafF.oXLuAEMS5LIgi2Lxr17yu');
-- thiago / senha: 123
INSERT INTO usuario (usuario, senha) VALUES ('thiago', '$2a$10$57O3OKPPf/cjGhhSmfrgUuj8ofq4UOT.KDYMNYIdF4QW5IwYZgpi6');

-- Tabela associativa muitos-para-muitos (Cruza os IDs gerados de forma sequencial na inicialização)
INSERT INTO usuario_roles (usuario_id, role_id) VALUES (1, 1); -- admin -> ROLE_ADMIN
INSERT INTO usuario_roles (usuario_id, role_id) VALUES (2, 2); -- thiago -> ROLE_USER

-- 2. CADASTRO DE PRODUTOS
INSERT INTO produto (descricao, valor, imagem) VALUES ('Notebook Dell', 4500.00, 'notebook_dell.jpg');
INSERT INTO produto (descricao, valor, imagem) VALUES ('Mouse Logitech', 150.00, 'mouse_logitech.jpg');
INSERT INTO produto (descricao, valor, imagem) VALUES ('Monitor LG 24 Pol', 900.00, 'monitorlg_24pol.jpg');

-- 3. CADASTRO DE CLIENTES (PESSOAS)
INSERT INTO pessoa (tipo_pessoa, nome, cpf, email, telefone, usuario_id)
VALUES ('PF', 'João Silva', '123.456.789-00', 'joao@email.com', '9999-9999', null);

-- Thiago Medeiros (ID gerado automaticamente como 2, vinculado ao usuario_id 2 do banco)
INSERT INTO pessoa (tipo_pessoa, nome, cpf, email, telefone, usuario_id)
VALUES ('PF', 'Thiago Medeiros', '321.456.345-12', 'thiago@email.com', '1234-5678', 2);