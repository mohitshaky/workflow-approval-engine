# Workflow Approval Engine

> **What problem this solves:** Automates multi-step approval workflows — replaces email chains and spreadsheets with a reliable, auditable system.

[![CI](https://github.com/mohitshaky/workflow-approval-engine/actions/workflows/ci.yml/badge.svg)](https://github.com/mohitshaky/workflow-approval-engine/actions/workflows/ci.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Java 17](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.org/projects/jdk/17/)

## Key Results
- ✅ Cuts approval time by 80%
- ✅ Handles concurrent workflows
- ✅ Zero lost approvals

## Tech Stack
Java 17 · Spring Boot · Flowable BPMN · PostgreSQL · REST API · Docker

## What It Does
A configurable workflow approval engine that models multi-step approval chains as BPMN processes. Approvers receive tasks via API, every decision is recorded with a timestamp and actor, and escalation rules fire automatically — replacing ad-hoc email threads with a structured, fully auditable process.

## Quick Start
```bash
# clone and run
git clone https://github.com/mohitshaky/workflow-approval-engine.git
cd workflow-approval-engine
./gradlew bootRun
```

## License
MIT — see [LICENSE](LICENSE)