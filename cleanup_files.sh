#!/bin/bash

# Script to clean up unnecessary documentation files
# Run with: bash cleanup_files.sh

echo "Starting cleanup of unnecessary files..."

# Navigate to project root
cd /Users/ductampro/AndroidStudioProjects/SumUp

# Delete demo files
echo "Removing demo files..."
rm -f DEMO_PRESENTATION_GUIDE.md
rm -f DEMO_SCENARIOS_DETAILED.md
rm -f SUMUP_TESTING_DEMO_PLAN.md

# Delete test and debug files
echo "Removing test and debug files..."
rm -f QUICK_TEST_SCRIPT.md
rm -f QUICK_TEST_GUIDE.md
rm -f TEST_CASES.md
rm -f DEBUG_GUIDE.md

# Delete build guides
echo "Removing build guide files..."
rm -f ANDROID_STUDIO_BUILD_GUIDE.md
rm -f BUILD_MULTIPLE_VERSIONS.md
rm -f FIX_OUT_OF_MEMORY.md
rm -f MULTIPLE_FLAVORS_SOLUTION.md

# Delete old analysis files
echo "Removing old analysis files..."
rm -f GHOST_FEATURES_ANALYSIS.md
rm -f SUMUP_ACTUAL_IMPLEMENTATION_ANALYSIS.md

# Delete implementation planning files
echo "Removing implementation planning files..."
rm -f SEQUENTIAL_TOOLTIP_IMPLEMENTATION.md
rm -f WELCOME_CARD_IMPLEMENTATION.md
rm -f HELP_DIALOG_REDESIGN.md
rm -f HELP_FEEDBACK_IMPROVEMENTS.md
rm -f LARGE_DOCUMENT_PROCESSING_PLAN.md

# Navigate to docs folder
cd docs

# Delete old academic structure files
echo "Removing old academic structure files..."
rm -f 01-academic-project-overview.md
rm -f 04-ui-ux-analysis.md
rm -f 13-academic-presentation-guide.md
rm -f 14-comprehensive-summary.md
rm -f academic-evaluation-checklist.md
rm -f academic-project-summary.md
rm -f academic-technical-spec.md

# Delete duplicate folders
echo "Removing duplicate folders..."
rm -rf academic
rm -rf ARCHIVE

# Delete old planning docs
echo "Removing old planning docs..."
rm -f CONTINUATION_PROMPT.md
rm -f DOCUMENTATION_REORGANIZATION.md

# Delete UI improvement tracking files
echo "Removing UI improvement tracking files..."
rm -f UI-UX-ANALYSIS-REPORT.md
rm -f UI-UX-IMPROVEMENTS-SUMMARY.md
rm -f UI_UX_IMPROVEMENTS_IMPLEMENTED.md
rm -f UX_IMPROVEMENT_PLAN.md

# Delete old implementation plans
echo "Removing old implementation plans..."
rm -f ocr-implementation-plan.md
rm -f result-screen-redesign-plan.md
rm -f settings-screen-redesign-plan.md

# Delete processing flow analysis
echo "Removing processing flow analysis files..."
rm -f PROCESSING_FLOW_OPTIMIZED.md
rm -f PROCESSING_SCREEN_ANALYSIS.md
rm -f PROGRESS_BAR_COMPLETE_FLOW.md
rm -f PROGRESS_BAR_FLOW_ANALYSIS.md

# More cleanup
rm -f complete-source-code-analysis.md
rm -f package-cleanup-summary.md
rm -f result-screen-implementation-summary.md
rm -f result-screen-phase2-summary.md
rm -f dialog-ui-ux-evaluation.md
rm -f app-flows-analysis.md
rm -f haptic-ripple-guide.md
rm -f executive-summary.md

# Additional files
rm -f SUMUP_COMPREHENSIVE_PROJECT_REPORT.md
rm -f TOP_BAR_CONTEXT_UPDATE.md
rm -f TOP_BAR_REDESIGN_PROPOSAL.md
rm -f UI_SPACING_IMPROVEMENTS.md
rm -f TEXT_LIMIT_INCREASE.md
rm -f SUMMARY_LENGTH_SOLUTIONS_ANALYSIS.md
rm -f PROFESSIONAL_SUMMARY_REDESIGN.md
rm -f PDF_OCR_FLOW_IMPROVEMENTS.md
rm -f OCR_TESTING_GUIDE.md
rm -f NEW_APP_ICON_DESIGN.md
rm -f NAVIGATION_DRAWER_DESIGN_SPEC.md
rm -f LARGE_PDF_HANDLING_SOLUTION.md
rm -f LARGE_PDF_HANDLING_PROPOSAL.md
rm -f FIREBASE_PROJECT_SETUP_DETAILED.md
rm -f FEATURE_DISCOVERY_UPGRADE.md
rm -f CHUCKER_USAGE.md
rm -f APP_ICON_DESIGN_GUIDE.md
rm -f API_KEY_MIGRATION_GUIDE.md
rm -f PRODUCTION_UPGRADE_SUMMARY.md

# Delete old BAO_CAO_DO_AN files (if they exist)
cd BAO_CAO_DO_AN
echo "Cleaning up old report structure..."
rm -f 01_GIOI_THIEU_DE_TAI.md
rm -f 02_PHAN_TICH_YEU_CAU.md
rm -f 03_KHAO_SAT_HIEN_TRANG.md
rm -f 04_KIEN_TRUC_HE_THONG.md
rm -f 05_THIET_KE_CO_SO_DU_LIEU.md
rm -f 06_THIET_KE_GIAO_DIEN.md
rm -f 07_CONG_NGHE_CONG_CU.md
rm -f 08_CHI_TIET_TRIEN_KHAI.md
rm -f 09_TICH_HOP_AI.md
rm -f 10_KE_HOACH_KIEM_THU.md
rm -f 11_KET_QUA_DANH_GIA.md
rm -f 12_KET_LUAN.md
rm -f 13_DEMO_SAN_PHAM.md
rm -f TONG_KET_BAO_CAO.md
rm -f 00_BIA_VA_MUC_LUC.md
rm -f DEMO_EXPANDED.md

# Keep only the 3-chapter structure
echo "Keeping only 3-chapter structure files..."
echo "Files kept:"
ls -la *.md

# Return to project root
cd /Users/ductampro/AndroidStudioProjects/SumUp

echo ""
echo "Cleanup completed!"
echo ""
echo "Essential files kept:"
echo "- README.md"
echo "- CLAUDE.md"
echo "- CHANGELOG.md"
echo "- LICENSE"
echo "- CONTRIBUTING.md"
echo "- CODE_OF_CONDUCT.md"
echo "- GEMINI_API_SETUP.md"
echo "- GEMINI_API_LIMITS.md"
echo "- Report files in docs/BAO_CAO_DO_AN/ (3-chapter structure)"
echo ""
echo "To see remaining MD files:"
echo "find . -name '*.md' -type f | grep -v node_modules | sort"