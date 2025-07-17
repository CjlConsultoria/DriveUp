INSERT INTO categoria (nome) VALUES
('PEÇA AUTOMOTIVA'),
('FERRAMENTA'),
('SERVIÇO');


INSERT INTO produto (codigo, descricao, valor_fornecedor, categoria_id) VALUES
('P001', 'Filtro de Óleo', 30.00, (SELECT id FROM categoria WHERE nome = 'PEÇA AUTOMOTIVA')),
('P002', 'Pastilha de Freio', 120.00, (SELECT id FROM categoria WHERE nome = 'PEÇA AUTOMOTIVA')),
('P003', 'Óleo Motor 5W30', 80.00, (SELECT id FROM categoria WHERE nome = 'PEÇA AUTOMOTIVA')),
('P004', 'Velas de Ignição', 60.00, (SELECT id FROM categoria WHERE nome = 'PEÇA AUTOMOTIVA')),
('P005', 'Chave de Fenda', 25.00, (SELECT id FROM categoria WHERE nome = 'FERRAMENTA')),
('P006', 'Macaco Hidráulico', 500.00, (SELECT id FROM categoria WHERE nome = 'FERRAMENTA')),
('P007', 'Bomba de Combustível', 300.00, (SELECT id FROM categoria WHERE nome = 'PEÇA AUTOMOTIVA')),
('P008', 'Serviço de Troca de Óleo', 150.00, (SELECT id FROM categoria WHERE nome = 'SERVIÇO')),
('P009', 'Alinhamento e Balanceamento', 200.00, (SELECT id FROM categoria WHERE nome = 'SERVIÇO')),
('P010', 'Filtro de Ar', 40.00, (SELECT id FROM categoria WHERE nome = 'PEÇA AUTOMOTIVA')),
('P011', 'Lubrificante Multiuso', 35.00, (SELECT id FROM categoria WHERE nome = 'PEÇA AUTOMOTIVA')),
('P012', 'Serviço de Troca de Pastilha de Freio', 180.00, (SELECT id FROM categoria WHERE nome = 'SERVIÇO')),
('P013', 'Chave Inglesa', 45.00, (SELECT id FROM categoria WHERE nome = 'FERRAMENTA')),
('P014', 'Serviço de Troca de Velas', 100.00, (SELECT id FROM categoria WHERE nome = 'SERVIÇO')),
('P015', 'Filtro de Combustível', 50.00, (SELECT id FROM categoria WHERE nome = 'PEÇA AUTOMOTIVA'));

INSERT INTO movimento_estoque (produto_id, tipo, valor_venda, data_venda, quantidade, descricao) VALUES
-- Entradas (compra de peças e ferramentas)
((SELECT id FROM produto WHERE codigo = 'P001'), 'ENTRADA', NULL, '2025-06-01 08:00:00', 20, 'Compra de 20 filtros de óleo.'),
((SELECT id FROM produto WHERE codigo = 'P002'), 'ENTRADA', NULL, '2025-06-02 09:30:00', 15, 'Compra de 15 pastilhas de freio.'),
((SELECT id FROM produto WHERE codigo = 'P005'), 'ENTRADA', NULL, '2025-06-03 10:15:00', 10, 'Compra de 10 chaves de fenda.'),
((SELECT id FROM produto WHERE codigo = 'P006'), 'ENTRADA', NULL, '2025-06-04 11:00:00', 3, 'Compra de 3 macacos hidráulicos.'),
((SELECT id FROM produto WHERE codigo = 'P003'), 'ENTRADA', NULL, '2025-06-05 14:00:00', 30, 'Compra de 30 litros de óleo motor 5W30.'),

-- Saídas (vendas / uso em serviço)
((SELECT id FROM produto WHERE codigo = 'P001'), 'SAIDA', 45.00, '2025-06-10 10:00:00', 5, 'Uso de 5 filtros de óleo em serviços de troca.'),
((SELECT id FROM produto WHERE codigo = 'P002'), 'SAIDA', 180.00, '2025-06-11 14:30:00', 4, 'Uso de 4 pastilhas de freio para substituição.'),
((SELECT id FROM produto WHERE codigo = 'P008'), 'SAIDA', 150.00, '2025-06-12 15:00:00', 1, 'Serviço de troca de óleo realizado.'),
((SELECT id FROM produto WHERE codigo = 'P009'), 'SAIDA', 200.00, '2025-06-13 09:00:00', 1, 'Serviço de alinhamento e balanceamento realizado.'),
((SELECT id FROM produto WHERE codigo = 'P006'), 'SAIDA', NULL, '2025-06-14 11:00:00', 1, 'Uso do macaco hidráulico no serviço.'),

-- Edições (ajuste estoque ou informações)
((SELECT id FROM produto WHERE codigo = 'P001'), 'EDITADO', NULL, '2025-06-15 16:00:00', 15, 'Quantidade ajustada de 20 para 15 devido a inventário.'),
((SELECT id FROM produto WHERE codigo = 'P006'), 'EDITADO', NULL, '2025-06-15 17:00:00', 2, 'Quantidade ajustada de 3 para 2 após verificação.'),
((SELECT id FROM produto WHERE codigo = 'P008'), 'EDITADO', NULL, '2025-06-16 10:30:00', 2, 'Valor do serviço atualizado de 150 para 160.');

UPDATE produto p
SET quantidade_estoque = GREATEST((
  SELECT
    COALESCE(SUM(CASE WHEN m.tipo = 'ENTRADA' THEN m.quantidade ELSE 0 END), 0) -
    COALESCE(SUM(CASE WHEN m.tipo = 'SAIDA' THEN m.quantidade ELSE 0 END), 0)
  FROM movimento_estoque m
  WHERE m.produto_id = p.id
), 0);


