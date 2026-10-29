BEGIN;

ALTER TABLE public.student
    DROP CONSTRAINT IF EXISTS student_current_status_check;

ALTER TABLE public.student
    ADD CONSTRAINT student_current_status_check
    CHECK (
        current_status IN (
            'Active',
            'Graduated',
            'Dropped',
            'Suspended',
            'Transferred',
            'Alumni',
            'Inactive'
        )
    );

COMMIT;
