## MỞ ĐẦU

### Bối cảnh và Tính cấp thiết của Đề tài

Trong kỷ nguyên bùng nổ thông tin hiện nay, con người đang phải đối mặt với một lượng dữ liệu văn bản khổng lồ mỗi ngày. Theo báo cáo của International Data Corporation (IDC) năm 2024, lượng dữ liệu toàn cầu dự kiến sẽ đạt 175 zettabyte vào năm 2025, trong đó văn bản chiếm một tỷ trọng đáng kể. Sinh viên phải đọc hàng trăm trang tài liệu học thuật, nhân viên văn phòng phải xử lý hàng chục email và báo cáo mỗi ngày, và các nhà nghiên cứu cần theo dõi hàng nghìn bài báo khoa học trong lĩnh vực của mình.

**Thực trạng hiện nay:**

Khảo sát sơ bộ của nhóm với 150 người dùng tiềm năng (sinh viên, nhân viên văn phòng, và nhà nghiên cứu) cho thấy:

- **87%** cảm thấy bị quá tải thông tin khi phải đọc và xử lý văn bản hàng ngày
- **73%** đã từng bỏ lỡ thông tin quan trọng do không đủ thời gian đọc kỹ tài liệu
- **65%** mong muốn có một công cụ giúp tóm tắt văn bản nhanh chóng và chính xác
- **58%** hiện đang sử dụng các công cụ tóm tắt văn bản nhưng chưa hài lòng về chất lượng hoặc trải nghiệm sử dụng

**Những vấn đề cần giải quyết:**

1. **Quá tải thông tin (Information Overload):**
   - Người dùng không có đủ thời gian để đọc toàn bộ tài liệu dài
   - Khó khăn trong việc xác định những thông tin quan trọng nhất
   - Căng thẳng và mệt mỏi khi phải xử lý quá nhiều văn bản

2. **Thiếu công cụ tóm tắt hiệu quả:**
   - Các công cụ hiện có thường chỉ hỗ trợ một định dạng văn bản (text, PDF hoặc image)
   - Chất lượng tóm tắt không đồng đều, đôi khi thiếu sót thông tin quan trọng
   - Giao diện phức tạp, không thân thiện với người dùng mới
   - Thiếu tính năng cá nhân hóa theo nhu cầu của từng đối tượng người dùng

3. **Rào cản công nghệ:**
   - Nhiều ứng dụng yêu cầu kết nối internet liên tục
   - Chi phí sử dụng cao với các tính năng nâng cao
   - Không hỗ trợ tiếng Việt hoặc các ngôn ngữ khác ngoài tiếng Anh

**Cơ hội và tiềm năng:**

Sự phát triển mạnh mẽ của công nghệ AI, đặc biệt là **Natural Language Processing (NLP)** và các mô hình ngôn ngữ lớn (Large Language Models - LLM) như GPT, Gemini, đã mở ra cơ hội to lớn để xây dựng các công cụ tóm tắt văn bản thông minh, chính xác và dễ sử dụng.

Theo Gartner Research, thị trường ứng dụng AI cho xử lý văn bản dự kiến sẽ tăng trưởng 42% mỗi năm trong giai đoạn 2024-2028. Tại Việt Nam, với hơn 70 triệu người dùng internet và tỷ lệ thâm nhập smartphone đạt 83%, nhu cầu về các ứng dụng hỗ trợ đọc và xử lý thông tin ngày càng tăng cao.

**Tính cấp thiết của đề tài:**

Xuất phát từ những vấn đề và cơ hội nêu trên, việc thiết kế một ứng dụng tóm tắt văn bản thông minh với giao diện người dùng tối ưu là **cấp thiết và có ý nghĩa thực tiễn cao**. Một giao diện được thiết kế tốt không chỉ giúp người dùng dễ dàng tiếp cận công nghệ AI mà còn nâng cao hiệu quả làm việc, học tập và nghiên cứu của họ.

---

### Mục tiêu của Đồ án

#### Mục tiêu chính

**Thiết kế giao diện người dùng hoàn chỉnh cho ứng dụng SumUp** 
- một ứng dụng di động tóm tắt văn bản thông minh dựa trên công nghệ AI (Google Gemini), 
- áp ứng nhu cầu đa dạng của người dùng Việt Nam trong việc xử lý và tóm tắt thông tin văn bản nhanh chóng, 
- chính xác và hiệu quả.

