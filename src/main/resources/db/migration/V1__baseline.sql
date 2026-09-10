-- Baseline: estado do schema no momento da adoção do Flyway.
--
-- Extraído com pg_dump --schema-only do banco gerado por hibernate.ddl-auto=update,
-- para que bancos novos nasçam idênticos aos que já existem. Os nomes gerados pelo
-- Hibernate (ukkb972..., fkcgtkui...) foram mantidos de propósito: renomeá-los faria
-- um banco novo divergir dos já existentes, que não passam por esta migração.
--
-- A partir daqui, toda mudança de schema é uma migração nova (V2__, V3__, ...).

CREATE TABLE usuarios (
    id uuid NOT NULL,
    email character varying(255) NOT NULL,
    nome character varying(100) NOT NULL,
    role character varying(255) NOT NULL,
    senha character varying(255) NOT NULL,
    CONSTRAINT usuarios_pkey PRIMARY KEY (id),
    CONSTRAINT ukkfsp0s1tflm1cwlj8idhqsad0 UNIQUE (email),
    CONSTRAINT usuarios_role_check CHECK (((role)::text = ANY ((ARRAY['ADMIN'::character varying, 'CLIENTE'::character varying])::text[])))
);

CREATE TABLE barbeiros (
    id uuid NOT NULL,
    ativo boolean NOT NULL,
    especialidade character varying(255) NOT NULL,
    nome character varying(100) NOT NULL,
    CONSTRAINT barbeiros_pkey PRIMARY KEY (id),
    CONSTRAINT barbeiros_especialidade_check CHECK (((especialidade)::text = ANY ((ARRAY['FADE'::character varying, 'DEGRADE'::character varying, 'CORTE_SOCIAL'::character varying, 'BARBA'::character varying, 'CABELO_LONGO'::character varying])::text[])))
);

CREATE TABLE servicos (
    id uuid NOT NULL,
    ativo boolean NOT NULL,
    descricao character varying(255) NOT NULL,
    duracao_minutos integer NOT NULL,
    nome character varying(255) NOT NULL,
    preco numeric(10,2) NOT NULL,
    CONSTRAINT servicos_pkey PRIMARY KEY (id),
    CONSTRAINT ukkb972hkbvdm429coc3qxrf2wp UNIQUE (nome)
);

CREATE TABLE agendamentos (
    id uuid NOT NULL,
    criado_em timestamp(6) without time zone NOT NULL,
    data_hora_visita timestamp(6) without time zone NOT NULL,
    status_agendamento character varying(255) NOT NULL,
    valor_servico_no_momento numeric(10,2) NOT NULL,
    barbeiro_id uuid NOT NULL,
    cliente_id uuid NOT NULL,
    servico_id uuid NOT NULL,
    CONSTRAINT agendamentos_pkey PRIMARY KEY (id),
    CONSTRAINT agendamentos_status_agendamento_check CHECK (((status_agendamento)::text = ANY ((ARRAY['AGENDADO'::character varying, 'CONCLUIDO'::character varying, 'CANCELADO'::character varying])::text[]))),
    CONSTRAINT fkh8l520yidj3wpn6t1asp7ybkd FOREIGN KEY (barbeiro_id) REFERENCES barbeiros(id),
    CONSTRAINT fkl5sc6m80kowo1xbid9dodxvno FOREIGN KEY (cliente_id) REFERENCES usuarios(id),
    CONSTRAINT fkcgtkuidmxhrkhgt0m742k35wd FOREIGN KEY (servico_id) REFERENCES servicos(id)
);

-- Unicidade parcial: um barbeiro não pode ter dois agendamentos ativos no mesmo horário,
-- mas um agendamento CANCELADO libera o horário. JPA não expressa índice parcial, por isso
-- a entidade Agendamento não declara uniqueConstraints.
CREATE UNIQUE INDEX uk_barbeiro_data_hora_visita_ativo
    ON agendamentos (barbeiro_id, data_hora_visita)
    WHERE ((status_agendamento)::text <> 'CANCELADO'::text);
