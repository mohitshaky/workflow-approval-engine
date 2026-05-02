<databaseChangeLog
        xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog
        http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-3.8.xsd">

    <!-- Core Workflow Definition Table -->
    <changeSet id="1-create-workflow-definitions" author="mohit">
        <createTable tableName="workflow_definitions">
            <column name="id" type="BIGSERIAL" autoIncrement="true">
                <constraints primaryKey="true" nullable="false"/>
            </column>
            <column name="name" type="VARCHAR(255)">
                <constraints nullable="false" unique="true"/>
            </column>
            <column name="description" type="TEXT"/>
            <column name="entity_type" type="VARCHAR(100)">
                <constraints nullable="false"/>
            </column>
            <column name="version" type="INTEGER" defaultValueNumeric="1"/>
            <column name="is_active" type="BOOLEAN" defaultValueBoolean="true"/>
            <column name="created_by" type="BIGINT"/>
            <column name="created_at" type="TIMESTAMP" defaultValueComputed="CURRENT_TIMESTAMP"/>
            <column name="updated_at" type="TIMESTAMP" defaultValueComputed="CURRENT_TIMESTAMP"/>
        </createTable>
    </changeSet>

    <!-- Workflow Steps Configuration -->
    <changeSet id="2-create-workflow-steps" author="mohit">
        <createTable tableName="workflow_steps">
            <column name="id" type="BIGSERIAL" autoIncrement="true">
                <constraints primaryKey="true" nullable="false"/>
            </column>
            <column name="workflow_definition_id" type="BIGINT"/>
            <column name="step_name" type="VARCHAR(255)">
                <constraints nullable="false"/>
            </column>
            <column name="step_order" type="INTEGER">
                <constraints nullable="false"/>
            </column>
            <column name="step_type" type="VARCHAR(50)">
                <constraints nullable="false"/>
            </column>
            <column name="approval_type" type="VARCHAR(50)"/>
            <column name="timeout_hours" type="INTEGER" defaultValueNumeric="24"/>
            <column name="escalation_step_id" type="BIGINT"/>
            <column name="auto_approve_conditions" type="JSON"/>
            <column name="required_roles" type="JSON"/>
            <column name="notification_template" type="VARCHAR(255)"/>
            <column name="created_at" type="TIMESTAMP" defaultValueComputed="CURRENT_TIMESTAMP"/>
        </createTable>
        <addUniqueConstraint
                tableName="workflow_steps"
                columnNames="workflow_definition_id, step_order"
                constraintName="unique_step_order"/>
    </changeSet>

    <!-- Workflow Instances -->
    <changeSet id="3-create-workflow-instances" author="mohit">
        <createTable tableName="workflow_instances">
            <column name="id" type="BIGSERIAL" autoIncrement="true">
                <constraints primaryKey="true" nullable="false"/>
            </column>
            <column name="workflow_definition_id" type="BIGINT"/>
            <column name="entity_id" type="BIGINT">
                <constraints nullable="false"/>
            </column>
            <column name="entity_type" type="VARCHAR(100)">
                <constraints nullable="false"/>
            </column>
            <column name="current_step_id" type="BIGINT"/>
            <column name="status" type="VARCHAR(50)" defaultValue="PENDING"/>
            <column name="priority" type="VARCHAR(20)" defaultValue="MEDIUM"/>
            <column name="initiated_by" type="BIGINT"/>
            <column name="initiated_at" type="TIMESTAMP" defaultValueComputed="CURRENT_TIMESTAMP"/>
            <column name="completed_at" type="TIMESTAMP"/>
            <column name="metadata" type="JSON"/>
        </createTable>
        <addUniqueConstraint
                tableName="workflow_instances"
                columnNames="entity_id, entity_type, workflow_definition_id"
                constraintName="unique_entity_workflow"/>
    </changeSet>

    <!-- Workflow Approvals -->
    <changeSet id="4-create-workflow-approvals" author="mohit">
        <createTable tableName="workflow_approvals">
            <column name="id" type="BIGSERIAL" autoIncrement="true">
                <constraints primaryKey="true" nullable="false"/>
            </column>
            <column name="workflow_instance_id" type="BIGINT"/>
            <column name="workflow_step_id" type="BIGINT"/>
            <column name="approver_id" type="BIGINT"/>
            <column name="status" type="VARCHAR(50)" defaultValue="PENDING"/>
            <column name="comments" type="TEXT"/>
            <column name="approved_at" type="TIMESTAMP"/>
            <column name="escalated_at" type="TIMESTAMP"/>
            <column name="escalated_to" type="BIGINT"/>
            <column name="created_at" type="TIMESTAMP" defaultValueComputed="CURRENT_TIMESTAMP"/>
        </createTable>
        <addUniqueConstraint
                tableName="workflow_approvals"
                columnNames="workflow_instance_id, workflow_step_id, approver_id"
                constraintName="unique_approver_step"/>
    </changeSet>

    <!-- Workflow History -->
    <changeSet id="5-create-workflow-history" author="mohit">
        <createTable tableName="workflow_history">
            <column name="id" type="BIGSERIAL" autoIncrement="true">
                <constraints primaryKey="true" nullable="false"/>
            </column>
            <column name="workflow_instance_id" type="BIGINT"/>
            <column name="action" type="VARCHAR(100)">
                <constraints nullable="false"/>
            </column>
            <column name="actor_id" type="BIGINT"/>
            <column name="from_status" type="VARCHAR(50)"/>
            <column name="to_status" type="VARCHAR(50)"/>
            <column name="step_name" type="VARCHAR(255)"/>
            <column name="comments" type="TEXT"/>
            <column name="metadata" type="JSON"/>
            <column name="created_at" type="TIMESTAMP" defaultValueComputed="CURRENT_TIMESTAMP"/>
        </createTable>
    </changeSet>

    <!-- Approval Rules -->
    <changeSet id="6-create-approval-rules" author="mohit">
        <createTable tableName="approval_rules">
            <column name="id" type="BIGSERIAL" autoIncrement="true">
                <constraints primaryKey="true" nullable="false"/>
            </column>
            <column name="workflow_definition_id" type="BIGINT"/>
            <column name="rule_name" type="VARCHAR(255)">
                <constraints nullable="false"/>
            </column>
            <column name="entity_field" type="VARCHAR(100)"/>
            <column name="operator" type="VARCHAR(20)"/>
            <column name="threshold_value" type="VARCHAR(255)"/>
            <column name="required_approver_role" type="VARCHAR(100)"/>
            <column name="approval_level" type="INTEGER"/>
            <column name="is_active" type="BOOLEAN" defaultValueBoolean="true"/>
            <column name="created_at" type="TIMESTAMP" defaultValueComputed="CURRENT_TIMESTAMP"/>
        </createTable>
    </changeSet>

    <!-- User Workflow Roles -->
    <changeSet id="7-create-user-workflow-roles" author="mohit">
        <createTable tableName="user_workflow_roles">
            <column name="id" type="BIGSERIAL" autoIncrement="true">
                <constraints primaryKey="true" nullable="false"/>
            </column>
            <column name="user_id" type="BIGINT"/>
            <column name="role_name" type="VARCHAR(100)">
                <constraints nullable="false"/>
            </column>
            <column name="entity_type" type="VARCHAR(100)"/>
            <column name="is_active" type="BOOLEAN" defaultValueBoolean="true"/>
            <column name="assigned_by" type="BIGINT"/>
            <column name="assigned_at" type="TIMESTAMP" defaultValueComputed="CURRENT_TIMESTAMP"/>
        </createTable>
        <addUniqueConstraint
                tableName="user_workflow_roles"
                columnNames="user_id, role_name, entity_type"
                constraintName="unique_user_role_entity"/>
    </changeSet>

</databaseChangeLog>
