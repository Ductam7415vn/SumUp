# TÀI LIỆU 5: KẾ HOẠCH DỰ ÁN & QUẢN LÝ THAY ĐỔI
## SUMUP - ỨNG DỤNG TÓM TẮT VĂN BẢN THÔNG MINH

---

## 1. KẾ HOẠCH DỰ ÁN (LỊCH TRÌNH THỰC HIỆN)

### 1.1. Timeline Tổng quan

**Thời gian**: 3 tháng (12 tuần)
**Team size**: 5 thành viên
**Methodology**: Agile/Scrum (2-week sprints)

```
Month 1: Planning & Foundation
├── Week 1-2: Sprint 1 - Setup & Architecture
└── Week 3-4: Sprint 2 - Core Features (Text Summarization)

Month 2: Feature Development
├── Week 5-6: Sprint 3 - Document Processing (PDF, DOCX)
└── Week 7-8: Sprint 4 - OCR & Advanced Features

Month 3: Polish & Release
├── Week  9-10: Sprint 5 - Testing & Bug Fixes
└── Week 11-12: Sprint 6 - Final Polish & Deployment
```

### 1.2. Sprint Planning

#### **Sprint 1: Foundation (Week 1-2)**
**Goal**: Setup project, architecture, core infrastructure

**Tasks:**
| Task ID | Task | Assignee | Estimate | Status |
|---------|------|----------|----------|--------|
| T-001 | Project setup & Gradle config | Backend | 1d | ✅ Done |
| T-002 | Clean Architecture skeleton | Backend | 2d | ✅ Done |
| T-003 | Hilt DI setup | Backend | 1d | ✅ Done |
| T-004 | Room Database schema | Backend | 2d | ✅ Done |
| T-005 | Material 3 theme setup | Frontend | 2d | ✅ Done |
| T-006 | Navigation structure | Frontend | 2d | ✅ Done |
| T-007 | Figma design system | UI/UX | 3d | ✅ Done |
| T-008 | API integration POC | Backend | 3d | ✅ Done |

**Sprint Review**: All tasks completed ✅
**Velocity**: 16 story points

#### **Sprint 2: Core Features (Week 3-4)**
**Goal**: Text summarization functionality

| Task ID | Task | Assignee | Estimate | Status |
|---------|------|----------|----------|--------|
| T-010 | Gemini API integration | Backend | 3d | ✅ Done |
| T-011 | SummarizeTextUseCase | Backend | 2d | ✅ Done |
| T-012 | Main Screen UI | Frontend | 3d | ✅ Done |
| T-013 | Result Screen UI | Frontend | 2d | ✅ Done |
| T-014 | Processing screen animation | Frontend | 2d | ✅ Done |
| T-015 | API key management | Backend | 2d | ✅ Done |
| T-016 | Error handling | Backend | 1d | ✅ Done |
| T-017 | Unit tests | Tester | 2d | ✅ Done |

**Sprint Review**: 100% completion ✅
**Velocity**: 17 story points

#### **Sprint 3: Document Processing (Week 5-6)**
**Goal**: PDF & DOCX support

| Task ID | Task | Assignee | Estimate | Status |
|---------|------|----------|----------|--------|
| T-020 | PDFBox integration | Backend | 3d | ✅ Done |
| T-021 | DOCX processor (Mammoth) | Backend | 2d | ✅ Done |
| T-022 | File upload UI | Frontend | 2d | ✅ Done |
| T-023 | Large PDF handling | Backend | 3d | ✅ Done |
| T-024 | Smart sectioning | Backend | 2d | ✅ Done |
| T-025 | Progress tracking | Frontend | 2d | ✅ Done |
| T-026 | DocumentProcessorFactory | Backend | 1d | ✅ Done |
| T-027 | Integration tests | Tester | 2d | ✅ Done |

**Sprint Review**: All completed ✅
**Velocity**: 17 story points
**Challenges**: PDFBox compatibility issues resolved

#### **Sprint 4: OCR & Advanced (Week 7-8)**
**Goal**: Camera OCR, History, Settings

