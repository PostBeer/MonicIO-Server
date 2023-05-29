SET search_path = "jpa";

CREATE OR REPLACE FUNCTION write_log()
    RETURNS TRIGGER
AS '
BEGIN
    IF (NEW IS DISTINCT FROM OLD) THEN
        INSERT INTO jpa.taskslog(project_id, task_id, name, status, changed_on)
        VALUES ((SELECT project_id FROM jpa.project_task WHERE jpa.project_task.task_id = NEW.id),
                NEW.id, NEW.name, NEW.status, now() AT TIME ZONE ''Europe/Moscow'');
    END IF;

    RETURN NEW;
END;
' LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER taskChange AFTER UPDATE
    ON task
    FOR EACH ROW
    EXECUTE PROCEDURE write_log();