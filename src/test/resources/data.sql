-- Sample Donation Centers in Nigeria
-- This file will be executed on application startup (for development/demo purposes)

-- Clear existing data to prevent duplicates
DELETE FROM donation_centers;

-- Lagos Donation Centers
INSERT INTO donation_centers (name, type, address, city, state, latitude, longitude, phone_number, email, opening_hours, accepted_items, is_active, website)
VALUES
('Lagos Food Bank Initiative', 'Food Bank', 'Plot 2, Community Road, Ikeja', 'Lagos', 'Lagos State', 6.6018, 3.3515, '+234-1-460-5700', 'info@lagosfoodbank.org', 'Mon-Fri: 9AM-5PM, Sat: 10AM-2PM', 'Fresh produce, canned goods, packaged foods, grains', true, 'https://lagosfoodbank.org'),

('Victoria Island Community Center', 'Community Fridge', '15 Akin Adesola Street, Victoria Island', 'Lagos', 'Lagos State', 6.4281, 3.4219, '+234-803-123-4567', 'contact@vicommunitycenter.org', '24/7', 'All food items, fresh and packaged', true, null),

('Yaba Shelter & Food Program', 'Shelter', '45 Herbert Macaulay Way, Yaba', 'Lagos', 'Lagos State', 6.5152, 3.3758, '+234-802-987-6543', 'help@yabashelter.org', 'Mon-Sun: 8AM-8PM', 'Non-perishable items, canned goods, water', true, 'https://yabashelter.org'),

('Lekki Community Kitchen', 'Food Bank', 'Block 12, Admiralty Way, Lekki Phase 1', 'Lagos', 'Lagos State', 6.4391, 3.4658, '+234-809-555-1234', 'info@lekkikitchen.org', 'Tue-Sat: 10AM-6PM', 'Fresh vegetables, fruits, dairy products, bread', true, null),

-- Abuja Donation Centers
('Abuja Hope Foundation', 'Food Bank', '23 Gimbiya Street, Garki', 'Abuja', 'FCT', 9.0579, 7.4951, '+234-9-234-5678', 'contact@abujahope.org', 'Mon-Fri: 9AM-4PM', 'All food types, hygiene products', true, 'https://abujahope.org'),

('Wuse Community Fridge', 'Community Fridge', 'Wuse Market Area', 'Abuja', 'FCT', 9.0643, 7.4892, '+234-810-222-3333', 'wusefridge@gmail.com', 'Always Open', 'Fresh and packaged foods', true, null),

('Asokoro Shelter Home', 'Shelter', '12 Yakubu Gowon Crescent, Asokoro', 'Abuja', 'FCT', 9.0300, 7.5239, '+234-805-444-5555', 'info@asokoroshelter.org', '24/7', 'Non-perishable foods, toiletries, clothing', true, 'https://asokoroshelter.org'),

-- Port Harcourt Donation Centers
('Port Harcourt Food Share', 'Food Bank', '78 Aba Road, Port Harcourt', 'Port Harcourt', 'Rivers State', 4.8156, 7.0498, '+234-84-123-4567', 'info@phfoodshare.org', 'Mon-Sat: 9AM-5PM', 'All food items, baby food, formula', true, null),

('GRA Community Support', 'Community Fridge', 'Old GRA, Port Harcourt', 'Port Harcourt', 'Rivers State', 4.8339, 7.0083, '+234-803-777-8888', 'gracommunity@gmail.com', 'Daily: 7AM-10PM', 'Fresh produce, packaged goods', true, null),

-- Kano Donation Centers
('Kano Food Relief Center', 'Food Bank', 'Murtala Mohammed Way, Kano', 'Kano', 'Kano State', 12.0022, 8.5920, '+234-64-987-6543', 'help@kanofoodrelief.org', 'Mon-Fri: 8AM-6PM, Sat: 9AM-2PM', 'Grains, canned goods, cooking oil', true, 'https://kanofoodrelief.org'),

('Sabon Gari Community Kitchen', 'Community Fridge', 'Sabon Gari Market Area', 'Kano', 'Kano State', 12.0053, 8.5264, '+234-811-333-4444', 'sabongari@gmail.com', 'Daily: 6AM-9PM', 'All halal food items', true, null),

-- Ibadan Donation Centers
('Ibadan Charity Kitchen', 'Food Bank', '34 Lebanon Street, Dugbe', 'Ibadan', 'Oyo State', 7.3964, 3.8961, '+234-2-810-1234', 'charity@ibadankitchen.org', 'Tue-Sun: 9AM-5PM', 'Fresh vegetables, fruits, grains, dairy', true, null),

('Bodija Community Support', 'Community Fridge', 'Bodija Market, Ibadan', 'Ibadan', 'Oyo State', 7.4340, 3.9090, '+234-802-666-7777', 'bodijasupport@gmail.com', '24/7', 'All food types', true, null);