| Task ID | Task | Assignee | Estimate | Status |
|---------|------|----------|----------|--------|
| T-030 | ML Kit OCR integration | Backend | 3d | ✅ Done |
| T-031 | Camera UI (CameraX) | Frontend | 3d | ✅ Done |
| T-032 | History screen | Frontend | 2d | ✅ Done |
| T-033 | Search & filter | Backend | 2d | ✅ Done |
| T-034 | Settings screen | Frontend | 2d | ✅ Done |
| T-035 | Theme switching | Frontend | 1d | ✅ Done |
| T-036 | Draft auto-save | Backend | 2d | ✅ Done |
| T-037 | UI tests (Compose) | Tester | 2d | ✅ Done |

**Sprint Review**: Completed ✅
**Velocity**: 17 story points

#### **Sprint 5: Testing & Polish (Week 9-10)**
**Goal**: Bug fixes, testing, optimization

| Task ID | Task | Assignee | Estimate | Status |
|---------|------|----------|----------|--------|
| T-040 | Comprehensive testing | Tester | 5d | ✅ Done |
| T-041 | Bug fixing | All | 3d | ✅ Done |
| T-042 | Performance optimization | Backend | 2d | ✅ Done |
| T-043 | Accessibility improvements | Frontend | 2d | ⏳ In Progress |
| T-044 | Animation polish | Frontend | 1d | ✅ Done |
| T-045 | Documentation update | BA | 2d | ✅ Done |

**Sprint Review**: 90% completed
**Blockers**: Accessibility features pending
**Velocity**: 15 story points

#### **Sprint 6: Release (Week 11-12)**
**Goal**: Final prep, deployment, documentation

| Task ID | Task | Assignee | Estimate | Status |
|---------|------|----------|----------|--------|
| T-050 | ProGuard rules finalize | Backend | 1d | ✅ Done |
| T-051 | Release APK generation | Backend | 1d | ✅ Done |
| T-052 | User manual | BA | 2d | ✅ Done |
| T-053 | Demo preparation | PM | 2d | ✅ Done |
| T-054 | Final regression testing | Tester | 3d | ✅ Done |
| T-055 | Deploy to Play Store (beta) | PM | 1d | 🚀 Ready |

**Sprint Review**: Release ready! ✅
**Velocity**: 10 story points (lighter for release)

### 1.3. Milestone Tracking

| Milestone | Target Date | Status | Deliverables |
|-----------|-------------|--------|--------------|
| M1: Project Setup | Week 2 | ✅ Done | Architecture, DB, DI |
| M2: Core MVP | Week 4 | ✅ Done | Text summarization working |
| M3: Multi-format | Week 6 | ✅ Done | PDF, DOCX support |
| M4: Feature Complete | Week 8 | ✅ Done | All features implemented |
| M5: Beta Release | Week 10 | ✅ Done | Bug-free beta version |
| M6: Production | Week 12 | 🚀 Ready | v1.0.3 release |

---

## 2. QUẢN LÝ TIẾN ĐỘ

### 2.1. Burn Down Chart

**Sprint 5 Burn Down (Current):**
```
Story Points
    20│
      │  ╲
    15│   ╲___
      │       ╲___
    10│           ╲___
      │               ╲___ Ideal
     5│                   ╲___
      │  Actual → → → → → → →╲___
     0└────────────────────────────╲
       Mon  Tue  Wed  Thu  Fri  Mon  Tue
       Day 1   Day 3   Day 5   Day 7   Day 10
```

**Observations:**
- On track with ideal burn down
- No major blockers
- Velocity consistent at ~17 SP/sprint

### 2.2. Cumulative Flow Diagram

```
Tasks
  50│                        ╱Done
    │                   ╱╱╱╱
  40│              ╱╱╱╱
    │         ╱╱╱╱         In Progress
  30│    ╱╱╱╱
    │╱╱╱╱                  To Do
  20│
    │
  10│
    │
   0└────────────────────────────────
     Sprint1  Sprint2  Sprint3  Sprint4  Sprint5  Sprint6
```

**Current Status:**
- To Do: 8 tasks
- In Progress: 3 tasks
- Done: 92 tasks
- **Overall Progress**: 90%

### 2.3. Team Velocity

