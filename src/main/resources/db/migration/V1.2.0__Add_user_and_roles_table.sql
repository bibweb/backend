CREATE TABLE user (
  id BIGINT NOT NULL AUTO_INCREMENT,
  username NVARCHAR(100) NOT NULL,
  password NVARCHAR(100) NOT NULL,
  PRIMARY KEY(id)
);

CREATE TABLE role (
  id BIGINT NOT NULL AUTO_INCREMENT,
  rolename NVARCHAR(100) NOT NULL,
  PRIMARY KEY(id)
);

CREATE TABLE user_roles(
  user_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  PRIMARY KEY(user_id, role_id)
);

ALTER TABLE user_roles ADD CONSTRAINT ur_role_id FOREIGN KEY(role_id) REFERENCES role(id);
ALTER TABLE user_roles ADD CONSTRAINT ur_user_id FOREIGN KEY(user_id) REFERENCES user(id);

ALTER TABLE user ADD UNIQUE (username);