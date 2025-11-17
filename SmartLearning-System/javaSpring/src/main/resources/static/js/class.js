function showToast(msg, isSuccess = true) {
    const toastEl = document.getElementById('deleteToast');
    const toastBody = document.getElementById('toastMsg');
    toastBody.textContent = msg;
    // Xóa class cũ
    toastEl.classList.remove('toast-success', 'toast-fail');
    // Thêm class mới
    toastEl.classList.add(isSuccess ? 'toast-success' : 'toast-fail');
    const toast = new bootstrap.Toast(toastEl, { delay: 2000 });
    toast.show();
}


function deleteClass(endpoint, id) {
    if (confirm("Dữ liệu liên quan của lớp học cũng sẽ bị xóa, bạn chắc chắn xóa ??") === true) {
        fetch(endpoint + id, { method: "delete" })
        .then(res => {
            if (res.status === 204 || res.status === 200) {
                showToast("Delete Class Success!", true);
                setTimeout(() => location.reload(), 1500);
            } else {
                showToast("Delete Class Failed!", false);
            }
        });
    }
}