| Sprint | Planned | Completed | Velocity |
|--------|---------|-----------|----------|
| Sprint 1 | 16 SP | 16 SP | 100% |
| Sprint 2 | 17 SP | 17 SP | 100% |
| Sprint 3 | 17 SP | 17 SP | 100% |
| Sprint 4 | 17 SP | 17 SP | 100% |
| Sprint 5 | 17 SP | 15 SP | 88% |
| Sprint 6 | 10 SP | 10 SP | 100% (projected) |

**Average Velocity**: 16.5 story points/sprint
**Consistency**: Excellent (90%+ completion rate)

---

## 3. QUẢN LÝ THAY ĐỔI (CHANGE MANAGEMENT)

### 3.1. Change Request Process

```
┌─────────────────┐
│  Change Request │
│   Submitted     │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│   Impact        │
│   Analysis      │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│   Approval      │
│   (PM + Team)   │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  Implementation │
│   & Testing     │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│   Deployment    │
│   & Review      │
└─────────────────┘
```

### 3.2. Change Log (Yêu cầu thay đổi)

#### **CR-001: Add Streaming Summaries** ✅ Completed
```yaml
Request Date: Week 5
Requested By: Product Owner
Priority: High
Impact: Medium

Description:
  Real-time streaming của summary text thay vì chờ hoàn thành

Analysis:
  - Effort: 3 days
  - Risk: Low (API supports streaming)
  - Dependencies: None

Approval: ✅ Approved (Week 5)
Implementation: Sprint 4
Status: ✅ Deployed in v1.0.2

Benefits:
  - Better UX (progressive display)
  - Faster perceived performance
  - Modern AI experience
```

#### **CR-002: Large PDF Strategy Selection** ✅ Completed
```yaml
Request Date: Week 6
Requested By: Tester (from user feedback)
Priority: High
Impact: High

Description:
  Cho phép user chọn cách xử lý PDF lớn:
  - Process All pages
  - Select specific pages
  - Cancel

Analysis:
  - Effort: 2 days
  - Risk: Medium (UI/UX changes)
  - Dependencies: PDF processor refactor

Approval: ✅ Approved (Week 6)
Implementation: Sprint 4
Status: ✅ Deployed in v1.0.3

Benefits:
  - User control over processing
  - Reduced API costs
  - Better UX for large documents
```

#### **CR-003: Multi-language Support** ⏳ Planned
```yaml
Request Date: Week 8
Requested By: Stakeholder
Priority: Medium
Impact: High

Description:
  Mở rộng hỗ trợ từ EN/VI sang thêm 5 ngôn ngữ:
  - Japanese, Korean, Chinese, French, Spanish

Analysis:
  - Effort: 2 weeks
  - Risk: Medium (i18n complexity)
  - Dependencies: Translation resources

Approval: ⏳ Pending (scheduled for v1.1.0)
Implementation: Sprint 8 (next release)
Status: Backlog

Estimated Impact:
  - 30% more users (international)
  - Increased app complexity
  - Need translator resources
```

#### **CR-004: Voice Input** 📋 Proposed
```yaml
Request Date: Week 10
Requested By: User feedback
Priority: Low
Impact: Medium

Description:
  Thêm khả năng nhập liệu bằng giọng nói
  - Speech-to-text
  - Tích hợp với Gemini voice API

Analysis:
  - Effort: 1 week
  - Risk: Low
  - Dependencies: Google Speech API

Approval: 📋 Under Review
Status: Proposed for v1.2.0
```

### 3.3. Change Impact Matrix

| Change | Scope | Timeline | Cost | Risk | Decision |
|--------|-------|----------|------|------|----------|
| CR-001 Streaming | Medium | 3d | Low | Low | ✅ Approved |
| CR-002 PDF Strategy | High | 2d | Low | Medium | ✅ Approved |
| CR-003 Multi-lang | High | 10d | High | Medium | ⏳ v1.1.0 |
| CR-004 Voice Input | Medium | 5d | Medium | Low | 📋 Review |
| CR-005 Cloud Sync | Very High | 20d | High | High | 🚫 Rejected |

### 3.4. Version Control

**Current Version**: v1.0.3
**Release History**:

```
v1.0.3 (Current) - January 15, 2025
  ✅ Large PDF handling with user options
  ✅ Streaming summaries
  ✅ Enhanced error handling
  ✅ Performance optimizations
  🐛 Fixed 8 bugs

v1.0.2 - December 20, 2024
  ✅ Real-time streaming
  ✅ Improved processing animations
  ✅ Better offline caching
  🐛 Fixed 5 bugs

v1.0.1 - November 30, 2024
  ✅ Initial feature set
  ✅ Text, PDF, DOCX, OCR support
  ✅ 6 AI personas
  ✅ History & Settings
  🐛 Fixed 12 bugs from beta

v1.0.0-beta - November 1, 2024
  ✅ Beta release
  ✅ Core features working
  ⚠️ Known issues tracked
```

**Planned Releases**:
```
v1.1.0 - March 2025 (Q1)
  - Multi-language support (5 languages)
  - Widget support
  - Advanced export options
  - Performance improvements

v1.2.0 - June 2025 (Q2)
  - Voice input
  - Collaborative features
  - Premium tier features
  - iOS version (parallel)

v2.0.0 - December 2025 (Q4)
  - Cloud sync
  - Web dashboard
  - API for developers
  - Advanced AI models
```

---

## 4. QUẢN LÝ RỦI RO

### 4.1. Risk Register

| Risk ID | Risk | Probability | Impact | Mitigation | Owner |
|---------|------|-------------|--------|------------|-------|
| R-001 | API rate limits exceeded | Medium | High | Implement local caching, queue | Backend |
| R-002 | Large PDF performance | Low | High | ✅ Mitigated: Sectioning implemented | Backend |
| R-003 | Security breach (API keys) | Low | Critical | ✅ Mitigated: Encryption implemented | Backend |
| R-004 | OCR accuracy issues | Medium | Medium | Use ML Kit high-accuracy mode | Backend |
| R-005 | App size too large | Low | Low | Monitor, ProGuard optimization | Backend |
| R-006 | Device compatibility | Medium | Medium | Test on multiple devices | Tester |
| R-007 | Network dependency | High | Medium | ✅ Mitigated: Offline mode | Backend |

### 4.2. Risk Response

**R-001: API Rate Limits** (Mitigated)
```
Issue: Gemini API có giới hạn 60 requests/minute

Solution Implemented:
  1. Request queueing với backoff
  2. Local caching cho repeated requests
  3. Usage tracking dashboard
  4. User notification at 80% quota

Status: ✅ Monitoring shows <40 req/min average
```

**R-003: Security** (Mitigated)
```
Issue: API keys có thể bị đánh cắp

Solution Implemented:
  1. AndroidX Security Crypto (AES256-GCM)
  2. Certificate pinning (production)
  3. No BuildConfig storage
  4. ProGuard obfuscation

Status: ✅ Security audit passed
```

---

## 5. RESOURCE MANAGEMENT

### 5.1. Team Allocation

**Current Sprint (Sprint 5):**

| Member | Role | Allocation | Current Tasks |
|--------|------|------------|---------------|
| Alice | PM | 100% | Sprint planning, stakeholder mgmt |
| Bob | Backend Dev | 100% | Performance optimization, bug fixes |
| Carol | Frontend Dev | 100% | Accessibility features, UI polish |
| David | UI/UX | 50% | Animation refinement, user testing |
| Eve | Tester | 100% | Comprehensive testing, automation |

**Total Team Hours**: 40 hrs/week × 5 members = 200 hrs/week

### 5.2. Budget Tracking

**Total Budget**: $50,000 (3 months)
**Spent**: $42,000 (84%)
**Remaining**: $8,000 (16%)

| Category | Budgeted | Actual | Variance |
|----------|----------|--------|----------|
| Development | $30,000 | $28,000 | -$2,000 ✅ |
| Design | $8,000 | $7,500 | -$500 ✅ |
| Testing | $5,000 | $4,000 | -$1,000 ✅ |
| Infrastructure | $3,000 | $2,000 | -$1,000 ✅ |
| Contingency | $4,000 | $500 | -$3,500 ✅ |

**Status**: Under budget, on schedule ✅

### 5.3. Tool Stack Costs

