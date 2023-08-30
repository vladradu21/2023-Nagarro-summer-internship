CREATE TABLE `category`
(
    `id`        INT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    `name`      VARCHAR(50) NOT NULL,
    `type`      VARCHAR(50) NOT NULL,
    `isDefault` BOOLEAN     NOT NULL DEFAULT false
);

CREATE TABLE user_categories
(
    user_id       INT         NOT NULL,
    category_id   INT         NOT NULL,
    category_name VARCHAR(50) NOT NULL,
    type        VARCHAR(50) NOT NULL,
    `isDefault` BOOLEAN     NOT NULL DEFAULT false,
    `isSelected` BOOLEAN     NOT NULL DEFAULT false,
    PRIMARY KEY (user_id, category_name),
    FOREIGN KEY (user_id) REFERENCES `user` (id),
    FOREIGN KEY (category_id) REFERENCES category (id)
);

CREATE TABLE group_categories
(
    group_id      INT         NOT NULL,
    category_id   INT         NOT NULL,
    category_name VARCHAR(50) NOT NULL,
    type          VARCHAR(50) NOT NULL,
    `isDefault` BOOLEAN     NOT NULL DEFAULT false,
    `isSelected` BOOLEAN     NOT NULL DEFAULT false,
    PRIMARY KEY (group_id, category_id),
    FOREIGN KEY (group_id) REFERENCES `group` (id),
    FOREIGN KEY (category_id) REFERENCES category (id)
);