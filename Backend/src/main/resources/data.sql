-- Inserir categorias (se não existirem)
INSERT INTO categoria (nome)
SELECT 'PEÇA AUTOMOTIVA'
WHERE NOT EXISTS (SELECT 1 FROM categoria WHERE nome = 'PEÇA AUTOMOTIVA');

INSERT INTO categoria (nome)
SELECT 'FERRAMENTA'
WHERE NOT EXISTS (SELECT 1 FROM categoria WHERE nome = 'FERRAMENTA');

INSERT INTO categoria (nome)
SELECT 'SERVIÇO'
WHERE NOT EXISTS (SELECT 1 FROM categoria WHERE nome = 'SERVIÇO');


-- Inserir produtos (se não existirem)
INSERT INTO produto (codigo, descricao, valor_fornecedor, categoria_id)
SELECT 'P001', 'Filtro de Óleo', 30.00, (SELECT id FROM categoria WHERE nome = 'PEÇA AUTOMOTIVA')
WHERE NOT EXISTS (SELECT 1 FROM produto WHERE codigo = 'P001');

INSERT INTO produto (codigo, descricao, valor_fornecedor, categoria_id)
SELECT 'P002', 'Pastilha de Freio', 120.00, (SELECT id FROM categoria WHERE nome = 'PEÇA AUTOMOTIVA')
WHERE NOT EXISTS (SELECT 1 FROM produto WHERE codigo = 'P002');

INSERT INTO produto (codigo, descricao, valor_fornecedor, categoria_id)
SELECT 'P003', 'Óleo Motor 5W30', 80.00, (SELECT id FROM categoria WHERE nome = 'PEÇA AUTOMOTIVA')
WHERE NOT EXISTS (SELECT 1 FROM produto WHERE codigo = 'P003');

INSERT INTO produto (codigo, descricao, valor_fornecedor, categoria_id)
SELECT 'P004', 'Velas de Ignição', 60.00, (SELECT id FROM categoria WHERE nome = 'PEÇA AUTOMOTIVA')
WHERE NOT EXISTS (SELECT 1 FROM produto WHERE codigo = 'P004');

INSERT INTO produto (codigo, descricao, valor_fornecedor, categoria_id)
SELECT 'P005', 'Chave de Fenda', 25.00, (SELECT id FROM categoria WHERE nome = 'FERRAMENTA')
WHERE NOT EXISTS (SELECT 1 FROM produto WHERE codigo = 'P005');

INSERT INTO produto (codigo, descricao, valor_fornecedor, categoria_id)
SELECT 'P006', 'Macaco Hidráulico', 500.00, (SELECT id FROM categoria WHERE nome = 'FERRAMENTA')
WHERE NOT EXISTS (SELECT 1 FROM produto WHERE codigo = 'P006');

INSERT INTO produto (codigo, descricao, valor_fornecedor, categoria_id)
SELECT 'P007', 'Bomba de Combustível', 300.00, (SELECT id FROM categoria WHERE nome = 'PEÇA AUTOMOTIVA')
WHERE NOT EXISTS (SELECT 1 FROM produto WHERE codigo = 'P007');

INSERT INTO produto (codigo, descricao, valor_fornecedor, categoria_id)
SELECT 'P008', 'Serviço de Troca de Óleo', 150.00, (SELECT id FROM categoria WHERE nome = 'SERVIÇO')
WHERE NOT EXISTS (SELECT 1 FROM produto WHERE codigo = 'P008');

INSERT INTO produto (codigo, descricao, valor_fornecedor, categoria_id)
SELECT 'P009', 'Alinhamento e Balanceamento', 200.00, (SELECT id FROM categoria WHERE nome = 'SERVIÇO')
WHERE NOT EXISTS (SELECT 1 FROM produto WHERE codigo = 'P009');

INSERT INTO produto (codigo, descricao, valor_fornecedor, categoria_id)
SELECT 'P010', 'Filtro de Ar', 40.00, (SELECT id FROM categoria WHERE nome = 'PEÇA AUTOMOTIVA')
WHERE NOT EXISTS (SELECT 1 FROM produto WHERE codigo = 'P010');