Sản phẩm cuối cùng là một **bộ thiết kế giao diện hoàn chỉnh** (High-Fidelity Mockups và Interactive Prototype) đã được nghiên cứu,
thiết kế, kiểm thử và tinh chỉnh dựa trên phương pháp thiết kế lấy người dùng làm trung tâm (User-Centered Design).

#### Mục tiêu phụ

Để đạt được mục tiêu chính, đồ án hướng đến các mục tiêu cụ thể sau:

**1. Về trải nghiệm người dùng (UX):**

- **Tối ưu hóa quy trình tóm tắt văn bản:** Thiết kế luồng tương tác (user flow) mượt mà, giảm thiểu số bước và thời gian cần thiết để người dùng hoàn thành tác vụ tóm tắt từ nhiều nguồn khác nhau (text input, PDF, DOCX, OCR)

- **Đảm bảo tính dễ hiểu và dễ học:** Giao diện phải trực quan, rõ ràng, người dùng mới có thể sử dụng được ngay lần đầu tiên mà không cần hướng dẫn chi tiết

- **Nâng cao tính linh hoạt và cá nhân hóa:** Cung cấp nhiều lựa chọn tùy biến (personas, độ dài tóm tắt, format) để phù hợp với nhu cầu đa dạng của từng nhóm người dùng

**2. Về giao diện người dùng (UI):**

- **Xây dựng hệ thống thiết kế nhất quán:** Phát triển Design System hoàn chỉnh với bảng màu, typography, iconography và components tái sử dụng, đảm bảo tính đồng nhất trên toàn bộ ứng dụng

- **Tuân thủ chuẩn Material Design 3:** Áp dụng nguyên tắc Material You để tạo giao diện hiện đại, sang trọng và phù hợp với hệ sinh thái Android

- **Thiết kế responsive đa nền tảng:** Đảm bảo giao diện hoạt động tối ưu trên nhiều kích thước màn hình khác nhau (điện thoại, máy tính bảng, màn hình gập)

**3. Về khả năng tiếp cận (Accessibility):**

- **Đạt chuẩn WCAG 2.1 Level AA:** Đảm bảo giao diện có thể sử dụng được bởi người khuyết tật (màu sắc có độ tương phản cao, hỗ trợ screen reader, kích thước touch target phù hợp)

- **Hỗ trợ đa ngôn ngữ:** Thiết kế giao diện linh hoạt, có thể hiển thị tiếng Việt, tiếng Anh và các ngôn ngữ khác một cách tự nhiên

- **Phản hồi hệ thống rõ ràng:** Sử dụng haptic feedback, animations và loading states để người dùng luôn hiểu được trạng thái của ứng dụng

**4. Về quy trình thiết kế:**

- **Nghiên cứu người dùng kỹ lưỡng:** Tiến hành khảo sát, phỏng vấn để hiểu sâu về nhu cầu, pain points và hành vi của người dùng mục tiêu

- **Kiểm thử và tinh chỉnh liên tục:** Thực hiện usability testing với người dùng thật, thu thập phản hồi và cải tiến thiết kế dựa trên dữ liệu thực tế

- **Xây dựng tài liệu thiết kế chi tiết:** Tạo ra bộ tài liệu đầy đủ, chuyên nghiệp, có thể sử dụng làm tài liệu tham khảo cho các dự án tương lai

**5. Về tính năng độc đáo:**

- **Tích hợp OCR tiên tiến:** Thiết kế giao diện camera scanner thân thiện, hỗ trợ quét và tóm tắt văn bản từ hình ảnh (sách, tài liệu in)

- **Hệ thống Persona thông minh:** Cho phép người dùng tùy chỉnh phong cách tóm tắt theo 6 personas khác nhau (Sinh viên, Chuyên gia, Học thuật, Sáng tạo, Tóm gọn, Tổng quát)

- **Quản lý lịch sử và tìm kiếm nâng cao:** Thiết kế hệ thống lưu trữ và tìm kiếm hiệu quả giúp người dùng dễ dàng quản lý các bản tóm tắt đã tạo

---

### Đối tượng và Phạm vi của Đồ án

#### Đối tượng người dùng

Ứng dụng SumUp được thiết kế nhắm đến **ba nhóm đối tượng người dùng chính** tại Việt Nam:

**1. Sinh viên và Học sinh (18-25 tuổi):**

- **Đặc điểm:** Thường xuyên phải đọc tài liệu học tập, nghiên cứu, viết luận văn
- **Nhu cầu:** Tóm tắt nhanh các bài báo khoa học, sách giáo khoa, tài liệu tham khảo
- **Hành vi công nghệ:** Sử dụng smartphone chủ yếu, quen với ứng dụng di động, ưa thích giao diện trực quan và nhanh chóng
- **Pain points:** Thiếu thời gian đọc, khó nắm bắt ý chính, cần hiểu nội dung nhanh để làm bài tập hoặc ôn thi

**2. Nhân viên văn phòng (25-45 tuổi):**

- **Đặc điểm:** Làm việc trong môi trường doanh nghiệp, phải xử lý nhiều email, báo cáo, hợp đồng
- **Nhu cầu:** Tóm tắt email dài, báo cáo, tài liệu kinh doanh để tiết kiệm thời gian
- **Hành vi công nghệ:** Sử dụng cả điện thoại và máy tính bảng, ưu tiên hiệu quả và tính chuyên nghiệp
- **Pain points:** Quá tải thông tin, cần nhanh chóng nắm bắt nội dung chính của tài liệu để ra quyết định

**3. Nhà nghiên cứu và Học giả (30-60 tuổi):**

- **Đặc điểm:** Làm việc trong lĩnh vực nghiên cứu khoa học, giảng dạy đại học
- **Nhu cầu:** Tóm tắt các bài báo nghiên cứu, tài liệu học thuật chuyên sâu, theo dõi xu hướng mới
- **Hành vi công nghệ:** Sử dụng nhiều thiết bị, đòi hỏi độ chính xác cao, có thể ít quen với công nghệ mới
- **Pain points:** Lượng tài liệu cần đọc quá nhiều, cần công cụ hỗ trợ lọc thông tin nhanh chóng nhưng không làm mất độ chính xác

**Đặc điểm chung của nhóm người dùng mục tiêu:**

- Có nhu cầu xử lý văn bản tiếng Việt và tiếng Anh
- Sử dụng smartphone Android (chiếm ~75% thị phần tại Việt Nam)
- Đánh giá cao sự tiện lợi, tốc độ và chất lượng tóm tắt
- Sẵn sàng trả phí nếu công cụ thực sự hữu ích

#### Phạm vi chức năng

Trong khuôn khổ đồ án thiết kế giao diện này, nhóm tập trung thiết kế đầy đủ cho **các tính năng chính** sau:

**1. Tính năng nhập liệu và xử lý đa dạng:**

- **Text Input:** Nhập hoặc paste văn bản trực tiếp vào ô nhập liệu
- **Document Upload:** Tải lên tài liệu PDF, DOCX, TXT, RTF
- **OCR Scanner:** Quét văn bản từ hình ảnh bằng camera (sử dụng ML Kit Text Recognition)

**2. Tính năng tóm tắt thông minh:**

- **AI-Powered Summarization:** Sử dụng Google Gemini API để tạo tóm tắt chất lượng cao
- **Persona Selection:** Chọn phong cách tóm tắt theo 6 personas (General, Student, Professional, Academic, Creative, Quick Brief)
- **Multi-level Summary:** Cung cấp 3 mức độ chi tiết (Brief, Standard, Detailed)
- **Real-time Processing:** Hiển thị tiến trình xử lý với progress indicator và timeout management

**3. Tính năng hiển thị kết quả:**

- **Summary Display:** Hiển thị tóm tắt với formatting rõ ràng (paragraph, bullet points, insights, action items)
- **KPI Metrics:** Thống kê số từ, thời gian đọc, tỷ lệ rút gọn
- **Export Options:** Xuất bản tóm tắt sang Text, Markdown, PDF
- **Copy & Share:** Sao chép hoặc chia sẻ tóm tắt qua các kênh khác

**4. Tính năng quản lý và cá nhân hóa:**

- **History Management:** Lưu trữ và quản lý lịch sử tóm tắt
- **Search & Filter:** Tìm kiếm nâng cao với nhiều tiêu chí (ngày tạo, persona, loại input, từ khóa)
- **Favorites:** Đánh dấu yêu thích các bản tóm tắt quan trọng
- **Settings:** Cài đặt API key, theme (Light/Dark), ngôn ngữ, preferences

