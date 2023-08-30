ALTER TABLE `user`
    ADD COLUMN `default_currency` CHAR(3);

ALTER TABLE `group`
    ADD COLUMN `default_currency` CHAR(3);