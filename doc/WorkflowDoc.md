# TÀI LIỆU MÔ TẢ QUY TRÌNH NGHIỆP VỤ (WORKFLOW)
## WEB MUA BÁN ĐỒ CŨ DÀNH CHO SINH VIÊN

* **Môn học:** ............................................................
* **Giảng viên hướng dẫn:** ........................................
* **Nhóm thực hiện:** ................................................
* **Thành viên:** ........................................................
* **Ngày hoàn thành:** ....../....../..........

*TP. Hồ Chí Minh – 2026*

---

## THÔNG TIN TÀI LIỆU

| Mục | Nội dung |
| :--- | :--- |
| **Tên tài liệu** | Tài liệu mô tả quy trình nghiệp vụ (Workflow) |
| **Tên dự án** | Web mua bán đồ cũ dành cho sinh viên |
| **Phiên bản** | 1.0 – Bản dự thảo |
| **Tài liệu tham chiếu** | Tài liệu đặc tả yêu cầu phần mềm (SRS) phiên bản 1.0, các phần 1–3; Tài liệu đặc tả Use Case phiên bản 1.0 |
| **Trạng thái** | Dự thảo, cần nhóm xác nhận các giả định ở mục 2 |
| **Đối tượng sử dụng** | Giảng viên, nhóm phân tích – thiết kế, nhóm phát triển và kiểm thử |

---

## MỤC LỤC

> *Lưu ý: Mở tài liệu bằng Microsoft Word, nhấn Ctrl+A rồi F9 để cập nhật mục lục và số trang.*

