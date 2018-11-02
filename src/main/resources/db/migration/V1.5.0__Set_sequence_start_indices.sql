SELECT setval(pg_get_serial_sequence('bookrequest', 'id'), coalesce(max(id),0) + 1, false) FROM bookrequest;
SELECT setval(pg_get_serial_sequence('book', 'id'), coalesce(max(id),0) + 1, false) FROM book;
SELECT setval(pg_get_serial_sequence('role', 'id'), coalesce(max(id),0) + 1, false) FROM role;
SELECT setval(pg_get_serial_sequence('bibweb_user', 'id'), coalesce(max(id),0) + 1, false) FROM bibweb_user;
SELECT setval(pg_get_serial_sequence('reservation', 'id'), coalesce(max(id),0) + 1, false) FROM reservation;
SELECT setval(pg_get_serial_sequence('checkout', 'id'), coalesce(max(id),0) + 1, false) FROM checkout;