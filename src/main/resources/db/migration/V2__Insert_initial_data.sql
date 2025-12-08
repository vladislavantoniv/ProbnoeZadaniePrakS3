
INSERT INTO patients (last_name, first_name, middle_name, birth_date, gender, phone_number, email) VALUES
('Антонив', 'Владислав', 'Дмитриевич', '2000-12-14', 'MALE', '+7911651445', 'vlad2000chess@gmail.com'),
('Медведева', 'Мария', 'Романовна', '2003-06-13', 'FEMALE', '+7998498', 'medvedeva@mail.ru')
ON CONFLICT (email) DO NOTHING;

INSERT INTO test_types (name, code, description, price) VALUES
('Общий анализ крови', 'OAK-001', 'Анализ крови с определением основных показателей', 500.00),
('Анализ на наличие психотропных веществ', 'АНП-001', 'Анализ на наличие психотропных веществ и наркотиков до кучи', 8000.00),
('Биохимический анализ крови', 'BIO-001', 'Расширенный биохимический анализ, показывает то, что не показывает общй анализ крови', 2500.00)
ON CONFLICT (code) DO NOTHING;

INSERT INTO orders (patient_id, comment)
SELECT p.id, 'Плановое обследование'
FROM patients p
WHERE p.email = 'vlad2000chess@gmail.com'
ON CONFLICT DO NOTHING;

INSERT INTO orders (patient_id, comment)
SELECT p.id, 'Диагностика из-за симптомов'
FROM patients p
WHERE p.email = 'medvedeva@mail.ru'
ON CONFLICT DO NOTHING;

INSERT INTO tests (order_id, test_type_id, status)
SELECT o.id, tt.id, 'PENDING'
FROM orders o
CROSS JOIN test_types tt
WHERE o.comment = 'Плановое обследование'
AND tt.code IN ('OAK-001', 'АНП-001')
ON CONFLICT DO NOTHING;

INSERT INTO tests (order_id, test_type_id, status)
SELECT o.id, tt.id, 'PENDING'
FROM orders o
CROSS JOIN test_types tt
WHERE o.comment = 'Диагностика из-за симптомов'
AND tt.code = 'BIO-001'
ON CONFLICT DO NOTHING;