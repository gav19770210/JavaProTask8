-- Table: public.user_limits

-- DROP TABLE IF EXISTS public.user_limits;

CREATE TABLE IF NOT EXISTS public.user_limits
(
    id bigint NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ),
    user_id bigint NOT NULL,
    daily_limit DECIMAL NOT NULL,
    curr_daily_limit DECIMAL NOT NULL,
    curr_daily_limit_date TIMESTAMP,
    CONSTRAINT user_limits_pkey PRIMARY KEY (id),
    CONSTRAINT user_limits_user_id_key UNIQUE (user_id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.user_limits
    OWNER to postgres;
-- Index: user_limits_user_id_idx

-- DROP INDEX IF EXISTS public.user_limits_user_id_idx;

CREATE UNIQUE INDEX IF NOT EXISTS user_limits_user_id_idx
    ON public.user_limits USING btree
    (user_id ASC NULLS LAST)
    TABLESPACE pg_default;
