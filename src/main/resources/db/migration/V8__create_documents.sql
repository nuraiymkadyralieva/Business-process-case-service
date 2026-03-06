create table if not exists documents (
                                         id bigserial primary key,
                                         case_id bigint not null,
                                         type varchar(50) not null,
    title varchar(255) not null,
    description varchar(500),
    document_number varchar(100),
    issued_at timestamp,
    created_at timestamp not null default now(),
    created_by varchar(100),

    constraint fk_documents_case
    foreign key (case_id) references cases(id) on delete cascade
    );

create index if not exists idx_documents_case_id on documents(case_id);
create index if not exists idx_documents_case_id_type on documents(case_id, type);