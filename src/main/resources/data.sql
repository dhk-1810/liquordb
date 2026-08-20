-- liquor_subcategories 초기 소분류 데이터 시딩 (INSERT IGNORE)

-- COCKTAIL (베이스 기준)
INSERT IGNORE INTO liquor_subcategories (name, name_ko, category) VALUES ('Vodka based', '보드카 베이스', 'COCKTAIL');
INSERT IGNORE INTO liquor_subcategories (name, name_ko, category) VALUES ('Rum based', '럼 베이스', 'COCKTAIL');
INSERT IGNORE INTO liquor_subcategories (name, name_ko, category) VALUES ('Gin based', '진 베이스', 'COCKTAIL');
INSERT IGNORE INTO liquor_subcategories (name, name_ko, category) VALUES ('Tequila based', '데킬라 베이스', 'COCKTAIL');
INSERT IGNORE INTO liquor_subcategories (name, name_ko, category) VALUES ('Whisky based', '위스키 베이스', 'COCKTAIL');
INSERT IGNORE INTO liquor_subcategories (name, name_ko, category) VALUES ('Brandy based', '브랜디 베이스', 'COCKTAIL');
INSERT IGNORE INTO liquor_subcategories (name, name_ko, category) VALUES ('Liqueur based', '리큐르 베이스', 'COCKTAIL');
INSERT IGNORE INTO liquor_subcategories (name, name_ko, category) VALUES ('Other', '기타', 'COCKTAIL');

-- WHISKY
INSERT IGNORE INTO liquor_subcategories (name, name_ko, category) VALUES ('Single Malt', '싱글 몰트', 'WHISKY');
INSERT IGNORE INTO liquor_subcategories (name, name_ko, category) VALUES ('Blended', '블렌디드', 'WHISKY');
INSERT IGNORE INTO liquor_subcategories (name, name_ko, category) VALUES ('Bourbon', '버번', 'WHISKY');
INSERT IGNORE INTO liquor_subcategories (name, name_ko, category) VALUES ('Rye', '라이', 'WHISKY');

-- BEER
INSERT IGNORE INTO liquor_subcategories (name, name_ko, category) VALUES ('Ale', '에일', 'BEER');
INSERT IGNORE INTO liquor_subcategories (name, name_ko, category) VALUES ('Lager', '라거', 'BEER');
INSERT IGNORE INTO liquor_subcategories (name, name_ko, category) VALUES ('Wheat', '밀맥주', 'BEER');
INSERT IGNORE INTO liquor_subcategories (name, name_ko, category) VALUES ('Stout', '스타우트', 'BEER');

-- WINE
INSERT IGNORE INTO liquor_subcategories (name, name_ko, category) VALUES ('Red', '레드 와인', 'WINE');
INSERT IGNORE INTO liquor_subcategories (name, name_ko, category) VALUES ('White', '화이트 와인', 'WINE');
INSERT IGNORE INTO liquor_subcategories (name, name_ko, category) VALUES ('Sparkling', '스파클링 와인', 'WINE');
INSERT IGNORE INTO liquor_subcategories (name, name_ko, category) VALUES ('Rosé', '로제 와인', 'WINE');
