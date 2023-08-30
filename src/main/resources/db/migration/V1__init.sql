CREATE TABLE `user`
(
    id        INT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    email     VARCHAR(50) UNIQUE NOT NULL,
    username  VARCHAR(20) UNIQUE NOT NULL,
    password  VARCHAR(20)        NOT NULL,
    firstName VARCHAR(50)        NOT NULL,
    lastName  VARCHAR(50)        NOT NULL,
    country   VARCHAR(20) NULL,
    age       INT                NOT NULL
);

CREATE TABLE `group`
(
    id   INT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    name VARCHAR(20) NOT NULL
);

CREATE TABLE `role`
(
    id   INT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    name VARCHAR(50) NOT NULL
);

CREATE TABLE `role_user_group`
(
    userId  INT NOT NULL,
    groupId INT NOT NULL,
    roleId  INT NOT NULL,
    PRIMARY KEY (userId, groupId, roleId),
    UNIQUE (userId, groupId),
    FOREIGN KEY (userId) REFERENCES `user` (id),
    FOREIGN KEY (groupId) REFERENCES `group` (id),
    FOREIGN KEY (roleId) REFERENCES `role` (id)
);

CREATE TABLE `income`
(
    id          INT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    category    INT          NOT NULL,
    amount      DOUBLE       NOT NULL,
    currency    VARCHAR(255) NOT NULL,
    description VARCHAR(255) NULL,
    addedDate   DATE         NOT NULL,
    userId      INT          NOT NULL,
    FOREIGN KEY (userId) REFERENCES `user` (id)
);

CREATE TABLE `expense`
(
    id          INT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    category    VARCHAR(20)  NOT NULL,
    amount      DOUBLE       NOT NULL,
    name        VARCHAR(20) NULL,
    description VARCHAR(255) NULL,
    currency    VARCHAR(255) NOT NULL,
    addedDate   DATE         NOT NULL,
    userId      INT          NOT NULL,
    FOREIGN KEY (userId) REFERENCES `user` (id)
);