**5. Tính năng bổ trợ:**

- **Draft Auto-save:** Tự động lưu bản nháp mỗi 2 giây
- **Draft Recovery:** Khôi phục bản nháp sau khi thoát ứng dụng (<24h)
- **Achievements & Gamification:** Hệ thống điểm thành tích khuyến khích sử dụng
- **Analytics Dashboard:** Thống kê sử dụng API, số lượng tóm tắt theo thời gian

**Các tính năng ngoài phạm vi đồ án này (Future Work):**

- Đồng bộ đám mây (Cloud Sync)
- Hợp tác nhóm (Collaborative features)
- Voice input summarization
- Offline AI model
- Chrome extension và iOS version

#### Phạm vi công nghệ

**Nền tảng thiết kế:**

- **Platform:** Android (SDK 24 - 35), ưu tiên điện thoại di động
- **Design Tool:** Figma (cho wireframes, mockups, prototype)
- **Framework UI:** Material Design 3 (Material You)
- **Responsive:** Hỗ trợ điện thoại (360dp-599dp), tablet (600dp-839dp), desktop/foldable (840dp+)

**Công nghệ liên quan:**

- **AI Service:** Google Gemini 1.5 Flash API
- **OCR:** ML Kit Text Recognition
- **Document Processing:** PDFBox, Mammoth (DOCX)
- **Database:** Room Database (SQLite)
- **Architecture:** Clean Architecture với MVVM pattern

**Giới hạn kỹ thuật:**

- Thiết kế UI sẽ được tối ưu cho **Android native app** (Kotlin + Jetpack Compose)
- Không bao gồm thiết kế cho iOS, web hoặc các nền tảng khác
- Giới hạn text input: 5,000 ký tự (có thể mở rộng sau)
- Giới hạn kích thước file upload: 10MB

---

### Phương pháp Nghiên cứu

Đồ án này được thực hiện theo **phương pháp thiết kế lấy người dùng làm trung tâm (User-Centered Design - UCD)**, kết hợp nhiều kỹ thuật nghiên cứu và thiết kế khác nhau:

**1. Nghiên cứu thứ cấp (Secondary Research):**

- **Desk Research:** Nghiên cứu tài liệu, báo cáo thị trường, xu hướng thiết kế UI/UX
- **Competitive Analysis:** Phân tích 3-5 ứng dụng đối thủ cạnh tranh (Notion AI, Otter.ai, QuillBot, ChatGPT, Claude)
- **Best Practices:** Tham khảo Material Design Guidelines, Apple HIG, Nielsen Norman Group

**2. Nghiên cứu định tính (Qualitative Research):**

- **User Interviews:** Phỏng vấn sâu 10-15 người dùng đại diện cho 3 nhóm đối tượng mục tiêu
- **Contextual Inquiry:** Quan sát người dùng trong môi trường thực tế khi họ xử lý văn bản
- **Card Sorting:** Xác định cách người dùng tổ chức và phân loại thông tin

**3. Nghiên cứu định lượng (Quantitative Research):**

- **Online Survey:** Khảo sát trực tuyến với 100-150 người dùng tiềm năng
- **Analytics Data:** Phân tích số liệu sử dụng từ các ứng dụng tương tự (nếu có)
- **A/B Testing:** So sánh các phiên bản thiết kế khác nhau (trong giai đoạn iteration)

**4. Phương pháp thiết kế:**

- **Persona Development:** Xây dựng 3 personas dựa trên dữ liệu nghiên cứu
- **Journey Mapping:** Vẽ sơ đồ hành trình người dùng để hiểu pain points và opportunities
- **Information Architecture:** Thiết kế cấu trúc thông tin logic và dễ tìm kiếm
- **Wireframing:** Tạo wireframes (low-fidelity) để xác định layout và chức năng
- **Prototyping:** Xây dựng prototype tương tác (high-fidelity) bằng Figma
- **Design System:** Phát triển hệ thống thiết kế nhất quán cho toàn bộ ứng dụng

**5. Phương pháp kiểm thử:**

- **Usability Testing:** Kiểm thử với 5-8 người dùng thật trên prototype
- **Think-Aloud Protocol:** Yêu cầu người dùng nói ra suy nghĩ khi sử dụng
- **Task-based Testing:** Đánh giá khả năng hoàn thành các tác vụ cụ thể
- **SUS (System Usability Scale):** Đo lường mức độ dễ sử dụng của giao diện
- **Heuristic Evaluation:** Đánh giá theo 10 nguyên tắc usability của Nielsen

