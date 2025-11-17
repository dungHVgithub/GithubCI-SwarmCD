MỆNH ĐỀ VÀ TẬP HỢP - TOÁN LỚP 10
I. MỆNH ĐỀ
1. Khái niệm mệnh đề

Mệnh đề là một câu phát biểu có giá trị đúng hoặc sai, nhưng không thể vừa đúng vừa sai.
Ví dụ: 
"5 là số nguyên tố" (đúng).
"4 chia hết cho 3" (sai).


Không phải mệnh đề: Các câu hỏi, câu cảm thán, câu cầu khiến như "Hôm nay là thứ mấy?" hoặc "Học bài đi!".

2. Ký hiệu

Mệnh đề thường được ký hiệu bằng các chữ cái in hoa: P, Q, R,...
Giá trị chân lý: Đúng (True, ký hiệu T) hoặc Sai (False, ký hiệu F).

3. Các loại mệnh đề

Mệnh đề đơn: Là mệnh đề không chứa mệnh đề khác.
Mệnh đề phức: Được tạo từ các mệnh đề đơn bằng các liên từ logic như "và", "hoặc", "nếu... thì",...
Mệnh đề phủ định (¬P): Phủ định của mệnh đề P. Nếu P đúng thì ¬P sai và ngược lại.
Mệnh đề kéo theo (P ⇒ Q): "Nếu P thì Q". Mệnh đề này sai chỉ khi P đúng và Q sai.
Mệnh đề đảo: Từ P ⇒ Q, mệnh đề đảo là Q ⇒ P.
Mệnh đề tương đương (P ⇔ Q): "P khi và chỉ khi Q". Đúng khi cả P và Q cùng đúng hoặc cùng sai.
Mệnh đề hợp (P ∧ Q): "P và Q". Đúng khi cả P và Q đều đúng.
Mệnh đề tuyển (P ∨ Q): "P hoặc Q". Sai khi cả P và Q đều sai.



4. Bảng chân lý

Bảng chân lý thể hiện giá trị đúng/sai của các mệnh đề phức:

II. TẬP HỢP
1. Khái niệm tập hợp

Tập hợp là tập hợp các đối tượng (phần tử) được xác định rõ ràng.
Ký hiệu: 
Tập hợp được ký hiệu bằng các chữ cái in hoa A, B, C,...
Phần tử được ký hiệu bằng chữ thường a, b, c,...

Cách biểu diễn:
Liệt kê: A = {1, 2, 3, 4}.
Đặc tính: A = {x | x là số nguyên dương nhỏ hơn 5}.


2. Các loại tập hợp

Tập rỗng (∅): Không chứa phần tử nào.
Tập con (⊂): A ⊂ B nếu mọi phần tử của A đều thuộc B.
Tập hợp con của chính nó và tập rỗng là tập con của mọi tập hợp.
Số tập con của tập hợp có n phần tử: (2^n).

3. Các phép toán trên tập hợp

Hợp: (A \cup B = {x \mid x \in A \text{ hoặc } x \in B}).
Giao: (A \cap B = {x \mid x \in A \text{ và } x \in B}).
Hiệu: (A \setminus B = {x \mid x \in A \text{ và } x \notin B}).
Phần bù: Nếu (A \subset U), phần bù của A là (U \setminus A = {x \in U \mid x \notin A}).
Tích Descartes: (A \times B = {(a, b) \mid a \in A, b \in B}).

4. Tính chất

Giao hoán: (A \cup B = B \cup A), (A \cap B = B \cap A).
Kết hợp: ((A \cup B) \cup C = A \cup (B \cup C)), ((A \cap B) \cap C = A \cap (B \cap C)).
Phân phối: 
(A \cup (B \cap C) = (A \cup B) \cap (A \cup C)),
(A \cap (B \cup C) = (A \cap B) \cup (A \cap C)).

5. Biểu đồ Venn

Dùng để minh họa các quan hệ và phép toán trên tập hợp.
Ví dụ: 
Hợp là vùng chứa tất cả phần tử của A và B.
Giao là vùng chung của A và B.

6. Ứng dụng

Mệnh đề và tập hợp được dùng để giải bài toán logic, đếm số phần tử, xác định quan hệ giữa các tập hợp.
Ví dụ: Tìm số phần tử của (A \cup B): (|A \cup B| = |A| + |B| - |A \cap B|).
