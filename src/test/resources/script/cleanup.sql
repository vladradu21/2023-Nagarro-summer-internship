-- Cleanup income table
DELETE from income;

-- Cleanup expense table
DELETE from expense;

-- Cleanup user_categories table
DELETE FROM user_categories;

-- Cleanup group_categories table
DELETE FROM group_categories;

-- Cleanup role_user_group table
DELETE FROM role_user_group;

-- Cleanup token table
DELETE FROM token;

-- Cleanup user table
DELETE FROM `user`;

-- Cleanup group table
DELETE FROM `group`;

-- Cleanup category table
DELETE FROM category where isDefault = false;