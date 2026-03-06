-- 1) таблица-справочник
create table if not exists procedure_types (
                                               id bigserial primary key,
                                               code varchar(50) not null unique,
    name varchar(150) not null,
    description varchar(500)
    );

-- 2) нормализуем уже существующие cases (если есть)
update cases
set procedure_type = upper(trim(procedure_type))
where procedure_type is not null;

-- 3) перенесём все уже существующие значения из cases в справочник (чтобы FK не упал)
insert into procedure_types(code, name)
select distinct procedure_type, procedure_type
from cases
where procedure_type is not null and procedure_type <> ''
    on conflict (code) do nothing;

-- 4) (опционально) предзаполним несколько типов "по умолчанию"
insert into procedure_types(code, name, description) values
                                                         ('BANKRUPTCY', 'Bankruptcy', 'Bankruptcy procedure'),
                                                         ('COURT_PROCEEDING', 'Court proceeding', 'Court-related procedure'),
                                                         ('CLAIM_REVIEW', 'Claim review', 'Review of claims / requests')
    on conflict (code) do nothing;

-- 5) FK: теперь cases.procedure_type должен существовать в procedure_types.code
alter table cases
    add constraint fk_cases_procedure_type
        foreign key (procedure_type)
            references procedure_types(code);