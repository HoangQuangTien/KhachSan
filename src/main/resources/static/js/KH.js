const content = {
    vi: {
        title: "Chào Mừng Đến Với DragonBall HOTEL",
        description: "Tìm kiếm để so sánh giá cả và khám phá ưu đãi tuyệt vời",
        button: "Tìm",
        home: "Trang Chủ",
        about: "Giới Thiệu",
        roomClasses: "Hạng Phòng",
        login: "Đăng Nhập",
        address: "Địa Chỉ: P. Kieu Mai, Phuc Dien, Tu Liem, Hanoi",
        email: "Email: contact@dragonballhotel.com",
        phone: "Điện Thoại: +84 397156204",
        copyright: "Bản Quyền © 2024 DragonBall Hotel. Tất cả quyền được bảo lưu.",
        footerTitle: "Vị trí của chúng tôi",
        footerContact: "Thông tin liên hệ"
    },
    en: {
        title: "Welcome to DragonBall HOTEL",
        description: "Search to compare prices and discover great deals",
        button: "Search",
        home: "Home",
        about: "About",
        roomClasses: "Room Classes",
        login: "Login",
        address: "Address: P. Kieu Mai, Phuc Dien, Tu Liem, Hanoi",
        email: "Email: contact@dragonballhotel.com",
        phone: "Phone: +84 397156204",
        copyright: "© 2024 DragonBall Hotel. All Rights Reserved.",
        footerTitle: "Our Location",
        footerContact: "Contact Information"
    }
};

let currentLang = 'vi'; // Ngôn ngữ mặc định

// Các phần tử trên trang
const titleElement = document.querySelector('.text-white.mb-3.text-center'); // Tiêu đề
const descriptionElement = document.querySelector('.text-white.mb-4.text-center'); // Mô tả
const searchButton = document.querySelector('.btn-primary'); // Nút tìm
const homeLink = document.querySelector('.Home-button-container .text-link:nth-of-type(1)'); // Trang chủ
const aboutLink = document.querySelector('.Home-button-container .text-link:nth-of-type(2)'); // Giới thiệu
const roomClassesLink = document.querySelector('.Home-button-container .text-link:nth-of-type(3)'); // Hạng phòng
const loginButton = document.querySelector('.login-button-container .btn'); // Nút đăng nhập

// Các phần tử trong footer
const footerTitle = document.querySelector('.bg-dark h5.text-white:nth-of-type(1)'); // Tiêu đề footer
const footerContact = document.querySelector('.bg-dark h5.text-white:nth-of-type(2)'); // Thông tin liên hệ footer
const footerAddress = document.querySelector('.bg-dark p:nth-of-type(1)'); // Địa chỉ footer
const footerEmail = document.querySelector('.bg-dark p:nth-of-type(2)'); // Email footer
const footerPhone = document.querySelector('.bg-dark p:nth-of-type(3)'); // Số điện thoại footer
const copyrightElement = document.querySelector('.copyright'); // Bản quyền

// Cập nhật nội dung dựa trên ngôn ngữ
function updateContent() {
    titleElement.textContent = content[currentLang].title;
    descriptionElement.textContent = content[currentLang].description;
    searchButton.textContent = content[currentLang].button;
    homeLink.textContent = content[currentLang].home;
    aboutLink.textContent = content[currentLang].about;
    roomClassesLink.textContent = content[currentLang].roomClasses;
    loginButton.textContent = content[currentLang].login;

    // Cập nhật nội dung footer
    footerTitle.textContent = content[currentLang].footerTitle;
    footerContact.textContent = content[currentLang].footerContact;
    footerAddress.textContent = content[currentLang].address;
    footerEmail.textContent = content[currentLang].email;
    footerPhone.textContent = content[currentLang].phone;
    copyrightElement.textContent = content[currentLang].copyright;
}

// Xử lý sự kiện nhấn vào nút ngôn ngữ
document.getElementById('vi-btn').addEventListener('click', () => {
    currentLang = 'vi'; // Chọn ngôn ngữ tiếng Việt
    updateContent(); // Cập nhật nội dung
});

document.getElementById('en-btn').addEventListener('click', () => {
    currentLang = 'en'; // Chọn ngôn ngữ tiếng Anh
    updateContent(); // Cập nhật nội dung
});

// Cập nhật nội dung ban đầu
updateContent();
