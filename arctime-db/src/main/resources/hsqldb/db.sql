
DROP TABLE holidays IF EXISTS;
DROP TABLE audit_logs IF EXISTS;
DROP TABLE timesheets IF EXISTS;
DROP TABLE bills IF EXISTS;
DROP TABLE pay_periods IF EXISTS;
DROP TABLE assignments IF EXISTS;
DROP TABLE tasks IF EXISTS;
DROP TABLE supervisors IF EXISTS;
DROP TABLE roles IF EXISTS;
DROP TABLE users IF EXISTS;
DROP TABLE companies IF EXISTS;

CREATE TABLE companies (
    id             INTEGER      GENERATED BY DEFAULT AS IDENTITY
                                (START WITH 1, INCREMENT BY 1) NOT NULL PRIMARY KEY,
    name           VARCHAR(250) NOT NULL,
    active         BOOLEAN      DEFAULT TRUE NOT NULL,

    CONSTRAINT unique_company_name UNIQUE (name)
);

CREATE TABLE users (
    id             INTEGER      GENERATED BY DEFAULT AS IDENTITY
                                (START WITH 1, INCREMENT BY 1) NOT NULL PRIMARY KEY,
    company_id     INTEGER      NOT NULL,
    login          VARCHAR(32)  NOT NULL,
    hashed_pass    VARCHAR(128) NOT NULL,
    email          VARCHAR_IGNORECASE(255) NOT NULL,
    first_name     VARCHAR(50)  NOT NULL,
    last_name      VARCHAR(50)  NOT NULL,
    active         BOOLEAN      DEFAULT TRUE NOT NULL,

    CONSTRAINT fk_users_company_id FOREIGN KEY (company_id)
        REFERENCES companies(id) ON DELETE CASCADE,

    CONSTRAINT unique_user_login UNIQUE (login),
    CONSTRAINT unique_user_email UNIQUE (email)
);