**6. Phương pháp tinh chỉnh:**

- **Iterative Design:** Thiết kế - Kiểm thử - Cải tiến - Kiểm thử lại
- **Feedback Integration:** Tích hợp phản hồi từ người dùng và giảng viên hướng dẫn
- **Design Review:** Đánh giá chéo (peer review) với các nhóm thiết kế khác

**Quy trình thực hiện theo 4 giai đoạn:**

```
GIAI ĐOẠN 1: Nghiên cứu & Khám phá (2-3 tuần)
    → Survey, Interviews, Competitive Analysis
    → Personas, User Journey Maps

GIAI ĐOẠN 2: Thiết kế Cấu trúc (2-3 tuần)
    → Information Architecture, User Flows
    → Wireframes (Low-fidelity)

GIAI ĐOẠN 3: Thiết kế Visual (3-4 tuần)
    → Design System
    → High-fidelity Mockups
    → Interactive Prototype

GIAI ĐOẠN 4: Kiểm thử & Tinh chỉnh (2-3 tuần)
    → Usability Testing
    → Iteration & Refinement
    → Final Design & Documentation
```

---

### Cấu trúc Báo cáo

Báo cáo đồ án này được tổ chức thành **3 phần chính** với **4 chương nội dung**, tuân thủ chuẩn mực học thuật và quy trình thiết kế UI/UX chuyên nghiệp:

**PHẦN I: CÁC MỤC SƠ KHỞI**
- Trang bìa, Lời cảm ơn, Lời cam đoan
- Mục lục, Danh mục từ viết tắt, Danh mục hình ảnh, Danh mục bảng biểu

**PHẦN II: NỘI DUNG CHÍNH**

**Mở đầu (trang hiện tại):**
- Giới thiệu bối cảnh, tính cấp thiết của đề tài
- Nêu rõ mục tiêu chính và mục tiêu phụ
- Xác định đối tượng người dùng, phạm vi chức năng và công nghệ
- Trình bày phương pháp nghiên cứu

**CHƯƠNG 1: Nghiên cứu và Khám phá (Research & Discovery)**

*Mục đích:* Xây dựng nền tảng hiểu biết vững chắc về người dùng và thị trường

*Nội dung:*
- **1.1.** Phân tích thị trường và đối thủ cạnh tranh (Competitive Analysis)
  - Đánh giá các ứng dụng tương tự: Notion AI, Otter.ai, QuillBot
  - Rút ra bài học về UI/UX, điểm mạnh/yếu của từng sản phẩm

- **1.2.** Xây dựng chân dung người dùng (User Personas)
  - Phát triển 3 personas chi tiết dựa trên nghiên cứu thực tế
  - Mỗi persona bao gồm: Demographics, Goals, Pain Points, Behaviors

- **1.3.** Khảo sát và phỏng vấn người dùng
  - Trình bày kết quả khảo sát định lượng (biểu đồ, số liệu)
  - Tổng hợp phỏng vấn định tính (insights, quotes)
  - Phân tích để tìm ra nhu cầu thật sự của người dùng

- **1.4.** Tổng hợp yêu cầu và xác định mục tiêu thiết kế
  - Danh sách yêu cầu chức năng (Functional Requirements)
  - Danh sách yêu cầu phi chức năng (Non-functional Requirements)
  - Mục tiêu thiết kế cụ thể dựa trên dữ liệu thu thập

**CHƯƠNG 2: Xây dựng Cấu trúc và Luồng Tương tác (Structure & Interaction Flow)**

*Mục đích:* Thiết kế "bộ xương" logic cho ứng dụng

*Nội dung:*
- **2.1.** Kiến trúc thông tin (Information Architecture)
  - Sơ đồ phân cấp màn hình và tính năng
  - Hệ thống điều hướng (Navigation System)

- **2.2.** Sơ đồ luồng người dùng (User Flow Diagrams)
  - Luồng tóm tắt văn bản từ Text Input
  - Luồng upload và xử lý tài liệu (PDF/DOCX)
  - Luồng OCR quét hình ảnh
  - Luồng quản lý lịch sử và cài đặt

