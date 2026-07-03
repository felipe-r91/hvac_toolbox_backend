package com.tech.hvac_backend.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Locale;

@Component
public class PartSchemaMigration implements ApplicationRunner {

    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;

    public PartSchemaMigration(DataSource dataSource, JdbcTemplate jdbcTemplate) {
        this.dataSource = dataSource;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!isPostgres()) {
            return;
        }

        jdbcTemplate.execute("""
                DO $$
                DECLARE
                    constraint_schema text;
                    constraint_name text;
                BEGIN
                    SELECT n.nspname, c.conname
                    INTO constraint_schema, constraint_name
                    FROM pg_constraint c
                    JOIN pg_class t ON t.oid = c.conrelid
                    JOIN pg_namespace n ON n.oid = t.relnamespace
                    WHERE t.relname = 'parts'
                      AND c.contype = 'u'
                      AND pg_get_constraintdef(c.oid) = 'UNIQUE (jci_part_number)'
                    LIMIT 1;

                    IF constraint_name IS NOT NULL THEN
                        EXECUTE format(
                            'ALTER TABLE %I.parts DROP CONSTRAINT %I',
                            constraint_schema,
                            constraint_name
                        );
                    END IF;
                END $$;
                """);

        jdbcTemplate.execute("""
                CREATE UNIQUE INDEX IF NOT EXISTS parts_jci_part_number_known_unique_idx
                ON parts (lower(jci_part_number))
                WHERE lower(jci_part_number) <> 'unknown'
                """);
    }

    private boolean isPostgres() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            return connection.getMetaData()
                    .getDatabaseProductName()
                    .toLowerCase(Locale.ROOT)
                    .contains("postgresql");
        }
    }
}
