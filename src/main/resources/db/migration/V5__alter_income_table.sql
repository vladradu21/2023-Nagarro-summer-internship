ALTER TABLE `income`
    MODIFY COLUMN category VARCHAR(50) NOT NULL,
    ADD COLUMN `repetition_flow` VARCHAR(50) AFTER `addedDate`,
    ADD COLUMN `group_id` INT AFTER `userId`,
    ADD COLUMN `name` VARCHAR(50) AFTER `category`,
    ADD COLUMN `type` VARCHAR(50) AFTER `id`,
    ADD CONSTRAINT `income_ibfk_2` FOREIGN KEY (`group_id`) REFERENCES `group` (`id`);