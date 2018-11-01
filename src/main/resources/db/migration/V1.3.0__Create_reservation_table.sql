CREATE TABLE reservation (
  id BIGSERIAL NOT NULL,
  book_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  reserved_at TIMESTAMP NOT NULL,
  active BOOLEAN NOT NULL,
  PRIMARY KEY(id)
);

ALTER TABLE reservation ADD CONSTRAINT res_book_id FOREIGN KEY(book_id) REFERENCES book(id);
ALTER TABLE reservation ADD CONSTRAINT res_user_id FOREIGN KEY(user_id) REFERENCES bibweb_user(id);

INSERT INTO reservation(id, book_id, user_id, reserved_at, active) VALUES(1, 12, 1, NOW(), true),
(2, 13, 1, NOW(), false),
(3, 15, 2, NOW(), true),
(4, 19, 4, NOW(), true);