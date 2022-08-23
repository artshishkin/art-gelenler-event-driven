DROP TABLE IF EXISTS public.users CASCADE;

CREATE TABLE public.users
(
    id        uuid NOT NULL,
    username  character varying,
    firstname character varying,
    lastname  character varying,
    CONSTRAINT users_pkey PRIMARY KEY (id)
);

DROP TABLE IF EXISTS public.documents CASCADE;

CREATE TABLE public.documents
(
    id          uuid                                           NOT NULL,
    document_id character varying NOT NULL,
    CONSTRAINT documents_pkey PRIMARY KEY (id)
);

DROP TABLE IF EXISTS public.user_permissions CASCADE;

CREATE TABLE public.user_permissions
(
    user_id            uuid NOT NULL,
    document_id        uuid NOT NULL,
    user_permission_id uuid NOT NULL,
    permission_type    character varying,
    CONSTRAINT document_fk FOREIGN KEY (document_id)
        REFERENCES public.documents (id),
    CONSTRAINT user_fk FOREIGN KEY (user_id)
        REFERENCES public.users (id)
);

CREATE INDEX "fki_USER_FK"
    ON public.user_permissions USING btree
    (user_id ASC NULLS LAST)
;

CREATE INDEX fki_document_fk
    ON public.user_permissions USING btree
    (document_id ASC NULLS LAST)
;
