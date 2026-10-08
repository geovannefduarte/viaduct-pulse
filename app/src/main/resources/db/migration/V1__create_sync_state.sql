create table if not exists sync_state (
    source     text primary key,
    watermark  jsonb       not null,
    updated_at timestamptz not null default now()
);
