-- V2__seed_reference_data.sql
-- Seed social programs and regional factors for development

INSERT INTO admin.social_program (name, type, status, description) VALUES
    ('Bolsa Família', 'A', 'A', 'Programa assistencial de transferência de renda'),
    ('Auxílio Emergencial', 'A', 'A', 'Programa assistencial emergencial'),
    ('BPC - Benefício de Prestação Continuada', 'P', 'A', 'Benefício previdenciário para idosos e PCD'),
    ('Seguro Desemprego', 'T', 'A', 'Programa de proteção ao trabalhador'),
    ('Programa Inativo Exemplo', 'A', 'I', 'Programa desativado para testes');

-- Regional factors (27 UFs)
INSERT INTO admin.reference_parameter (category, code, value, description) VALUES
    ('REGIONAL_FACTOR', '1', 1.00, 'AC - Acre'),
    ('REGIONAL_FACTOR', '2', 1.05, 'AL - Alagoas'),
    ('REGIONAL_FACTOR', '3', 1.10, 'AP - Amapá'),
    ('REGIONAL_FACTOR', '4', 1.15, 'AM - Amazonas'),
    ('REGIONAL_FACTOR', '5', 1.20, 'BA - Bahia'),
    ('REGIONAL_FACTOR', '6', 1.25, 'CE - Ceará'),
    ('REGIONAL_FACTOR', '7', 1.32, 'DF - Distrito Federal'),
    ('REGIONAL_FACTOR', '8', 1.10, 'ES - Espírito Santo'),
    ('REGIONAL_FACTOR', '9', 1.00, 'GO - Goiás'),
    ('REGIONAL_FACTOR', '10', 1.05, 'MA - Maranhão'),
    ('REGIONAL_FACTOR', '11', 1.00, 'MT - Mato Grosso'),
    ('REGIONAL_FACTOR', '12', 1.00, 'MS - Mato Grosso do Sul'),
    ('REGIONAL_FACTOR', '13', 1.00, 'MG - Minas Gerais'),
    ('REGIONAL_FACTOR', '14', 1.30, 'PA - Pará'),
    ('REGIONAL_FACTOR', '15', 1.32, 'PB - Paraíba'),
    ('REGIONAL_FACTOR', '16', 1.25, 'PR - Paraná'),
    ('REGIONAL_FACTOR', '17', 1.35, 'PE - Pernambuco'),
    ('REGIONAL_FACTOR', '18', 1.40, 'PI - Piauí'),
    ('REGIONAL_FACTOR', '19', 1.00, 'RJ - Rio de Janeiro'),
    ('REGIONAL_FACTOR', '20', 1.30, 'RN - Rio Grande do Norte'),
    ('REGIONAL_FACTOR', '21', 1.00, 'RS - Rio Grande do Sul'),
    ('REGIONAL_FACTOR', '22', 1.20, 'RO - Rondônia'),
    ('REGIONAL_FACTOR', '23', 1.30, 'RR - Roraima'),
    ('REGIONAL_FACTOR', '24', 1.00, 'SC - Santa Catarina'),
    ('REGIONAL_FACTOR', '25', 1.00, 'SP - São Paulo'),
    ('REGIONAL_FACTOR', '26', 1.25, 'SE - Sergipe'),
    ('REGIONAL_FACTOR', '27', 1.35, 'TO - Tocantins');
