-- Portable schema: works unchanged on both SQLite and MySQL.
-- Primary keys are VARCHAR(36) UUID strings (no vendor-specific AUTO_INCREMENT),
-- which is exactly what makes migrating SQLITE -> MYSQL a plain row copy.

CREATE TABLE IF NOT EXISTS players (
    uuid        VARCHAR(36)  PRIMARY KEY,
    username    VARCHAR(16)  NOT NULL,
    first_join  BIGINT       NOT NULL,
    last_join   BIGINT       NOT NULL,
    island_id   VARCHAR(36),
    balance     DOUBLE       NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS islands (
    id          VARCHAR(36)  PRIMARY KEY,
    owner_uuid  VARCHAR(36)  NOT NULL,
    world       VARCHAR(64)  NOT NULL,
    center_x    INT          NOT NULL,
    center_y    INT          NOT NULL,
    center_z    INT          NOT NULL,
    level       INT          NOT NULL DEFAULT 0,
    created_at  BIGINT       NOT NULL
);

CREATE TABLE IF NOT EXISTS island_members (
    island_id    VARCHAR(36) NOT NULL,
    player_uuid  VARCHAR(36) NOT NULL,
    role         VARCHAR(16) NOT NULL DEFAULT 'MEMBER',
    PRIMARY KEY (island_id, player_uuid)
);

CREATE TABLE IF NOT EXISTS quests (
    id           VARCHAR(36) PRIMARY KEY,
    name         VARCHAR(64) NOT NULL,
    description  VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS player_quests (
    player_uuid   VARCHAR(36) NOT NULL,
    quest_id      VARCHAR(36) NOT NULL,
    status        VARCHAR(16) NOT NULL DEFAULT 'IN_PROGRESS',
    progress      INT NOT NULL DEFAULT 0,
    completed_at  BIGINT,
    PRIMARY KEY (player_uuid, quest_id)
);
