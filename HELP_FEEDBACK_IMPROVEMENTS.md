# Help & Feedback Design Improvements

## Overview
Enhanced the Help & Feedback section in Navigation Drawer and updated contact information for the open source project.

## Design Improvements

### 1. **Navigation Drawer - Help & Feedback Item**
- **Before**: Simple menu item with outline icon
- **After**: Enhanced card design with:
  - Tertiary container background for better visibility
  - Icon with background circle for visual hierarchy
  - Subtitle "FAQs, Contact & Open Source" for clarity
  - External link indicator (OpenInNew icon)
  - Better touch target and padding

### 2. **Help & Feedback Dialog**
- **Updated Contact Info**:
  - Email: `ductam7415vn@gmail.com`
  - GitHub: `https://github.com/Ductam7415vn/SumUp`
  
- **Added Open Source Section**:
  - Visual card highlighting open source nature
  - "View Source" button - directs to GitHub repo
  - "Star" button - for GitHub stargazers
  - "Made with ❤️ by Duc Tam" attribution

### 3. **Visual Hierarchy**
- Used Material 3 color roles properly:
  - Primary container for contact section
  - Secondary container for open source section
  - Tertiary container for drawer item
- Clear visual separation between sections
- Improved button styling with FilledTonalButton

### 4. **UX Improvements**
- Clear indication that Help & Feedback opens a dialog
- Better organization of information
- Quick access to GitHub for bug reports
- Direct email integration with pre-filled subject

## Design Rationale
1. **Prominence**: Help & Feedback now stands out in the drawer
2. **Transparency**: Open source nature is prominently displayed
3. **Accessibility**: Multiple contact methods available
4. **Community**: Encourages GitHub engagement with star button
5. **Professional**: Clean, organized layout following Material Design

## Technical Changes
- Updated `NavigationDrawer.kt` with enhanced Help item
- Modified `HelpAndFeedbackDialog.kt` with new sections
- Updated contact email and GitHub URLs
- Fixed deprecated icon warnings