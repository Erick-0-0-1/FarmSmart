-- Insert Rice Varieties (based on our research)

-- Early Maturing Varieties (100-110 days)
INSERT IGNORE INTO rice_varieties (name, code, maturity_days, season, yield_potential, drought_tolerant, flood_tolerant, pest_resistant, description, recommended_fertilizer, planting_tips) VALUES
                                                                                                                                                                                                 ('Tubigan 10', 'NSIC Rc 152', 107, 'BOTH', 9.8, false, false, false, 'High-yielding early maturing variety suitable for both wet and dry seasons. Popular in Isabela region.', '14-14-14, Urea, 0-0-60', 'Apply basal fertilizer 0-14 days after transplanting. Second application at 28-32 days.'),

                                                                                                                                                                                                 ('Tubigan 18', 'NSIC Rc 222', 109, 'BOTH', 8.9, true, false, false, 'Drought-tolerant variety, excellent for dry season planting. Very popular among Isabela farmers.', '14-14-14, Urea, 0-0-60', 'Recommended for areas with limited water supply. Apply fertilizer in split applications.'),

                                                                                                                                                                                                 ('GSR 8', 'NSIC Rc 480', 110, 'BOTH', 7.5, true, true, true, 'Triple-tolerant variety: drought, submergence, and salinity resistant. Very resilient to climate challenges.', '14-14-14, Urea, 0-0-60', 'Excellent choice for areas prone to flooding or drought. Requires less intensive management.');

-- Medium Maturing Varieties (111-120 days)
INSERT IGNORE INTO rice_varieties (name, code, maturity_days, season, yield_potential, drought_tolerant, flood_tolerant, pest_resistant, description, recommended_fertilizer, planting_tips) VALUES
                                                                                                                                                                                                 ('IR64', 'IR64', 115, 'BOTH', 6.5, false, false, false, 'Most widely planted variety in the Philippines. Excellent grain quality and cooking characteristics.', '14-14-14, Urea, 0-0-60', 'Apply basal fertilizer 0-14 days after transplanting. Second application at 32-36 days.'),

                                                                                                                                                                                                 ('Matatag', 'NSIC Rc 238', 118, 'BOTH', 6.75, false, false, true, 'High-yielding variety with good grain quality. Shows resistance to some common pests.', '14-14-14, Urea, 0-0-60', 'Suitable for both irrigated and rainfed lowland conditions.');

-- Insert Fertilizer Schedules

-- For Tubigan 10 (ID will be 1)
INSERT IGNORE INTO fertilizer_schedules (variety_id, day_after_planting, fertilizer_type, amount_per_hectare, application_method, notes) VALUES
                                                                                                                                             (1, 7, '14-14-14', '1 bag (50kg)', 'Broadcast application', 'Apply during land preparation or 0-14 days after transplanting'),
                                                                                                                                             (1, 30, 'Urea', '1 bag (50kg)', 'Top dressing', 'Apply at 28-32 days after transplanting during active tillering'),
                                                                                                                                             (1, 30, '0-0-60 (Potash)', '0.5 bag (25kg)', 'Top dressing', 'Apply together with Urea for better grain filling');

-- For Tubigan 18 (ID will be 2)
INSERT IGNORE INTO fertilizer_schedules (variety_id, day_after_planting, fertilizer_type, amount_per_hectare, application_method, notes) VALUES
                                                                                                                                             (2, 7, '14-14-14', '1 bag (50kg)', 'Broadcast application', 'Apply during land preparation or 0-14 days after transplanting'),
                                                                                                                                             (2, 30, 'Urea', '1 bag (50kg)', 'Top dressing', 'Apply at 28-32 days after transplanting'),
                                                                                                                                             (2, 30, '0-0-60 (Potash)', '0.5 bag (25kg)', 'Top dressing', 'Apply together with Urea');

-- For GSR 8 (ID will be 3)
INSERT IGNORE INTO fertilizer_schedules (variety_id, day_after_planting, fertilizer_type, amount_per_hectare, application_method, notes) VALUES
                                                                                                                                             (3, 7, '14-14-14', '1 bag (50kg)', 'Broadcast application', 'Apply during land preparation or 0-14 days after transplanting'),
                                                                                                                                             (3, 30, 'Urea', '1 bag (50kg)', 'Top dressing', 'Apply at 28-32 days after transplanting'),
                                                                                                                                             (3, 30, '0-0-60 (Potash)', '0.5 bag (25kg)', 'Top dressing', 'Apply together with Urea');

-- For IR64 (ID will be 4)
INSERT IGNORE INTO fertilizer_schedules (variety_id, day_after_planting, fertilizer_type, amount_per_hectare, application_method, notes) VALUES
                                                                                                                                             (4, 7, '14-14-14', '1 bag (50kg)', 'Broadcast application', 'Apply during land preparation or 0-14 days after transplanting'),
                                                                                                                                             (4, 35, 'Urea', '1 bag (50kg)', 'Top dressing', 'Apply at 32-36 days after transplanting'),
                                                                                                                                             (4, 35, '0-0-60 (Potash)', '0.5 bag (25kg)', 'Top dressing', 'Apply together with Urea');

-- For Matatag (ID will be 5)
INSERT IGNORE INTO fertilizer_schedules (variety_id, day_after_planting, fertilizer_type, amount_per_hectare, application_method, notes) VALUES
                                                                                                                                             (5, 7, '14-14-14', '1 bag (50kg)', 'Broadcast application', 'Apply during land preparation or 0-14 days after transplanting'),
                                                                                                                                             (5, 35, 'Urea', '1 bag (50kg)', 'Top dressing', 'Apply at 32-36 days after transplanting'),
                                                                                                                                      (5, 35, '0-0-60 (Potash)', '0.5 bag (25kg)', 'Top dressing', 'Apply together with Urea');