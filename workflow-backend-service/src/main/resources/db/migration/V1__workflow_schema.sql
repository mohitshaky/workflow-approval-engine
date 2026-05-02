-- ============================
-- ENUMS
-- ============================
DO $$ BEGIN
    CREATE TYPE workflow_status AS ENUM ('PENDING','APPROVED','REJECTED','CANCELLED','ESCALATED');
EXCEPTION WHEN duplicate_object THEN null; END $$;

DO $$ BEGIN
    CREATE TYPE workflow_priority AS ENUM ('LOW','MEDIUM','HIGH','CRITICAL');
EXCEPTION WHEN duplicate_object THEN null; END $$;

DO $$ BEGIN
    CREATE TYPE approval_status AS ENUM ('PENDING','APPROVED','REJECTED','ESCALATED');
EXCEPTION WHEN duplicate_object THEN null; END $$;

DO $$ BEGIN
    CREATE TYPE approval_type AS ENUM ('SINGLE','ALL','MAJORITY');
EXCEPTION WHEN duplicate_object THEN null; END $$;

DO $$ BEGIN
    CREATE TYPE step_type AS ENUM ('APPROVAL','NOTIFICATION','SYSTEM_ACTION');
EXCEPTION WHEN duplicate_object THEN null; END $$;

-- ============================
-- TABLES
-- ============================