CREATE TABLE roles (
    name           VARCHAR(30)  NOT NULL,
    user_id        INTEGER      NOT NULL,
    
    CONSTRAINT unique_role UNIQUE (name, user_id),

    CONSTRAINT fk_roles_user_id FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE supervisors (
    company_id     INTEGER      NOT NULL,
    user_id        INTEGER      NOT NULL,
    supervisor_id  INTEGER      NOT NULL,
    is_primary     BOOLEAN      DEFAULT FALSE NOT NULL,

    CONSTRAINT fk_supervisors_company_id FOREIGN KEY (company_id)
        REFERENCES companies(id) ON DELETE CASCADE,
    CONSTRAINT fk_supervisors_user_id FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_supervisors_supervisor_id FOREIGN KEY (supervisor_id)
        REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE tasks (
    id             INTEGER      GENERATED BY DEFAULT AS IDENTITY
                                (START WITH 1, INCREMENT BY 1) NOT NULL PRIMARY KEY,
    company_id     INTEGER      NOT NULL,
    description    VARCHAR(255) NOT NULL,
    job_code       VARCHAR(255) NOT NULL,
    admin          BOOLEAN      DEFAULT FALSE NOT NULL,
    active         BOOLEAN      DEFAULT TRUE NOT NULL,

    CONSTRAINT unique_task UNIQUE (job_code),

    CONSTRAINT fk_tasks_company_id FOREIGN KEY (company_id)
        REFERENCES companies(id) ON DELETE CASCADE
);

CREATE TABLE assignments (
    id             INTEGER      GENERATED BY DEFAULT AS IDENTITY
                                (START WITH 1, INCREMENT BY 1) NOT NULL PRIMARY KEY,
    company_id     INTEGER      NOT NULL,
    task_id        INTEGER      NOT NULL,
    user_id        INTEGER      NOT NULL,
    labor_cat      VARCHAR(120) NOT NULL,
    item_name      VARCHAR(255) NOT NULL,
    begin          DATE,
    end            DATE,

    CONSTRAINT fk_assignments_company_id FOREIGN KEY (company_id)
        REFERENCES companies(id) ON DELETE CASCADE,
    CONSTRAINT fk_assignments_task_id FOREIGN KEY (task_id)
        REFERENCES tasks(id) ON DELETE CASCADE,
    CONSTRAINT fk_assignments_user_id FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE pay_periods (
    company_id     INTEGER      NOT NULL,
    begin          DATE         NOT NULL PRIMARY KEY,
    end            DATE         NOT NULL,
    type           VARCHAR(20)  NOT NULL,

    CONSTRAINT fk_pay_periods_company_id FOREIGN KEY (company_id)
        REFERENCES companies(id) ON DELETE CASCADE
);

CREATE TABLE bills (
    id             INTEGER      GENERATED BY DEFAULT AS IDENTITY
                                (START WITH 1, INCREMENT BY 1) NOT NULL PRIMARY KEY,
    assignment_id  INTEGER,
    task_id        INTEGER      NOT NULL,
    user_id        INTEGER      NOT NULL,
    day            DATE         NOT NULL,
    hours          VARCHAR(6)   NOT NULL,
    timestamp      TIMESTAMP    DEFAULT NOW() NOT NULL,

    CONSTRAINT unique_bill UNIQUE (assignment_id, task_id, user_id, day),

    CONSTRAINT fk_bills_assignment_id FOREIGN KEY (assignment_id)
        REFERENCES assignments(id) ON DELETE CASCADE,
    CONSTRAINT fk_bills_task_id FOREIGN KEY (task_id)
        REFERENCES tasks(id) ON DELETE CASCADE,
    CONSTRAINT fk_bills_user_id FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE timesheets (
    id             INTEGER      GENERATED BY DEFAULT AS IDENTITY
                                (START WITH 1, INCREMENT BY 1) NOT NULL PRIMARY KEY,
    company_id     INTEGER      NOT NULL,
    user_id        INTEGER      NOT NULL,
    pp_begin       DATE         NOT NULL,
    completed      BOOLEAN      DEFAULT FALSE NOT NULL,
    approved       BOOLEAN      DEFAULT FALSE NOT NULL,
    verified       BOOLEAN      DEFAULT FALSE NOT NULL,
    exported       BOOLEAN      DEFAULT FALSE NOT NULL,
    approver_id    INTEGER,
    verifier_id    INTEGER,
    exporter_id    INTEGER,

    CONSTRAINT unique_timesheet UNIQUE (user_id, pp_begin),

    CONSTRAINT fk_timesheets_company_id FOREIGN KEY (company_id)
        REFERENCES companies(id) ON DELETE CASCADE,
    CONSTRAINT fk_timesheets_user_id FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_timesheets_pp_begin FOREIGN KEY (pp_begin)
        REFERENCES pay_periods(begin) ON DELETE CASCADE,
    CONSTRAINT fk_timesheets_approver_id FOREIGN KEY (approver_id)
        REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_timesheets_verifier_id FOREIGN KEY (verifier_id)
        REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_timesheets_exporter_id FOREIGN KEY (exporter_id)
        REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE audit_logs (
    company_id     INTEGER      NOT NULL,
    timesheet_id   INTEGER      NOT NULL,
    log            LONGVARCHAR  NOT NULL,
    timestamp      TIMESTAMP    DEFAULT NOW() NOT NULL,

    CONSTRAINT fk_audit_logs_company_id FOREIGN KEY (company_id)
        REFERENCES companies(id) ON DELETE CASCADE,
    CONSTRAINT fk_audit_logs_timesheet_id FOREIGN KEY (timesheet_id)
        REFERENCES timesheets(id) ON DELETE CASCADE
);

CREATE TABLE holidays (
    id             INTEGER      GENERATED BY DEFAULT AS IDENTITY
                                (START WITH 1, INCREMENT BY 1) NOT NULL PRIMARY KEY,
    company_id     INTEGER      NOT NULL,
    description    VARCHAR(100) NOT NULL,
    config         VARCHAR(100) NOT NULL,

    CONSTRAINT unique_holiday_desc UNIQUE (description),
    CONSTRAINT unique_holiday_config UNIQUE (config),

    CONSTRAINT fk_holidays_company_id FOREIGN KEY (company_id)
        REFERENCES companies(id) ON DELETE CASCADE
);

