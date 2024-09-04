$(document).ready(function () {
    let list =[];

    let apiGetUrl = "http://localhost:8080/lich-lam-viec/hien-thi";

    loadTable('', '', '');

    function showSuccessToast(message) {
        Toastify({
            text: message,
            duration: 5000,
            gravity: "top",
            position: "right",
            style: {
                background: "#4CAF50",
            },
            stopOnFocus: true
        }).showToast();
    }

    function showErrorToast(message) {
        Toastify({
            text: message,
            duration: 5000,
            gravity: "top",
            position: "right",
            style: {
                background: "#f44336",
            },
            stopOnFocus: true
        }).showToast();
    }

    $('#file').on('change', function() {
        let fileName = '';
        let newFileName = $(this).val().split('\\').pop(); // Lấy tên file đã chọn mới

        if (newFileName) { // Nếu có chọn file mới
            fileName = newFileName; // Lưu tên file mới
            $('#labelFile').html('<label for="file" style="margin: 15px">' + fileName + '</label>'); // Thay đổi nội dung của label
            $('#btnSubmitFile').show(); // Hiển thị nút btn để gửi file
        } else { // Nếu không chọn file mới
            // Giữ nguyên file đã chọn trước đó, nếu có
            if (fileName) {
                $('#labelFile').html('<label for="file" style="margin: 15px">' + fileName + '</label>'); // Giữ nguyên nội dung của label
                $('#btnSubmitFile').show(); // Hiển thị nút btn để gửi file
            } else {
                // Nếu không có file đã chọn trước đó, không làm gì cả
            }
        }
    });
    $('#btnSubmitFile').click(function() {
        ajaxSubmitForm(); // Gọi hàm ajaxSubmitForm để gửi dữ liệu form
    });

    // Hàm gửi dữ liệu form bằng AJAX
    function ajaxSubmitForm() {
        let form = new FormData();
        let file = $('#file')[0].files[0]; // Lấy file từ input
        form.append('file', file); // Thêm file vào FormData

        // Vô hiệu hóa nút gửi
        $('#btnSubmitFile').prop('disabled', true);

        $.ajax({
            type: 'POST',
            enctype: 'multipart/form-data',
            url: '/lich-lam-viec/upload', // Đường dẫn API xử lý upload file
            data: form,
            processData: false,
            contentType: false,
            cache: false,
            timeout: 1000000,
            success: function() {
                showSuccessToast("Tải lên file thành công!")
                $('#btnSubmitFile').prop('disabled', false); // Bật lại nút gửi
                $('#file').val(''); // Xóa giá trị của input file
                $('#labelFile').html('<label for="file" style="margin: 15px;"><i class="fas fa-file-excel fa-2x"></i></label>'); // Reset label
            },
            error: function() {
                showErrorToast("Tải lên file thất bại!")
                $('#btnSubmitFile').prop('disabled', false); // Bật lại nút gửi
            }
        });
    }

    $('#date-input').change(function () {
        let selectedDate = $(this).val();
        let key = $('#searchInput').val();
        let selectedValue = $('input[name="status"]:checked').val();
        loadTable(key, selectedValue, selectedDate);
    });
    $('#searchInput').on('input', function () {
        let key = $(this).val().trim(); // Lấy giá trị từ ô input search
        let selectedDate = $('#date-input').val();
        let selectedValue = $('input[name="status"]:checked').val();
        loadTable(key, selectedValue, selectedDate);
    });
    $('input[type=radio][name=status]').change(function () {
        let selectedStatus = this.value;
        let key = $('#searchInput').val();
        let selectedDate = $('#date-input').val();
        loadTable(key, selectedStatus, selectedDate);
    });

    function loadTable(keyString = '', ca = '', date = '') {
        $('#labelFile').hover(
            function() {
                $(this).css('background-color', '#2e8b57');
            },
            function() {
                $(this).css('background-color', '#3ad29f');
                $(this).css('color', 'white');
            }
        );
        let tbody = '';
        $.getJSON(apiGetUrl, function (data) {
            list = data;
            console.log(data)
            let today = new Date();

            let year = today.getFullYear();
            let month = ('0' + (today.getMonth() + 1)).slice(-2); // Tháng trong JavaScript bắt đầu từ 0
            let day = ('0' + today.getDate()).slice(-2);

            let formattedDate = year + '-' + month + '-' + day;
            if (keyString !== '' && ca !== '' && date !== '') {
                list = list.filter(lich => lich["gioBatDau"] === ca && lich["ngay"] === date &&
                    (lich["nhanVien"].maNhanVien.toLowerCase().includes(keyString.toLowerCase()) ||
                        lich["nhanVien"].hoTen.toLowerCase().includes(keyString.toLowerCase())));
            }
            if (date !== '' && keyString === '' && ca !== '') {
                list = list.filter(lich => lich["gioBatDau"] === ca && lich["ngay"] === date);
            }
            if (date !== '' && keyString === '' && ca === '') {
                list = list.filter(lich => lich["ngay"] === date);
            }

            if (date === '' && keyString === '' && ca === '') {
                list = list.filter(lich => lich["ngay"] === formattedDate
                );
            }

            if (date === '' && keyString !== '' & ca !== '') {
                list = list.filter(lich => lich["ngay"] === formattedDate && lich["gioBatDau"] === ca  &&
                    (lich["nhanVien"].maNhanVien.toLowerCase().includes(keyString.toLowerCase()) ||
                        lich["nhanVien"].hoTen.toLowerCase().includes(keyString.toLowerCase())));
            }

            if(date === '' && keyString ==='' && ca!=='') {
                list = list.filter(lich => lich["ngay"] === formattedDate && lich["gioBatDau"] === ca );
            }

            if(date === '' && keyString !=='' && ca===''){
                list = list.filter(lich => lich["ngay"] === formattedDate &&
                    (lich["nhanVien"].maNhanVien.toLowerCase().includes(keyString.toLowerCase()) ||
                        lich["nhanVien"].hoTen.toLowerCase().includes(keyString.toLowerCase())));
            }



            list.forEach((lich, index) => {
                let ngayFormatted = new Date(lich["ngay"]).toLocaleDateString('vi-VN');
                let gioBd = lich["gioBatDau"].slice(0, -3);
                let giokt = lich["gioKetThuc"].slice(0, -3);
                tbody += `<tr style="cursor: default">
                            <td class="special-td">${index + 1}</td>
                            <td class="special-td">${lich["nhanVien"].maNhanVien}</td>
                            <td class="special-td">${lich["nhanVien"].hoTen}</td>                                                
                            <td class="special-td">${lich["viTri"]}</td>
                            <td class="special-td">${lich["gioBatDau"] === '06:30:00' ? 'Ca sáng' : 'Ca chiều'}</td>
                            <td class="special-td">${gioBd}</td>
                            <td class="special-td">${giokt}</td>
                            <td class="special-td">${ngayFormatted}</td>
                        </tr>`;
            });
            $('#tbodyContainer').html(tbody);
        });
    }

});