* [1. GIỚI THIỆU](#1-giới-thiệu)
  * [1.1. Mục đích](#11-mục-đích)
  * [1.2. Phạm vi](#12-phạm-vi)
  * [1.3. Ký hiệu trong sơ đồ](#13-ký-hiệu-trong-sơ-đồ)
* [2. GIẢ ĐỊNH VÀ ĐIỂM CẦN XÁC NHẬN](#2-giả-định-và-điểm-cần-xác-nhận)
* [3. WORKFLOW TỔNG QUAN (WF-00)](#3-workflow-tổng-quan-wf-00)
* [4. WORKFLOW CHI TIẾT](#4-workflow-chi-tiết)
  * [WF-01. Đăng ký, đăng nhập và đăng xuất](#wf-01-đăng-ký-đăng-nhập-và-đăng-xuất)
  * [WF-02. Đăng bán và quản lý sản phẩm](#wf-02-đăng-bán-và-quản-lý-sản-phẩm)
  * [WF-03. Tìm kiếm, giỏ hàng và đặt hàng](#wf-03-tìm-kiếm-giỏ-hàng-và-đặt-hàng)
  * [WF-04. Xử lý đơn hàng](#wf-04-xử-lý-đơn-hàng)
  * [WF-05. Hủy đơn hàng](#wf-05-hủy-đơn-hàng)
  * [WF-06. Trao đổi giữa người mua và người bán (Chat)](#wf-06-trao-đổi-giữa-người-mua-và-người-bán-chat)
  * [WF-07. Đánh giá giao dịch](#wf-07-đánh-giá-giao-dịch)
  * [WF-08. Báo cáo vi phạm và kiểm duyệt](#wf-08-báo-cáo-vi-phạm-và-kiểm-duyệt)
  * [WF-09. Quản trị tài khoản, danh mục và thống kê](#wf-09-quản-trị-tài-khoản-danh-mục-và-thống-kê)
* [5. SƠ ĐỒ CHUYỂN TRẠNG THÁI](#5-sơ-đồ-chuyển-trạng-thái)
  * [5.1. Trạng thái đơn hàng](#51-trạng-thái-đơn-hàng)
  * [5.2. Trạng thái sản phẩm](#52-trạng-thái-sản-phẩm)
  * [5.3. Trạng thái tài khoản và báo cáo vi phạm](#53-trạng-thái-tài-khoản-và-báo-cáo-vi-phạm)
* [6. QUY TẮC NGHIỆP VỤ ÁP DỤNG TRONG WORKFLOW](#6-quy-tắc-nghiệp-vụ-áp-dụng-trong-workflow)
* [7. ĐỐI CHIẾU WORKFLOW VÀ USE CASE](#7-đối-chiếu-workflow-và-use-case)

---

## 1. GIỚI THIỆU

### 1.1. Mục đích
Tài liệu này mô tả các quy trình nghiệp vụ (workflow) của hệ thống Web mua bán đồ cũ dành cho sinh viên, dựa trên tài liệu SRS phiên bản 1.0. Mỗi quy trình được thể hiện bằng sơ đồ luồng và bảng mô tả từng bước, giúp nhóm thống nhất cách các tác nhân và hệ thống phối hợp với nhau từ lúc đăng ký đến khi hoàn tất giao dịch và quản trị nền tảng.

Tài liệu là cơ sở để thiết kế Activity Diagram, Sequence Diagram, thiết kế cơ sở dữ liệu (đặc biệt là các trạng thái của đơn hàng, sản phẩm, tài khoản) và xây dựng kịch bản kiểm thử.

### 1.2. Phạm vi
Tài liệu bao gồm 9 quy trình nghiệp vụ chính (WF-01 đến WF-09), quy trình tổng quan (WF-00) và các sơ đồ chuyển trạng thái. Phạm vi bám sát mục 2.5 của SRS: không bao gồm cổng thanh toán trực tuyến, vận chuyển tự động, hoàn tiền hoặc xử lý tranh chấp tài chính.

### 1.3. Ký hiệu trong sơ đồ
Các sơ đồ dùng màu để phân biệt đối tượng thực hiện bước; hình dạng thể hiện loại bước:

| Ký hiệu | Ý nghĩa |
| :--- | :--- |
| **Hình chữ nhật bo góc (có màu)** | Một bước xử lý do tác nhân hoặc hệ thống thực hiện; màu cho biết đối tượng thực hiện. |
| **Hình thoi** | Điểm quyết định (điều kiện đúng/sai hoặc lựa chọn của người dùng). |
| **Hình chữ nhật nét đứt** | Quy trình con được mô tả ở một workflow khác (WF-xx). |
| **Khung viền đậm / khung đen** | Điểm bắt đầu / điểm kết thúc của quy trình. |
| **Mũi tên nét đứt** | Luồng tùy chọn, luồng quay lại hoặc luồng do sự kiện thời gian kích hoạt. |

---

## 2. GIẢ ĐỊNH VÀ ĐIỂM CẦN XÁC NHẬN

SRS hiện mới hoàn thiện các phần 1–3 và ghi chú cuối tài liệu nêu rõ nhóm cần thống nhất mô hình tài khoản, cách đặt hàng, phương thức thanh toán, giao nhận và liên hệ giữa người mua và người bán. Để hoàn thiện quy trình, tài liệu này đưa ra các giả định sau. Các giả định là đề xuất, cần nhóm xác nhận hoặc điều chỉnh; khi thay đổi, cần cập nhật lại sơ đồ và bảng bước tương ứng.

| Mã | Nội dung giả định | Căn cứ / ghi chú |
| :--- | :--- | :--- |
| **G-01** | Khách chưa đăng nhập được xem danh sách, tìm kiếm và xem chi tiết sản phẩm. Mọi thao tác khác (giỏ hàng, đặt hàng, chat, đánh giá, báo cáo, đăng bán) yêu cầu đăng nhập. | SRS chưa nêu rõ khách vãng lai. Cần nhóm xác nhận. |
| **G-02** | Một tài khoản có đồng thời quyền mua và quyền bán, không cần đăng ký vai trò riêng. Quyền hiển thị theo ngữ cảnh nghiệp vụ. | SRS mục 2.3, 3.4.1. |
| **G-03** | Mỗi sản phẩm là một món đồ cụ thể (số lượng 1). Vì vậy "cập nhật giỏ hàng" gồm chọn, bỏ chọn và xóa sản phẩm, không có số lượng. | Đặc thù đồ cũ. Cần nhóm xác nhận. |
| **G-04** | Kiểm duyệt sau đăng: sản phẩm đăng xong hiển thị ngay; Admin gỡ hoặc ẩn khi phát hiện vi phạm (qua báo cáo hoặc rà soát chủ động). | SRS mục 3.3 (kiểm duyệt nội dung đăng bán). |
| **G-05** | Thanh toán thực hiện trực tiếp giữa hai bên khi giao nhận (tiền mặt hoặc chuyển khoản ngoài hệ thống); không tích hợp cổng thanh toán. Giao nhận do hai bên tự thỏa thuận (gặp trực tiếp hoặc người bán giao). | SRS mục 2.5b (ngoài phạm vi). Ghi chú cuối SRS yêu cầu thống nhất. |
| **G-06** | Mỗi đơn hàng chỉ chứa sản phẩm của một người bán. Nếu giỏ có sản phẩm của nhiều người bán, hệ thống tự tách thành nhiều đơn. | Đề xuất để đơn giản hóa xử lý đơn. |
| **G-07** | Khi đơn được tạo, sản phẩm chuyển sang "Đang giữ" để tránh trùng đơn. Đơn Chờ xác nhận quá 48 giờ tự động hủy và sản phẩm trở về "Đang bán". | Đề xuất. Ngưỡng 48 giờ có thể điều chỉnh. |
| **G-08** | Người mua xác nhận đã nhận hàng và chuyển sang "Hoàn thành" | Quyết định của nhóm; khớp bảng chuyển trạng thái trong `specs/001-cart-management/data-model.md`. |
| **G-09** | Người dùng được đánh giá giao dịch trong vòng 7 ngày kể từ khi đơn Hoàn thành; mỗi bên đánh giá một lần cho mỗi đơn. | SRS mục 3.4.1 (điều kiện hoàn thành). Thời hạn là đề xuất. |
| **G-10** | Thông báo gửi qua chuông thông báo trong hệ thống (in-app). Gửi email là tùy chọn mở rộng. | Đề xuất. |
| **G-11** | Các ngưỡng kỹ thuật (mật khẩu tối thiểu 8 ký tự, tối đa 5 ảnh mỗi sản phẩm, mỗi ảnh tối đa 5 MB, tin nhắn tối đa 1000 ký tự, khóa tạm 15 phút sau 5 lần đăng nhập sai) chỉ là giá trị đề xuất. | Đề xuất. Nhóm có thể điều chỉnh khi thiết kế. |
| **G-12** | Chức năng "Cập nhật thông tin cá nhân" (UC23) được bổ sung từ mục 3.1 và 3.2 của SRS; ma trận phân quyền chưa có dòng tương ứng, nên cần thêm vào ma trận. | SRS mục 3.1, 3.2 và 3.4. |

---

## 3. WORKFLOW TỔNG QUAN (WF-00)

Sơ đồ dưới đây mô tả vòng đời nghiệp vụ tổng thể: người dùng đăng nhập, sau đó bán hoặc mua; đơn hàng được xử lý đến khi hoàn thành và được đánh giá; song song là quy trình báo cáo vi phạm và quản trị của Admin.

*(Hình 3.1. Workflow tổng quan (WF-00) – Lược bỏ hình ảnh)*

| Giai đoạn | Nội dung | Workflow chi tiết |
| :--- | :--- | :--- |
| **Tài khoản** | Đăng ký, đăng nhập, đăng xuất | WF-01 |
| **Đăng bán** | Đăng bán, chỉnh sửa, ẩn / hiện lại sản phẩm | WF-02 |
| **Tìm kiếm và đặt hàng** | Tìm kiếm, lọc, xem chi tiết, giỏ hàng, tạo đơn | WF-03 |
| **Xử lý đơn hàng** | Xác nhận / từ chối, giao nhận, hoàn thành | WF-04 |
| **Hủy đơn** | Hủy đơn Chờ xác nhận hoặc Đã xác nhận | WF-05 |
| **Trao đổi** | Chat giữa người mua và người bán | WF-06 |
| **Đánh giá** | Đánh giá giao dịch sau khi hoàn thành | WF-07 |
| **Kiểm duyệt** | Báo cáo vi phạm, xử lý báo cáo, gỡ sản phẩm, khóa tài khoản | WF-08 |
| **Quản trị** | Quản lý tài khoản, danh mục, thống kê | WF-09 |

---

## 4. WORKFLOW CHI TIẾT

Mỗi workflow gồm: thông tin chung, sơ đồ luồng và bảng mô tả các bước. Cột "Đối tượng" tương ứng với màu trong sơ đồ.

### WF-01. Đăng ký, đăng nhập và đăng xuất

| Thông tin | Chi tiết |
| :--- | :--- |
| **Mục tiêu** | Cho phép người dùng tạo tài khoản và truy cập hệ thống theo đúng vai trò. |
| **Tác nhân** | Người mua, Người bán, Admin (đăng nhập / đăng xuất) |
| **Kích hoạt** | Người dùng chọn Đăng ký hoặc Đăng nhập trên website. |
| **Use Case liên quan** | UC01, UC02 |
| **Quy tắc áp dụng** | BR-09 và G-02, G-11 |

#### WF-01A. Đăng ký tài khoản
*(Hình 4.1. Workflow đăng ký tài khoản (WF-01A) – Lược bỏ hình ảnh)*

**Bảng mô tả các bước – Workflow đăng ký tài khoản (WF-01A)**

| STT | Đối tượng | Hoạt động | Kết quả / trạng thái |
| :--- | :--- | :--- | :--- |
| 1 | Người dùng | Truy cập website, chọn Đăng ký. | Hiển thị form đăng ký. |
| 2 | Người dùng | Nhập họ tên, email, số điện thoại, mật khẩu và xác nhận mật khẩu. | Dữ liệu được gửi lên hệ thống. |
| 3 | Hệ thống | Kiểm tra trường bắt buộc, định dạng, email / số điện thoại chưa tồn tại. | Kết luận hợp lệ hoặc không hợp lệ. |
| 4 | Hệ thống | Nếu không hợp lệ: báo lỗi tại từng trường và quay lại bước 2. | Người dùng chỉnh sửa dữ liệu. |
| 5 | Hệ thống | Nếu hợp lệ: mã hóa mật khẩu và tạo tài khoản. | Tài khoản ở trạng thái Hoạt động. |
| 6 | Hệ thống | Thông báo thành công, chuyển sang trang đăng nhập. | Kết thúc quy trình. |

#### WF-01B. Đăng nhập và đăng xuất
*(Hình 4.2. Workflow đăng nhập, đăng xuất (WF-01B) – Lược bỏ hình ảnh)*

**Bảng mô tả các bước – Workflow đăng nhập, đăng xuất (WF-01B)**

| STT | Đối tượng | Hoạt động | Kết quả / trạng thái |
| :--- | :--- | :--- | :--- |
| 1 | Người dùng | Chọn Đăng nhập, nhập email / số điện thoại và mật khẩu. | Thông tin gửi lên hệ thống. |
| 2 | Hệ thống | Xác thực thông tin đăng nhập. | Đúng hoặc sai. |
| 3 | Hệ thống | Nếu sai: thông báo chung "thông tin đăng nhập không đúng", quay lại bước 1. | Người dùng nhập lại. |
| 4 | Hệ thống | Nếu đúng: kiểm tra trạng thái tài khoản. Tài khoản bị khóa thì thông báo và kết thúc. | Không tạo phiên đăng nhập. |
| 5 | Hệ thống | Tạo phiên đăng nhập; Admin được chuyển đến khu vực quản trị, người dùng khác về trang chủ. | Phiên đăng nhập hợp lệ. |
| 6 | Người dùng | Sử dụng các chức năng theo ma trận phân quyền. | Thực hiện nghiệp vụ. |
| 7 | Người dùng | Chọn Đăng xuất; hệ thống hủy phiên và quay về trang chủ. | Phiên đăng nhập kết thúc. |

---

### WF-02. Đăng bán và quản lý sản phẩm

| Thông tin | Chi tiết |
| :--- | :--- |
| **Mục tiêu** | Người bán đăng sản phẩm đã qua sử dụng và quản lý trạng thái hiển thị của sản phẩm. |
| **Tác nhân** | Người bán |
| **Kích hoạt** | Người bán chọn Đăng bán sản phẩm hoặc mở mục Sản phẩm của tôi. |
| **Use Case liên quan** | UC06, UC07, UC08 |
| **Quy tắc áp dụng** | BR-01, BR-02, BR-08; G-03, G-04 |

*(Hình 4.3. Workflow đăng bán và quản lý sản phẩm (WF-02) – Lược bỏ hình ảnh)*

**Bảng mô tả các bước – Workflow đăng bán và quản lý sản phẩm (WF-02)**

| STT | Đối tượng | Hoạt động | Kết quả / trạng thái |
| :--- | :--- | :--- | :--- |
| 1 | Người bán | Đăng nhập, chọn Đăng bán sản phẩm. | Hiển thị form đăng bán. |
| 2 | Người bán | Nhập tên, danh mục, mô tả, giá, tình trạng sử dụng và hình ảnh. | Dữ liệu gửi lên hệ thống. |
| 3 | Hệ thống | Kiểm tra dữ liệu bắt buộc, giá hợp lệ, danh mục đang hoạt động, định dạng và dung lượng ảnh. | Hợp lệ hoặc không hợp lệ. |
| 4 | Hệ thống | Nếu không hợp lệ: báo lỗi, giữ lại dữ liệu đã nhập, quay lại bước 2. | Người bán chỉnh sửa. |
| 5 | Hệ thống | Nếu hợp lệ: lưu sản phẩm và hiển thị công khai. | Sản phẩm ở trạng thái Đang bán. |
| 6 | Người bán | Vào Sản phẩm của tôi, chọn sản phẩm và chọn thao tác: Chỉnh sửa, Ẩn / ngừng bán hoặc Hiện lại. | Yêu cầu thao tác được gửi. |
| 7 | Hệ thống | Kiểm tra quyền sở hữu, trạng thái sản phẩm và các đơn hàng liên quan. | Cho phép hoặc từ chối. |
| 8 | Hệ thống | Nếu từ chối: thông báo lý do (ví dụ đang có đơn xử lý), quay lại danh sách. | Dữ liệu không đổi. |
| 9 | Hệ thống | Nếu cho phép: cập nhật dữ liệu và thông báo thành công. | Sản phẩm được sửa, chuyển Ẩn hoặc Đang bán trở lại. |

---

### WF-03. Tìm kiếm, giỏ hàng và đặt hàng

| Thông tin | Chi tiết |
| :--- | :--- |
| **Mục tiêu** | Người mua tìm được sản phẩm phù hợp và tạo đơn hàng gửi đến người bán. |
| **Tác nhân** | Người mua (và người bán khi đóng vai trò người mua), Hệ thống |
| **Kích hoạt** | Người dùng truy cập danh sách sản phẩm hoặc nhập từ khóa tìm kiếm. |
| **Use Case liên quan** | UC03, UC04, UC05, UC09, UC10 |
| **Quy tắc áp dụng** | BR-02, BR-03, BR-04; G-01, G-03, G-05, G-06, G-07 |

#### WF-03A. Tìm kiếm, xem chi tiết và thêm vào giỏ
*(Hình 4.4. Workflow tìm kiếm và thêm vào giỏ (WF-03A) – Lược bỏ hình ảnh)*

**Bảng mô tả các bước – Workflow tìm kiếm và thêm vào giỏ (WF-03A)**

| STT | Đối tượng | Hoạt động | Kết quả / trạng thái |
| :--- | :--- | :--- | :--- |
| 1 | Người mua | Duyệt danh sách sản phẩm, nhập từ khóa, chọn danh mục, khoảng giá, tình trạng, cách sắp xếp. | Yêu cầu tìm kiếm / lọc. |
| 2 | Hệ thống | Trả về danh sách sản phẩm Đang bán phù hợp, có phân trang. | Hiển thị kết quả. |
| 3 | Người mua | Chọn sản phẩm để xem chi tiết (ảnh, mô tả, giá, tình trạng, thông tin người bán). | Trang chi tiết sản phẩm. |
| 4 | Người mua | Chọn hành động: Hỏi thêm (WF-06), Báo cáo vi phạm (WF-08) hoặc Thêm vào giỏ. | Chuyển sang luồng tương ứng. |
| 5 | Hệ thống | Với Thêm vào giỏ: kiểm tra sản phẩm còn bán và không thuộc chính người mua. | Hợp lệ hoặc không. |
| 6 | Hệ thống | Nếu không hợp lệ: thông báo không thể thêm, quay lại trang chi tiết. | Giỏ hàng không đổi. |
| 7 | Người mua | Nếu hợp lệ: sản phẩm vào giỏ; người mua có thể chọn / bỏ chọn hoặc xóa sản phẩm trong giỏ. | Giỏ hàng được cập nhật; tiếp tục WF-03B. |

#### WF-03B. Đặt hàng
*(Hình 4.5. Workflow đặt hàng (WF-03B) – Lược bỏ hình ảnh)*

**Bảng mô tả các bước – Workflow đặt hàng (WF-03B)**

| STT | Đối tượng | Hoạt động | Kết quả / trạng thái |
| :--- | :--- | :--- | :--- |
| 1 | Người mua | Chọn các sản phẩm trong giỏ và nhấn Đặt hàng. | Hiển thị form thông tin đặt hàng. |
| 2 | Người mua | Nhập họ tên, số điện thoại, địa chỉ hoặc điểm hẹn nhận hàng, phương thức giao nhận, ghi chú. | Thông tin đơn hàng. |
| 3 | Hệ thống | Kiểm tra lại tình trạng từng sản phẩm tại thời điểm đặt. | Còn khả dụng hoặc không. |
| 4 | Hệ thống | Nếu có sản phẩm không còn khả dụng: thông báo, loại khỏi giỏ, quay lại bước 1. | Người mua chọn lại. |
| 5 | Hệ thống | Nếu khả dụng: tạo đơn hàng, tách đơn theo người bán; sản phẩm chuyển Đang giữ. | Đơn ở trạng thái Chờ xác nhận. |
| 6 | Hệ thống | Gửi thông báo cho người bán; xóa sản phẩm đã đặt khỏi giỏ. | Chuyển sang WF-04. |

---

### WF-04. Xử lý đơn hàng

| Thông tin | Chi tiết |
| :--- | :--- |
| **Mục tiêu** | Người bán xử lý đơn từ lúc nhận đến khi hoàn thành giao dịch hoặc từ chối. |
| **Tác nhân** | Người bán, Người mua (trao đổi), Hệ thống |
| **Kích hoạt** | Đơn hàng được tạo ở trạng thái Chờ xác nhận. |
| **Use Case liên quan** | UC11, UC12, UC13, UC15 |
| **Quy tắc áp dụng** | BR-05, BR-06, BR-12; G-05, G-07, G-08 |

*(Hình 4.6. Workflow xử lý đơn hàng (WF-04) – Lược bỏ hình ảnh)*

**Bảng mô tả các bước – Workflow xử lý đơn hàng (WF-04)**

| STT | Đối tượng | Hoạt động | Kết quả / trạng thái |
| :--- | :--- | :--- | :--- |
| 1 | Người bán | Nhận thông báo đơn mới, mở chi tiết đơn. | Xem thông tin sản phẩm, người mua, nơi nhận. |
| 2 | Người bán | Quyết định xác nhận hoặc từ chối đơn. | Chuyển nhánh xử lý. |
| 3 | Hệ thống | Nhánh từ chối: người bán nhập lý do; đơn chuyển Đã từ chối, sản phẩm về Đang bán, thông báo người mua. | Kết thúc, đơn không thực hiện. |
| 4 | Hệ thống | Nhánh tự động: quá 48 giờ chưa xử lý, đơn chuyển Đã hủy, sản phẩm về Đang bán, thông báo hai bên. | Kết thúc, đơn không thực hiện. |
| 5 | Hệ thống | Nhánh xác nhận: đơn chuyển Đã xác nhận, thông báo người mua. | Đơn ở trạng thái Đã xác nhận. |
| 6 | Người mua / Người bán | Trao đổi để thống nhất thời gian, địa điểm, cách thanh toán (WF-06). | Hai bên thống nhất phương án giao nhận. |
| 7 | Người bán | Cập nhật trạng thái Đang giao nhận; hệ thống thông báo người mua. | Đơn ở trạng thái Đang giao nhận. |
| 8 | Người bán | Bàn giao sản phẩm và nhận thanh toán ngoài hệ thống. | Giao dịch thực tế hoàn tất. |
| 9 | Người bán | Cập nhật trạng thái Hoàn thành. | Đơn Hoàn thành; sản phẩm Đã bán; thông báo người mua. |
| 10 | Hệ thống | Mở quyền đánh giá cho hai bên (WF-07). | Kết thúc, giao dịch thành công. |

---

### WF-05. Hủy đơn hàng

| Thông tin | Chi tiết |
| :--- | :--- |
| **Mục tiêu** | Cho phép người mua hoặc người bán hủy đơn khi đơn chưa chuyển sang giao nhận. |
| **Tác nhân** | Người mua, Người bán (có điều kiện) |
| **Kích hoạt** | Người dùng chọn Hủy đơn tại chi tiết đơn hàng. |
| **Use Case liên quan** | UC14 |
| **Quy tắc áp dụng** | BR-06, BR-12 |

*(Hình 4.7. Workflow hủy đơn hàng (WF-05) – Lược bỏ hình ảnh)*

**Bảng mô tả các bước – Workflow hủy đơn hàng (WF-05)**

| STT | Đối tượng | Hoạt động | Kết quả / trạng thái |
| :--- | :--- | :--- | :--- |
| 1 | Người dùng | Mở chi tiết đơn, chọn Hủy đơn. | Yêu cầu hủy gửi lên hệ thống. |
| 2 | Hệ thống | Kiểm tra đơn đang Chờ xác nhận hoặc Đã xác nhận. | Đủ hoặc không đủ điều kiện. |
| 3 | Hệ thống | Nếu không đủ điều kiện: từ chối, gợi ý trao đổi (WF-06) hoặc báo cáo (WF-08). | Đơn không đổi. |
| 4 | Người dùng | Nếu đủ điều kiện: nhập lý do hủy và xác nhận. | Yêu cầu hủy hợp lệ. |
| 5 | Hệ thống | Chuyển đơn sang Đã hủy, lưu người hủy, lý do, thời điểm. | Đơn ở trạng thái Đã hủy. |
| 6 | Hệ thống | Chuyển sản phẩm về Đang bán. | Sản phẩm có thể được đặt lại. |
| 7 | Hệ thống | Gửi thông báo cho bên còn lại. | Kết thúc. |

---

### WF-06. Trao đổi giữa người mua và người bán (Chat)

| Thông tin | Chi tiết |
| :--- | :--- |
| **Mục tiêu** | Hỗ trợ hai bên làm rõ thông tin sản phẩm và thống nhất cách giao nhận. |
| **Tác nhân** | Người mua, Người bán |
| **Kích hoạt** | Người dùng chọn Chat từ trang sản phẩm hoặc chi tiết đơn hàng. |
| **Use Case liên quan** | UC15 |
| **Quy tắc áp dụng** | BR-03, BR-12; G-11 |

*(Hình 4.8. Workflow trao đổi giữa người mua và người bán (WF-06) – Lược bỏ hình ảnh)*

**Bảng mô tả các bước – Workflow trao đổi giữa người mua và người bán (WF-06)**

| STT | Đối tượng | Hoạt động | Kết quả / trạng thái |
| :--- | :--- | :--- | :--- |
| 1 | Người dùng | Chọn Chat từ trang sản phẩm hoặc chi tiết đơn. | Yêu cầu mở hội thoại. |
| 2 | Hệ thống | Kiểm tra: không chat với chính mình, đối phương không bị khóa. | Cho phép hoặc từ chối. |
| 3 | Hệ thống | Nếu từ chối: thông báo lý do, kết thúc. | Không mở hội thoại. |
| 4 | Hệ thống | Nếu cho phép: mở hội thoại sẵn có hoặc tạo mới giữa hai bên. | Hiển thị lịch sử trao đổi. |
| 5 | Người dùng | Soạn và gửi tin nhắn. | Tin nhắn gửi lên hệ thống. |
| 6 | Hệ thống | Kiểm tra nội dung không rỗng và không vượt giới hạn; nếu sai thì báo lỗi, quay lại bước 5. | Người dùng sửa nội dung. |
| 7 | Hệ thống | Lưu tin nhắn, hiển thị cho người gửi, gửi thông báo cho người nhận. | Tin nhắn được lưu. |
| 8 | Người dùng | Người nhận xem tin nhắn và phản hồi, hoặc kết thúc trao đổi. | Tiếp tục hoặc kết thúc. |

---

### WF-07. Đánh giá giao dịch

| Thông tin | Chi tiết |
| :--- | :--- |
| **Mục tiêu** | Tăng tính minh bạch bằng cách cho hai bên đánh giá nhau sau giao dịch thành công. |
| **Tác nhân** | Người mua, Người bán (có điều kiện) |
| **Kích hoạt** | Đơn hàng chuyển sang Hoàn thành. |
| **Use Case liên quan** | UC16 |
| **Quy tắc áp dụng** | BR-07; G-09 |

*(Hình 4.9. Workflow đánh giá giao dịch (WF-07) – Lược bỏ hình ảnh)*

**Bảng mô tả các bước – Workflow đánh giá giao dịch (WF-07)**

| STT | Đối tượng | Hoạt động | Kết quả / trạng thái |
| :--- | :--- | :--- | :--- |
| 1 | Người dùng | Mở đơn đã hoàn thành, chọn Đánh giá. | Yêu cầu đánh giá. |
| 2 | Hệ thống | Kiểm tra: là một bên của đơn, chưa đánh giá, còn trong 7 ngày. | Đủ hoặc không đủ điều kiện. |
| 3 | Hệ thống | Nếu không đủ điều kiện: thông báo và kết thúc. | Không lưu đánh giá. |
| 4 | Người dùng | Chọn số sao (1–5), nhập nhận xét. | Dữ liệu đánh giá. |
| 5 | Hệ thống | Kiểm tra hợp lệ; nếu sai thì báo lỗi, quay lại bước 4. | Người dùng sửa. |
| 6 | Hệ thống | Lưu đánh giá và cập nhật điểm trung bình của người được đánh giá. | Đánh giá được ghi nhận. |
| 7 | Người dùng | Người được đánh giá xem kết quả tại hồ sơ hoặc lịch sử giao dịch. | Kết thúc. |

---

### WF-08. Báo cáo vi phạm và kiểm duyệt

| Thông tin | Chi tiết |
| :--- | :--- |
| **Mục tiêu** | Tiếp nhận, xác minh và xử lý nội dung hoặc tài khoản vi phạm một cách nhất quán. |
| **Tác nhân** | Người mua, Người bán (gửi báo cáo), Admin (xử lý), Hệ thống |
| **Kích hoạt** | Người dùng gửi báo cáo, hoặc Admin chủ động rà soát sản phẩm. |
| **Use Case liên quan** | UC17, UC20, UC21, UC18 |
| **Quy tắc áp dụng** | BR-03, BR-09, BR-11; G-04 |

*(Hình 4.10. Workflow báo cáo vi phạm và kiểm duyệt (WF-08) – Lược bỏ hình ảnh)*

**Bảng mô tả các bước – Workflow báo cáo vi phạm và kiểm duyệt (WF-08)**

| STT | Đối tượng | Hoạt động | Kết quả / trạng thái |
| :--- | :--- | :--- | :--- |
| 1 | Người dùng | Chọn Báo cáo tại sản phẩm hoặc tài khoản; chọn lý do, nhập mô tả và gửi. | Báo cáo gửi lên hệ thống. |
| 2 | Hệ thống | Kiểm tra hợp lệ và không trùng báo cáo đang chờ xử lý; nếu sai thì thông báo và kết thúc. | Hợp lệ hoặc không. |
| 3 | Hệ thống | Lưu báo cáo, thông báo đã tiếp nhận. | Báo cáo ở trạng thái Chờ xử lý. |
| 4 | Admin | Mở chi tiết báo cáo hoặc sản phẩm (cũng có thể bắt đầu từ rà soát chủ động), xác minh nội dung. | Kết luận có vi phạm hay không. |
| 5 | Admin | Không vi phạm: bác bỏ báo cáo, ghi chú xử lý. | Báo cáo được bác bỏ. |
| 6 | Admin | Có vi phạm và đối tượng là sản phẩm: gỡ sản phẩm; các đơn đang xử lý bị hủy, thông báo người bán. | Sản phẩm Bị gỡ. |
| 7 | Admin | Có vi phạm và đối tượng là tài khoản: khóa tài khoản, ẩn sản phẩm của tài khoản, ghi lý do. | Tài khoản Bị khóa. |
| 8 | Hệ thống | Chuyển báo cáo sang Đã xử lý, ghi nhật ký, thông báo kết quả cho người báo cáo. | Kết thúc. |

---

### WF-09. Quản trị tài khoản, danh mục và thống kê

| Thông tin | Chi tiết |
| :--- | :--- |
| **Mục tiêu** | Giúp Admin duy trì hoạt động của nền tảng. |
| **Tác nhân** | Admin, Hệ thống |
| **Kích hoạt** | Admin đăng nhập vào khu vực quản trị. |
| **Use Case liên quan** | UC18, UC19, UC22 |
| **Quy tắc áp dụng** | BR-09, BR-10, BR-11 |

*(Hình 4.11. Workflow quản trị tài khoản, danh mục và thống kê (WF-09) – Lược bỏ hình ảnh)*

**Bảng mô tả các bước – Workflow quản trị tài khoản, danh mục và thống kê (WF-09)**

| STT | Đối tượng | Hoạt động | Kết quả / trạng thái |
| :--- | :--- | :--- | :--- |
| 1 | Admin | Đăng nhập khu vực quản trị và chọn chức năng: Tài khoản, Danh mục hoặc Thống kê. | Chuyển nhánh chức năng. |
| 2 | Admin | Tài khoản: xem danh sách, tìm kiếm, mở chi tiết, chọn Khóa hoặc Mở khóa. | Yêu cầu thao tác. |
| 3 | Hệ thống | Khóa (kèm lý do): chặn đăng nhập, ẩn sản phẩm của tài khoản, ghi nhật ký. | Tài khoản Bị khóa. |
| 4 | Hệ thống | Mở khóa: cho phép đăng nhập trở lại, ghi nhật ký. | Tài khoản Hoạt động. |
| 5 | Admin | Danh mục: xem danh sách, chọn Thêm, Sửa hoặc Vô hiệu hóa. | Yêu cầu thao tác. |
| 6 | Hệ thống | Kiểm tra tên danh mục duy nhất và ràng buộc danh mục đang có sản phẩm; sai thì báo lỗi, quay lại bước 5. | Hợp lệ hoặc không. |
| 7 | Hệ thống | Lưu thay đổi danh mục, ghi nhật ký. | Danh mục được cập nhật. |
| 8 | Admin | Thống kê: chọn khoảng thời gian. | Yêu cầu thống kê. |
| 9 | Hệ thống | Tổng hợp và hiển thị số liệu người dùng, sản phẩm, đơn hàng, báo cáo. | Kết thúc. |

---

## 5. SƠ ĐỒ CHUYỂN TRẠNG THÁI

Các trạng thái dưới đây là cơ sở để thiết kế cơ sở dữ liệu và kiểm soát nghiệp vụ. Mỗi chuyển trạng thái chỉ được thực hiện bởi tác nhân và điều kiện ghi trong bảng.

### 5.1. Trạng thái đơn hàng
*(Hình 5.1. Chuyển trạng thái đơn hàng – Lược bỏ hình ảnh)*

| Từ | Sự kiện | Tác nhân | Đến | Điều kiện |
| :--- | :--- | :--- | :--- | :--- |
| **(Bắt đầu)** | Tạo đơn | Người mua | Chờ xác nhận | Sản phẩm khả dụng (BR-02) |
| **Chờ xác nhận** | Xác nhận | Người bán | Đã xác nhận | Đơn thuộc người bán |
| **Chờ xác nhận** | Từ chối | Người bán | Đã từ chối | Bắt buộc nhập lý do |
| **Chờ xác nhận** | Hủy / quá 48 giờ | Người mua, Người bán, Hệ thống | Đã hủy | BR-05, BR-06 |
| **Đã xác nhận** | Hủy | Người mua, Người bán | Đã hủy | Bắt buộc nhập lý do (BR-06) |
| **Đã xác nhận** | Cập nhật giao nhận | Người bán | Đang giao nhận | Hai bên đã thống nhất phương án |
| **Đang giao nhận** | Cập nhật hoàn thành | Người bán | Hoàn thành | Đã bàn giao và thanh toán |

### 5.2. Trạng thái sản phẩm
*(Hình 5.2. Chuyển trạng thái sản phẩm – Lược bỏ hình ảnh)*

| Từ | Sự kiện | Tác nhân | Đến | Điều kiện |
| :--- | :--- | :--- | :--- | :--- |
| **(Bắt đầu)** | Đăng bán | Người bán | Đang bán | Dữ liệu hợp lệ |
| **Đang bán** | Đơn được tạo | Hệ thống | Đang giữ | G-07 |
| **Đang giữ** | Đơn bị từ chối / hủy | Hệ thống | Đang bán | Đơn Đã từ chối hoặc Đã hủy |
| **Đang giữ** | Đơn Hoàn thành | Hệ thống | Đã bán | Người bán cập nhật Hoàn thành |
| **Đang bán** | Ẩn / ngừng bán | Người bán | Ẩn | Không có đơn đang xử lý (BR-08) |
| **Ẩn** | Hiện lại | Người bán | Đang bán | Sản phẩm không bị gỡ |
| **Đang bán, Ẩn, Đang giữ** | Gỡ sản phẩm | Admin | Bị gỡ | Có lý do; ghi nhật ký (BR-11) |

### 5.3. Trạng thái tài khoản và báo cáo vi phạm
*(Hình 5.3. Chuyển trạng thái tài khoản và báo cáo vi phạm – Lược bỏ hình ảnh)*

| Đối tượng | Từ | Sự kiện | Tác nhân | Đến |
| :--- | :--- | :--- | :--- | :--- |
| **Tài khoản** | (Bắt đầu) | Đăng ký thành công | Người dùng | Hoạt động |
| **Tài khoản** | Hoạt động | Khóa (kèm lý do) | Admin | Bị khóa |
| **Tài khoản** | Bị khóa | Mở khóa | Admin | Hoạt động |
| **Báo cáo** | (Bắt đầu) | Gửi báo cáo | Người mua, Người bán | Chờ xử lý |
| **Báo cáo** | Chờ xử lý | Xử lý (hợp lệ hoặc bác bỏ) | Admin | Đã xử lý |

---

## 6. QUY TẮC NGHIỆP VỤ ÁP DỤNG TRONG WORKFLOW

| Mã | Quy tắc |
| :--- | :--- |
| **BR-01** | Sản phẩm chỉ thuộc một người bán; chỉ chủ sở hữu được chỉnh sửa, ẩn hoặc hiện lại sản phẩm của mình. |
| **BR-02** | Chỉ sản phẩm ở trạng thái Đang bán mới được hiển thị công khai và được đặt mua. Sản phẩm Ẩn hoặc Bị gỡ không hiển thị với người mua. |
| **BR-03** | Người dùng không được mua, chat hoặc báo cáo sản phẩm của chính mình. |
| **BR-04** | Mỗi đơn hàng chỉ chứa sản phẩm của một người bán (G-06). |
| **BR-05** | Đơn Chờ xác nhận quá 48 giờ tự động chuyển Đã hủy (G-07). |
| **BR-06** | Chỉ được hủy đơn khi đơn ở trạng thái Chờ xác nhận hoặc Đã xác nhận; bên hủy bắt buộc nhập lý do. |
| **BR-07** | Chỉ đơn Hoàn thành mới được đánh giá; mỗi bên đánh giá một lần cho mỗi đơn, trong vòng 7 ngày (G-09). |
| **BR-08** | Không được chỉnh sửa giá và thông tin cốt lõi của sản phẩm khi sản phẩm đang có đơn Chờ xác nhận, Đã xác nhận hoặc Đang giao nhận. |
| **BR-09** | Tài khoản bị khóa không thể đăng nhập; sản phẩm của tài khoản đó bị ẩn khỏi danh mục công khai. |
| **BR-10** | Danh mục đang có sản phẩm chỉ được vô hiệu hóa, không được xóa. |
| **BR-11** | Admin không mặc nhiên được thay đổi giá bán hoặc nội dung giao dịch; mọi thao tác quản trị ảnh hưởng đến tài khoản, sản phẩm, báo cáo phải được ghi nhật ký. |
| **BR-12** | Người dùng chỉ xem và thao tác trên đơn hàng, cuộc hội thoại thuộc quyền của mình; Admin chỉ được xem đơn hàng ở chế độ chỉ đọc khi có căn cứ xử lý báo cáo. |

---

## 7. ĐỐI CHIẾU WORKFLOW VÀ USE CASE

| Workflow | Tên workflow | Use Case liên quan |
| :--- | :--- | :--- |
| **WF-01** | Đăng ký, đăng nhập và đăng xuất | UC01, UC02 |
| **WF-02** | Đăng bán và quản lý sản phẩm | UC06, UC07, UC08 |
| **WF-03** | Tìm kiếm, giỏ hàng và đặt hàng | UC03, UC04, UC05, UC09, UC10 |
| **WF-04** | Xử lý đơn hàng | UC11, UC12, UC13, UC15 |
| **WF-05** | Hủy đơn hàng | UC14 |
| **WF-06** | Trao đổi (Chat) | UC15 |
| **WF-07** | Đánh giá giao dịch | UC16 |
| **WF-08** | Báo cáo vi phạm và kiểm duyệt | UC17, UC18, UC20, UC21 |
| **WF-09** | Quản trị tài khoản, danh mục và thống kê | UC18, UC19, UC22 |
| **(Ngoài workflow)** | Cập nhật thông tin cá nhân | UC23 |