INSERT INTO produto (codigo, descricao, valor_fornecedor, categoria_id)
SELECT 'P011', 'Lubrificante Multiuso', 35.00, (SELECT id FROM categoria WHERE nome = 'PEÇA AUTOMOTIVA')
WHERE NOT EXISTS (SELECT 1 FROM produto WHERE codigo = 'P011');

INSERT INTO produto (codigo, descricao, valor_fornecedor, categoria_id)
SELECT 'P012', 'Serviço de Troca de Pastilha de Freio', 180.00, (SELECT id FROM categoria WHERE nome = 'SERVIÇO')
WHERE NOT EXISTS (SELECT 1 FROM produto WHERE codigo = 'P012');

INSERT INTO produto (codigo, descricao, valor_fornecedor, categoria_id)
SELECT 'P013', 'Chave Inglesa', 45.00, (SELECT id FROM categoria WHERE nome = 'FERRAMENTA')
WHERE NOT EXISTS (SELECT 1 FROM produto WHERE codigo = 'P013');

INSERT INTO produto (codigo, descricao, valor_fornecedor, categoria_id)
SELECT 'P014', 'Serviço de Troca de Velas', 100.00, (SELECT id FROM categoria WHERE nome = 'SERVIÇO')
WHERE NOT EXISTS (SELECT 1 FROM produto WHERE codigo = 'P014');

INSERT INTO produto (codigo, descricao, valor_fornecedor, categoria_id)
SELECT 'P015', 'Filtro de Combustível', 50.00, (SELECT id FROM categoria WHERE nome = 'PEÇA AUTOMOTIVA')
WHERE NOT EXISTS (SELECT 1 FROM produto WHERE codigo = 'P015');


-- Inserir movimentos de estoque (só se não existirem)
-- Exemplo para um registro, repita para os demais
INSERT INTO movimento_estoque (produto_id, tipo, valor_venda, data_venda, quantidade, descricao)
SELECT (SELECT id FROM produto WHERE codigo = 'P001'), 'ENTRADA', NULL, '2025-06-01 08:00:00', 20, 'Compra de 20 filtros de óleo.'
WHERE NOT EXISTS (
  SELECT 1 FROM movimento_estoque
  WHERE produto_id = (SELECT id FROM produto WHERE codigo = 'P001')
  AND tipo = 'ENTRADA'
  AND data_venda = '2025-06-01 08:00:00'
);

-- Repita esse padrão para os outros registros de movimento_estoque


-- Atualizar a quantidade em estoque de cada produto
UPDATE produto p
SET quantidade_estoque = GREATEST((
  SELECT
    COALESCE(SUM(CASE WHEN m.tipo = 'ENTRADA' THEN m.quantidade ELSE 0 END), 0) -
    COALESCE(SUM(CASE WHEN m.tipo = 'SAIDA' THEN m.quantidade ELSE 0 END), 0)
  FROM movimento_estoque m
  WHERE m.produto_id = p.id
), 0);


-- Inserir tipos padrão (TB_TIPO) apenas se não existirem
INSERT INTO drive_up.TB_TIPO (ID, DESCRICAO)
SELECT 1, 'ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM drive_up.TB_TIPO WHERE ID = 1);

INSERT INTO drive_up.TB_TIPO (ID, DESCRICAO)
SELECT 2, 'MECANICO'
WHERE NOT EXISTS (SELECT 1 FROM drive_up.TB_TIPO WHERE ID = 2);

INSERT INTO drive_up.TB_TIPO (ID, DESCRICAO)
SELECT 3, 'RECEPCIONISTA'
WHERE NOT EXISTS (SELECT 1 FROM drive_up.TB_TIPO WHERE ID = 3);

INSERT INTO drive_up.TB_TIPO (ID, DESCRICAO)
SELECT 4, 'AJUDANTE'
WHERE NOT EXISTS (SELECT 1 FROM drive_up.TB_TIPO WHERE ID = 4);


-- Inserir empresa admin padrão (se não existir)


INSERT INTO drive_up.TB_USUARIO_ROLE (ID, ROLE) VALUES
(1, 'ADMIN'),
(2, 'ADMINISTRATIVO'),
(3, 'USUARIO')
ON CONFLICT (ID) DO UPDATE SET
    ROLE = EXCLUDED.ROLE;