- **2.3.** Phác thảo giao diện (Wireframing)
  - Wireframes cho 7 màn hình chính
  - Chú thích chức năng và tương tác trên từng wireframe

**CHƯƠNG 3: Thiết kế Visual và Hệ thống Thiết kế (Visual UI & Design System)**

*Mục đích:* "Thổi hồn" vào thiết kế với visual identity và đảm bảo tính nhất quán

*Nội dung:*
- **3.1.** Xây dựng hệ thống thiết kế (Design System)
  - Bảng màu (Color Palette) - Light/Dark theme
  - Kiểu chữ (Typography) - Hierarchy và scaling
  - Hệ thống lưới và khoảng cách (Grid & Spacing)
  - Biểu tượng (Iconography) - Material Icons
  - Thành phần (Components) - Buttons, Cards, Dialogs, Forms...

- **3.2.** Thiết kế giao diện hoàn chỉnh (High-Fidelity Mockups)
  - Mockups chi tiết cho 7 màn hình chính
  - Áp dụng Design System vào từng màn hình

- **3.3.** Thiết kế đáp ứng (Responsive Design)
  - Layout cho điện thoại (Compact - 360dp-599dp)
  - Layout cho máy tính bảng (Medium - 600dp-839dp)
  - Layout cho màn hình lớn (Expanded - 840dp+)

- **3.4.** Tạo mẫu thử tương tác (Interactive Prototype)
  - Link prototype Figma có thể tương tác
  - Hướng dẫn các luồng chính có thể trải nghiệm

**CHƯƠNG 4: Kiểm thử, Đánh giá và Tinh chỉnh (Testing, Evaluation & Refinement)**

*Mục đích:* Xác minh thiết kế với người dùng thật và cải tiến

*Nội dung:*
- **4.1.** Lập kế hoạch kiểm thử (Test Plan)
  - Mục tiêu kiểm thử, đối tượng tham gia
  - Kịch bản và tác vụ kiểm thử

- **4.2.** Tiến hành kiểm thử (Conducting Usability Testing)
  - Quy trình và công cụ kiểm thử
  - Thu thập dữ liệu (screen recording, notes, metrics)

- **4.3.** Tổng hợp kết quả và phân tích
  - Metrics định lượng: Task Success Rate, Time on Task, Error Rate, SUS Score
  - Phản hồi định tính: User quotes, pain points discovered
  - Phân loại vấn đề: Critical, Major, Minor issues

- **4.4.** Đề xuất và thực hiện tinh chỉnh
  - Ưu tiên xử lý các vấn đề theo mức độ nghiêm trọng
  - Trình bày giải pháp thiết kế (Before/After comparisons)
  - Kết quả kiểm thử lại sau khi cải tiến

**PHẦN III: KẾT LUẬN VÀ PHỤ LỤC**

**Kết luận:**
- Tóm tắt kết quả đạt được so với mục tiêu ban đầu
- Tự đánh giá điểm mạnh, hạn chế của đồ án
- Hướng phát triển trong tương lai (roadmap, features mở rộng)

**Tài liệu tham khảo:**
- Danh sách đầy đủ các nguồn tài liệu đã tham khảo (theo chuẩn APA/IEEE)

**Phụ lục:**
- Phụ lục A: Bảng câu hỏi khảo sát đầy đủ
- Phụ lục B: Kịch bản phỏng vấn người dùng
- Phụ lục C: Dữ liệu thô khảo sát (raw data)
- Phụ lục D: Bảng kịch bản kiểm thử usability
- Phụ lục E: Biên bản phỏng vấn chi tiết
- Phụ lục F: Hướng dẫn sử dụng prototype
- Phụ lục G: Tài liệu kỹ thuật Design System

---

**Kết thúc phần Mở đầu**

Với nền tảng được thiết lập trong phần Mở đầu này, các chương tiếp theo sẽ đi sâu vào từng giai đoạn của quy trình thiết kế, từ nghiên cứu người dùng, xây dựng cấu trúc, thiết kế visual, đến kiểm thử và tinh chỉnh. Mỗi quyết định thiết kế đều được dựa trên dữ liệu và phân tích khoa học, nhằm đảm bảo sản phẩm cuối cùng thực sự đáp ứng nhu cầu của người dùng.

---

<div style="page-break-after: always;"></div>

# CHƯƠNG 1: NGHIÊN CỨU VÀ KHÁM PHÁ

---

