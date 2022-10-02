alter table mark
add coefficient double precision;

alter table mark
    add is_teacher_mark boolean;

ALTER TABLE mark
    RENAME COLUMN student_id TO owner_id;