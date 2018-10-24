CREATE TABLE reservation (
  id BIGINT NOT NULL AUTO_INCREMENT,
  book_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  reserved_at TIMESTAMP NOT NULL,
  active BIT NOT NULL,
  PRIMARY KEY(id)
);

ALTER TABLE reservation ADD CONSTRAINT res_book_id FOREIGN KEY(book_id) REFERENCES book(id);
ALTER TABLE reservation ADD CONSTRAINT res_user_id FOREIGN KEY(user_id) REFERENCES user(id);

ALTER TABLE user ADD UNIQUE (username);

INSERT INTO reservation(id, book_id, user_id, reserved_at, active) VALUES(1, 12, 1, DATEADD('DAY', -2, NOW()), false),
(2, 13, 1, NOW(), false),
(3, 15, 2, NOW(), true),
(4, 19, 4, NOW(), true),
(5, 12, 1, NOW(), true);