
DROP TABLE IF EXISTS holidays;
DROP TABLE IF EXISTS audit_logs;
DROP TABLE IF EXISTS timesheets;
DROP TABLE IF EXISTS bills;
DROP TABLE IF EXISTS pay_periods;
DROP TABLE IF EXISTS contract_assignments;
DROP TABLE IF EXISTS contracts;
DROP TABLE IF EXISTS supervisors;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS employees;
DROP TABLE IF EXISTS companies;

CREATE TABLE IF NOT EXISTS companies (
    `id`             INTEGER      NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `name`           VARCHAR(250) NOT NULL,
    `active`         BOOLEAN      NOT NULL DEFAULT TRUE,

    CONSTRAINT unique_company_name UNIQUE (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS employees (
    `id`             INTEGER      NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `company_id`     INTEGER      NOT NULL,
    `login`          VARCHAR(32)  NOT NULL,
    `hashed_pass`    VARCHAR(128) NOT NULL,
    `email`          VARCHAR(255) NOT NULL,
    `first_name`     VARCHAR(50)  NOT NULL,
    `last_name`      VARCHAR(50)  NOT NULL,
    `suffix`         VARCHAR(32),
    `division`       VARCHAR(255) NOT NULL,
    `personnel_type` VARCHAR(40)  NOT NULL,
    `active`         BOOLEAN      NOT NULL DEFAULT TRUE,

    CONSTRAINT fk_employees_company_id FOREIGN KEY (`company_id`)
        REFERENCES companies(`id`) ON DELETE CASCADE,

    CONSTRAINT unique_employee_login UNIQUE (`login`),
    CONSTRAINT unique_employee_email UNIQUE (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS roles (
    `name`           VARCHAR(30)  NOT NULL,
    `employee_id`    INTEGER      NOT NULL,
    
    CONSTRAINT unique_role UNIQUE (`name`, `employee_id`),

    CONSTRAINT fk_role_employee_id FOREIGN KEY (`employee_id`)
        REFERENCES employees(`id`) ON DELETE CASCADE,

    INDEX idx_roles_name USING HASH (`name`),
    INDEX idx_roles_employee_id USING HASH (`employee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS supervisors (
    `employee_id`    INTEGER      NOT NULL,
    `supervisor_id`  INTEGER      NOT NULL,
    `primary`        BOOLEAN      NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_supervisors_employee_id FOREIGN KEY (`employee_id`)
        REFERENCES employees(`id`) ON DELETE CASCADE,
    CONSTRAINT fk_supervisors_supervisor_id FOREIGN KEY (`supervisor_id`)
        REFERENCES employees(`id`) ON DELETE CASCADE,

    INDEX idx_supervisors_employee_id USING HASH (`employee_id`),
    INDEX idx_supervisors_supervisor_id USING HASH (`supervisor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS contracts (
    `id`             INTEGER      NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `company_id`     INTEGER      NOT NULL,
    `description`    VARCHAR(255) NOT NULL,
    `contract_num`   VARCHAR(50)  NOT NULL,
    `job_code`       VARCHAR(255) NOT NULL,
    `admin`          BOOLEAN      NOT NULL DEFAULT FALSE,
    `active`         BOOLEAN      NOT NULL DEFAULT TRUE,

    CONSTRAINT unique_contract UNIQUE (`contract_num`, `job_code`),

    CONSTRAINT fk_contracts_company_id FOREIGN KEY (`company_id`)
        REFERENCES companies(`id`) ON DELETE CASCADE,

    INDEX idx_contracts_contract_num USING HASH (`contract_num`),
    INDEX idx_contracts_job_code USING HASH (`job_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS contract_assignments (
    `id`             INTEGER      NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `contract_id`    INTEGER      NOT NULL,
    `employee_id`    INTEGER      NOT NULL,
    `labor_cat`      VARCHAR(120) NOT NULL,
    `item_name`      VARCHAR(255) NOT NULL,
    `start`          DATE,
    `end`            DATE,

    CONSTRAINT fk_contract_assignments_contract_id FOREIGN KEY (`contract_id`)
        REFERENCES contracts(`id`) ON DELETE CASCADE,
    CONSTRAINT fk_contract_assignments_employee_id FOREIGN KEY (`employee_id`)
        REFERENCES employees(`id`) ON DELETE CASCADE,

    INDEX idx_contract_assignments_contract_id USING HASH (`contract_id`),
    INDEX idx_contract_assignments_employee_id USING HASH (`employee_id`),
    INDEX idx_contract_assignments_start USING HASH (`start`),
    INDEX idx_contract_assignments_end USING HASH (`end`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS pay_periods (
    `company_id`     INTEGER      NOT NULL,
    `start`          DATE         NOT NULL PRIMARY KEY,
    `end`            DATE         NOT NULL,
    `type`           VARCHAR(20)  NOT NULL,

    CONSTRAINT fk_pay_periods_company_id FOREIGN KEY (`company_id`)
        REFERENCES companies(`id`) ON DELETE CASCADE,

    INDEX idx_pay_periods_end USING HASH (`end`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS bills (
    `id`             INTEGER      NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `assignment_id`  INTEGER      NOT NULL,
    `contract_id`    INTEGER      NOT NULL,
    `employee_id`    INTEGER      NOT NULL,
    `day`            DATE         NOT NULL,
    `hours`          FLOAT        NOT NULL,
    `timestamp`      TIMESTAMP    NOT NULL DEFAULT NOW(),

    CONSTRAINT unique_bill UNIQUE (`assignment_id`, `contract_id`, `employee_id`, `day`),

    CONSTRAINT fk_bill_assignment_id FOREIGN KEY (`assignment_id`)
        REFERENCES contract_assignments(`id`) ON DELETE CASCADE,
    CONSTRAINT fk_bill_contract_id FOREIGN KEY (`contract_id`)
        REFERENCES contracts(`id`) ON DELETE CASCADE,
    CONSTRAINT fk_bill_employee_id FOREIGN KEY (`employee_id`)
        REFERENCES employees(`id`) ON DELETE CASCADE,

    INDEX idx_bills_assignment_id USING HASH (`assignment_id`),
    INDEX idx_bills_contract_id USING HASH (`contract_id`),
    INDEX idx_bills_employee_id USING HASH (`employee_id`),
    INDEX idx_bills_day USING HASH (`day`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS timesheets (
    `id`             INTEGER      NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `employee_id`    INTEGER      NOT NULL,
    `pp_start`       DATE         NOT NULL,
    `completed`      BOOLEAN      NOT NULL DEFAULT FALSE,
    `approved`       BOOLEAN      NOT NULL DEFAULT FALSE,
    `verified`       BOOLEAN      NOT NULL DEFAULT FALSE,
    `exported`       BOOLEAN      NOT NULL DEFAULT FALSE,
    `approved_by`    INTEGER,
    `verified_by`    INTEGER,

    CONSTRAINT unique_timesheet UNIQUE (`employee_id`, `pp_start`),

    CONSTRAINT fk_timesheet_employee_id FOREIGN KEY (`employee_id`)
        REFERENCES employees(`id`) ON DELETE CASCADE,
    CONSTRAINT fk_timesheet_pp_start FOREIGN KEY (`pp_start`)
        REFERENCES pay_periods(`start`) ON DELETE CASCADE,
    CONSTRAINT fk_timesheet_approved_by FOREIGN KEY (`approved_by`)
        REFERENCES employees(`id`) ON DELETE CASCADE,
    CONSTRAINT fk_timesheet_verified_by FOREIGN KEY (`verified_by`)
        REFERENCES employees(`id`) ON DELETE CASCADE,

    INDEX idx_timesheets_employee_id USING HASH (`employee_id`),
    INDEX idx_timesheets_approved_by USING HASH (`approved_by`),
    INDEX idx_timesheets_verified_by USING HASH (`verified_by`),
    INDEX idx_timesheets_pp_start USING HASH (`pp_start`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS audit_logs (
    `timesheet_id`   INTEGER      NOT NULL,
    `log`            LONGTEXT     NOT NULL,
    `timestamp`      TIMESTAMP    NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_audit_log_timesheet_id FOREIGN KEY (`timesheet_id`)
        REFERENCES timesheets(`id`) ON DELETE CASCADE,

    INDEX idx_audit_logs_timesheet_id USING HASH (`timesheet_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS holidays (
    `id`             INTEGER      NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `company_id`     INTEGER      NOT NULL,
    `description`    VARCHAR(100) NOT NULL,
    `config`         VARCHAR(100) NOT NULL,

    CONSTRAINT unique_holiday_desc UNIQUE (`description`),
    CONSTRAINT unique_holiday_config UNIQUE (`config`),

    CONSTRAINT fk_holidays_company_id FOREIGN KEY (`company_id`)
        REFERENCES companies(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

