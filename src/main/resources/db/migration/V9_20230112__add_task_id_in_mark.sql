alter table mark
add column task_id bigint;

update mark set task_id = (select p.task_id from project p where p.id = project_id)