-- Workflow Definitions
CREATE TABLE IF NOT EXISTS workflow_definitions (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL,
    description TEXT,
    entity_type VARCHAR(255) NOT NULL,
    version INT DEFAULT 1,
    is_active BOOLEAN DEFAULT TRUE,
    created_by VARCHAR(100),
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- Workflow Steps
CREATE TABLE IF NOT EXISTS workflow_steps (
    id BIGSERIAL PRIMARY KEY,
    workflow_definition_id BIGINT REFERENCES workflow_definitions(id) ON DELETE CASCADE,
    step_name VARCHAR(255) NOT NULL,
    step_order INT NOT NULL,
    step_type step_type NOT NULL,
    approval_type approval_type,
    timeout_hours INT,
    escalation_step_id BIGINT,
    auto_approve_conditions JSONB,
    required_roles JSONB,
    notification_template TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    CONSTRAINT uq_workflow_step UNIQUE(workflow_definition_id, step_order)
);

-- Workflow Instances
CREATE TABLE IF NOT EXISTS workflow_instances (
    id BIGSERIAL PRIMARY KEY,
    workflow_definition_id BIGINT REFERENCES workflow_definitions(id) ON DELETE CASCADE,
    entity_id BIGINT NOT NULL,
    entity_type VARCHAR(255) NOT NULL,
    current_step_id BIGINT,
    status workflow_status NOT NULL DEFAULT 'PENDING',
    priority workflow_priority DEFAULT 'MEDIUM',
    initiated_by BIGINT NOT NULL,
    initiated_at TIMESTAMPTZ DEFAULT NOW(),
    completed_at TIMESTAMPTZ,
    metadata JSONB
);

-- Workflow Tasks
CREATE TABLE IF NOT EXISTS workflow_tasks (
    id BIGSERIAL PRIMARY KEY,
    instance_id BIGINT REFERENCES workflow_instances(id) ON DELETE CASCADE,
    step_id BIGINT REFERENCES workflow_steps(id) ON DELETE SET NULL,
    assigned_user VARCHAR(255),
    status VARCHAR(50) NOT NULL,
    started_at TIMESTAMPTZ,
    completed_at TIMESTAMPTZ,
    due_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- Workflow Approvals
CREATE TABLE IF NOT EXISTS workflow_approvals (
    id BIGSERIAL PRIMARY KEY,
    workflow_instance_id BIGINT REFERENCES workflow_instances(id) ON DELETE CASCADE,
    workflow_step_id BIGINT REFERENCES workflow_steps(id) ON DELETE CASCADE,
    approver_id BIGINT NOT NULL,
    status approval_status DEFAULT 'PENDING',
    comments TEXT,
    approved_at TIMESTAMPTZ,
    escalated_at TIMESTAMPTZ,
    escalated_to BIGINT,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- Workflow History
CREATE TABLE IF NOT EXISTS workflow_history (
    id BIGSERIAL PRIMARY KEY,
    workflow_instance_id BIGINT REFERENCES workflow_instances(id) ON DELETE CASCADE,
    action VARCHAR(255) NOT NULL,
    actor_id BIGINT,
    from_status VARCHAR(50),
    to_status VARCHAR(50),
    step_name VARCHAR(255),
    comments TEXT,
    metadata JSONB,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- Approval Rules
CREATE TABLE IF NOT EXISTS approval_rules (
    id BIGSERIAL PRIMARY KEY,
    workflow_definition_id BIGINT REFERENCES workflow_definitions(id) ON DELETE CASCADE,
    rule_name VARCHAR(255) NOT NULL,
    entity_field VARCHAR(255),
    operator VARCHAR(50),
    threshold_value VARCHAR(255),
    required_approver_role VARCHAR(100),
    approval_level INT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- User Workflow Roles
CREATE TABLE IF NOT EXISTS user_workflow_roles (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_name VARCHAR(100) NOT NULL,
    entity_type VARCHAR(100) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    assigned_by BIGINT,
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================
-- SAMPLE TEST DATA
-- ============================

-- Workflow definition
INSERT INTO workflow_definitions (name, description, entity_type, created_by)
VALUES ('Leave Approval Workflow', 'Approval process for employee leave requests', 'LEAVE_REQUEST', 'admin')
ON CONFLICT (name) DO NOTHING;

-- Steps
INSERT INTO workflow_steps (workflow_definition_id, step_name, step_order, step_type, approval_type, required_roles)
SELECT id, 'Manager Approval', 1, 'APPROVAL', 'SINGLE', '["MANAGER"]'::jsonb
FROM workflow_definitions WHERE name='Leave Approval Workflow'
ON CONFLICT DO NOTHING;

INSERT INTO workflow_steps (workflow_definition_id, step_name, step_order, step_type, approval_type, required_roles)
SELECT id, 'HR Approval', 2, 'APPROVAL', 'SINGLE', '["HR"]'::jsonb
FROM workflow_definitions WHERE name='Leave Approval Workflow'
ON CONFLICT DO NOTHING;

INSERT INTO workflow_steps (workflow_definition_id, step_name, step_order, step_type)
SELECT id, 'Final Notification', 3, 'NOTIFICATION'
FROM workflow_definitions WHERE name='Leave Approval Workflow'
ON CONFLICT DO NOTHING;

-- Instance
INSERT INTO workflow_instances (workflow_definition_id, entity_id, entity_type, initiated_by, status, priority)
SELECT id, 1001, 'LEAVE_REQUEST', 501, 'PENDING', 'MEDIUM'
FROM workflow_definitions WHERE name='Leave Approval Workflow'
ON CONFLICT DO NOTHING;

-- Task
INSERT INTO workflow_tasks (instance_id, step_id, assigned_user, status, started_at, due_at)
SELECT wi.id, ws.id, 'manager1', 'PENDING', NOW(), NOW() + INTERVAL '2 days'
FROM workflow_instances wi
JOIN workflow_steps ws ON wi.workflow_definition_id = ws.workflow_definition_id
WHERE ws.step_order = 1
LIMIT 1;

-- Approval
INSERT INTO workflow_approvals (workflow_instance_id, workflow_step_id, approver_id, status)
SELECT wi.id, ws.id, 501, 'PENDING'
FROM workflow_instances wi
JOIN workflow_steps ws ON wi.workflow_definition_id = ws.workflow_definition_id
WHERE ws.step_order = 1
LIMIT 1;

-- Approval Rule
INSERT INTO approval_rules (workflow_definition_id, rule_name, entity_field, operator, threshold_value, required_approver_role, approval_level)
SELECT id, 'High Leave Days Escalation', 'leave_days', '>', '10', 'HR_HEAD', 2
FROM workflow_definitions WHERE name='Leave Approval Workflow'
ON CONFLICT DO NOTHING;

-- History
INSERT INTO workflow_history (workflow_instance_id, action, actor_id, from_status, to_status, step_name, comments)
SELECT id, 'CREATED', 501, NULL, 'PENDING', 'Manager Approval', 'Workflow started'
FROM workflow_instances
LIMIT 1;

-- User roles
INSERT INTO user_workflow_roles (user_id, role_name, entity_type, assigned_by)
VALUES
(101, 'MANAGER', 'LEAVE_REQUEST', 1),
(102, 'REVIEWER', 'INVOICE', 1),
(103, 'APPROVER', 'PURCHASE_ORDER', 1),
(104, 'ADMIN', 'INVOICE', 1)
ON CONFLICT DO NOTHING;