| Tool | Monthly Cost | Annual | Usage |
|------|--------------|--------|-------|
| Figma | $15/user | $180 | UI/UX Design |
| GitHub | Free | $0 | Version control |
| Jira | $10/user | $120 | Project management |
| Firebase | $25/month | $300 | Analytics, Crashlytics |
| Google Cloud | $20/month | $240 | Gemini API, Storage |
| **Total** | **$70/month** | **$840/year** | - |

---

## 6. STAKEHOLDER MANAGEMENT

### 6.1. Stakeholder Register

| Stakeholder | Role | Interest | Influence | Strategy |
|-------------|------|----------|-----------|----------|
| Product Owner | Decision maker | High | High | Manage Closely |
| Development Team | Executors | High | Medium | Keep Informed |
| End Users | Beneficiaries | High | Low | Keep Satisfied |
| Academic Reviewer | Evaluator | Medium | High | Manage Closely |
| Investors | Funders | Medium | High | Keep Informed |

### 6.2. Communication Plan

**Daily:**
- Stand-up meeting (15 min, 9:30 AM)
- Slack updates

**Weekly:**
- Sprint planning (Monday, 2 hrs)
- Sprint review (Friday, 1 hr)
- Sprint retrospective (Friday, 1 hr)

**Bi-weekly:**
- Stakeholder demo (Friday, 30 min)
- Progress report email

**Monthly:**
- Executive summary report
- Budget review meeting

### 6.3. Decision Log

| Date | Decision | Made By | Rationale | Impact |
|------|----------|---------|-----------|--------|
| Nov 1 | Use Kotlin + Compose | Team | Modern stack, less boilerplate | Positive |
| Nov 5 | Clean Architecture | Backend Lead | Maintainability, testability | High |
| Nov 10 | Gemini 1.5 Flash | PM | Cost-effective, fast | Medium |
| Dec 1 | Add streaming | PO | Better UX | Positive |
| Dec 15 | PDF sectioning | Backend | Performance for large docs | High |
| Jan 10 | Multi-flavor builds | Backend | Testing flexibility | Medium |

---

## PHỤ LỤC

### A. Project Charter

**Project Name**: SumUp - AI Text Summarization App
**Start Date**: November 1, 2024
**Target End Date**: January 31, 2025
**Budget**: $50,000
**Team Size**: 5 members

**Objectives:**
1. ✅ Develop Android app with AI summarization
2. ✅ Support text, PDF, DOCX, OCR inputs
3. ✅ Achieve 45%+ test coverage
4. ✅ Release v1.0.3 by January 15, 2025

**Success Criteria:**
- ✅ All core features working
- ✅ <2s app startup time
- ✅ >95% crash-free rate
- ✅ Positive user feedback (>4.0★)

### B. Lessons Learned

**What Went Well:**
- ✅ Clean Architecture paid off (easy to maintain)
- ✅ Agile sprints kept us on track
- ✅ Hilt DI simplified dependency management
- ✅ Figma-to-Compose workflow efficient
- ✅ Team communication excellent

**Challenges:**
- ⚠️ PDFBox compatibility issues (solved with Android fork)
- ⚠️ API rate limits required creative solutions
- ⚠️ OCR accuracy in low light (ongoing)
- ⚠️ Test coverage lower than target (improving)

**For Next Time:**
- Start UI testing earlier
- More buffer for third-party library issues
- Invest in test automation from day 1
- Consider iOS version in parallel

### C. Release Checklist

**Pre-Release (v1.0.3):**
- [x] All critical bugs fixed
- [x] Performance benchmarks met
- [x] Security audit passed
- [x] Documentation complete
- [x] User manual ready
- [x] Demo prepared
- [x] APK signed & tested
- [x] Play Store listing ready
- [x] Marketing materials prepared

**Post-Release:**
- [ ] Monitor crash reports (Firebase)
- [ ] Track user feedback
- [ ] Plan v1.1.0 features
- [ ] Update roadmap
- [ ] Team retrospective

---

**Ngày tạo**: Ngày hiện tại
**Phiên bản**: 1.0
**Project Manager**: Team SumUp PM
**Status**: ✅ On Track for v1.0.3 Release
**Next Review**: Post-release retrospective
