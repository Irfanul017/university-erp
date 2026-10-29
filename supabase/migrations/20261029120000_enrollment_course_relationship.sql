BEGIN;

-- Historical enrollments have no course assignment in the source data. Keep
-- course_id nullable so existing rows remain valid; the API requires it for
-- every newly created or updated enrollment.
ALTER TABLE public.enrollment
    ADD COLUMN IF NOT EXISTS course_id BIGINT;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conrelid = 'public.enrollment'::regclass
          AND conname = 'fk_enrollment_course'
    ) THEN
        ALTER TABLE public.enrollment
            ADD CONSTRAINT fk_enrollment_course
            FOREIGN KEY (course_id) REFERENCES public.course(course_id);
    END IF;
END
$$;

CREATE INDEX IF NOT EXISTS idx_enrollment_course_id
    ON public.enrollment(course_id);

COMMIT;
