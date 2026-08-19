SELECT setval(
               pg_get_serial_sequence('course_masterlist', 'id'),
               COALESCE((SELECT MAX(id) FROM course_masterlist), 1)
       );