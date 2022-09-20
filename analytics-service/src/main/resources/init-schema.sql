CREATE SCHEMA analytics;

DROP TABLE IF EXISTS analytics.twitter_analytics CASCADE;

CREATE TABLE analytics.twitter_analytics
(
    id        uuid NOT NULL,
    word  character varying NOT NULL ,
    word_count bigint NOT NULL ,
    record_date time with time zone,
    CONSTRAINT twitter_analytics_pkey PRIMARY KEY (id)
);

CREATE INDEX "INDEX_WORD_BY_DATE"
    ON analytics.twitter_analytics USING btree
    (word ASC NULLS LAST, record_date DESC NULLS LAST)
;

