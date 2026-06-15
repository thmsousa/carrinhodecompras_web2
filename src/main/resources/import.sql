-- ====================================================================
-- 1. CONTROLE DE ACESSO (ROLES E USUÁRIOS)
-- ====================================================================
INSERT INTO role (nome) VALUES ('ROLE_ADMIN'); -- ID automático 1
INSERT INTO role (nome) VALUES ('ROLE_USER');  -- ID automático 2

-- admin / senha: admin (ID automático 1)
INSERT INTO usuario (usuario, senha) VALUES ('admin', '$2a$10$tbs4UMB6IouZDQjyy/lE/OArOO1iafF.oXLuAEMS5LIgi2Lxr17yu');
-- thiago / senha: 123 (ID automático 2)
INSERT INTO usuario (usuario, senha) VALUES ('thiago', '$2a$10$57O3OKPPf/cjGhhSmfrgUuj8ofq4UOT.KDYMNYIdF4QW5IwYZgpi6');

-- Tabela associativa muitos-para-muitos (Mapeia as permissões de acesso)
INSERT INTO usuario_roles (usuario_id, role_id) VALUES (1, 1); -- admin -> ROLE_ADMIN
INSERT INTO usuario_roles (usuario_id, role_id) VALUES (2, 2); -- thiago -> ROLE_USER

-- ====================================================================
-- 2. CADASTRO DE PRODUTOS
-- ====================================================================
INSERT INTO produto (descricao, valor, imagem) VALUES ('Notebook Dell', 4500.00, 'notebook_dell.jpg');
INSERT INTO produto (descricao, valor, imagem) VALUES ('Mouse Logitech', 150.00, 'mouse_logitech.jpg');
INSERT INTO produto (descricao, valor, imagem) VALUES ('Monitor LG 24 Pol', 900.00, 'monitorlg_24pol.jpg');
INSERT INTO produto (descricao, valor, imagem) VALUES ('Celular Samsung S26', 6000.00, 'celular_samsung_s26.jpg');
INSERT INTO produto (descricao, valor, imagem) VALUES ('Teclado Logitech', 100.00, 'teclado_logitech.jpg');
INSERT INTO produto (descricao, valor, imagem) VALUES ('Televisão LG 55 Pol.', 2000.00, 'televisao.jpg');