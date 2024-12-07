const content = {
    vi: {
        title: "Chào Mừng Đến Với DragonBall HOTEL",
        description: "Tìm kiếm để so sánh giá cả và khám phá ưu đãi tuyệt vời",
        button: "Tìm",
        home: "Trang Chủ",
        roomSystem: "Hệ Thống Phòng",
        about: "Giới Thiệu",
        rules: "Quy Tắc",
        // login: "Đăng Nhập",
        address: "Địa Chỉ: P. Kieu Mai, Phuc Dien, Tu Liem, Hanoi",
        email: "Email: contact@dragonballhotel.com",
        phone: "Điện Thoại: +84 397156204",
        copyright: "Bản Quyền © 2024 DragonBall Hotel. Tất cả quyền được bảo lưu.",
        footerTitle: "Vị trí của chúng tôi",
        footerContact: "Thông tin liên hệ",
        tabHome: "Trang Chủ",
        tabRoomSystem: "Hệ Thống Phòng",
        tabAbout: "Giới Thiệu",
        tabRules: "Quy Tắc",
        titleTrangchu: "Đánh thức mọi giác quan của bạn tại DragonBall Hotel",
        descriptionTrangchu: "Khách sạn Dragon Ball Hotel với các phòng ngủ được xây dựng theo phong cách hiện đại phương Tây. <br> Mỗi phòng được bố trí đón ánh sáng và gió biển thổi vào rất thoáng mát. <br> Đội ngũ nhân viên phục vụ là người địa phương rất thân thiện và được đào tạo chuyên nghiệp để phục vụ cho quý khách.",
        featuresTrangchu: [
            "Khách sạn sang trọng tại trung tâm thành phố",
            "Vị trí thuận tiện gần các điểm du lịch",
            "Dịch vụ hoàn hảo và tiện nghi hiện đại"
        ],
        titleTopPhong: "Phòng nổi bật"
    },
    en: {
        title: "Welcome to DragonBall HOTEL",
        description: "Search to compare prices and discover great deals",
        button: "Search",
        home: "Home",
        roomSystem: "Room System",
        about: "About",
        rules: "Rules",
        login: "Login",
        address: "Address: P. Kieu Mai, Phuc Dien, Tu Liem, Hanoi",
        email: "Email: contact@dragonballhotel.com",
        phone: "Phone: +84 397156204",
        copyright: "© 2024 DragonBall Hotel. All Rights Reserved.",
        footerTitle: "Our Location",
        footerContact: "Contact Information",
        tabHome: "Home",
        tabRoomSystem: "Room System",
        tabAbout: "About",
        tabRules: "Rules",
        titleTrangchu: "Awaken all your senses at DragonBall Hotel",
        descriptionTrangchu: "Dragon Ball Hotel rooms are designed in a modern Western style. <br> Each room is arranged to receive light and sea breeze, creating a refreshing atmosphere. <br> The staff are friendly locals, professionally trained to serve guests.",
        featuresTrangchu: [
            "Luxury hotel in the city center",
            "Convenient location near tourist attractions",
            "Perfect services and modern amenities"
        ],
        titleTopPhong: "Featured Rooms"
    }
};

let currentLang = 'vi'; // Default language

// Các phần tử cần cập nhật nội dung
const elements = {
    title: document.querySelector('.text-white.mb-3.text-center'),
    description: document.querySelector('.text-white.mb-4.text-center'),
    searchButton: document.querySelector('.btn-primary'),
    homeTab: document.querySelector('#tab-trangchu'),
    roomSystemTab: document.querySelector('#tab-hethongphong'),
    aboutTab: document.querySelector('#tab-gioithieu'),
    rulesTab: document.querySelector('#tab-quytac'),
    // loginButton: document.querySelector('.login-button-container .btn'),
    footerTitle: document.querySelector('.bg-dark h5.text-white:nth-of-type(1)'),
    footerContact: document.querySelector('.bg-dark h5.text-white:nth-of-type(2)'),
    footerAddress: document.querySelector('.bg-dark p:nth-of-type(1)'),
    footerEmail: document.querySelector('.bg-dark p:nth-of-type(2)'),
    footerPhone: document.querySelector('.bg-dark p:nth-of-type(3)'),
    copyrightElement: document.querySelector('.copyright'),
    titleTrangchu: document.querySelector('#tab-trangchu .eco-garden-description h2'),
    descriptionTrangchu: document.querySelector('#tab-trangchu .eco-garden-description p:first-of-type'),
    feature1: document.querySelector('#tab-trangchu .features p:nth-of-type(1)'),
    feature2: document.querySelector('#tab-trangchu .features p:nth-of-type(2)'),
    feature3: document.querySelector('#tab-trangchu .features p:nth-of-type(3)'),
    topPhongTitle: document.querySelector('#tab-trangchu .top-phong-title'),
};

// Hàm cập nhật nội dung
function updateContent(language) {
    const langContent = content[language];

    // Cập nhật các phần tử tiêu đề, mô tả, nút và các tab
    elements.title.textContent = langContent.title;
    elements.description.textContent = langContent.description;
    elements.searchButton.textContent = langContent.button;
    elements.homeTab.textContent = langContent.tabHome;
    elements.roomSystemTab.textContent = langContent.tabRoomSystem;
    elements.aboutTab.textContent = langContent.tabAbout;
    elements.rulesTab.textContent = langContent.tabRules;
    // elements.loginButton.textContent = langContent.login;
    elements.footerTitle.textContent = langContent.footerTitle;
    elements.footerContact.textContent = langContent.footerContact;
    elements.footerAddress.textContent = langContent.address;
    elements.footerEmail.textContent = langContent.email;
    elements.footerPhone.textContent = langContent.phone;
    elements.copyrightElement.textContent = langContent.copyright;

    // Cập nhật thông tin trang chủ
    elements.titleTrangchu.textContent = langContent.titleTrangchu;
    elements.descriptionTrangchu.innerHTML = langContent.descriptionTrangchu;

    // Cập nhật các tính năng nổi bật
    elements.feature1.textContent = langContent.featuresTrangchu[0];
    elements.feature2.textContent = langContent.featuresTrangchu[1];
    elements.feature3.textContent = langContent.featuresTrangchu[2];

    // Cập nhật tiêu đề phòng nổi bật
    elements.topPhongTitle.textContent = langContent.titleTopPhong;
}

// Thêm sự kiện cho các nút ngôn ngữ
document.getElementById('vi-btn').addEventListener('click', () => {
    currentLang = 'vi';
    updateContent(currentLang);
});

document.getElementById('en-btn').addEventListener('click', () => {
    currentLang = 'en';
    updateContent(currentLang);
});

// Cập nhật nội dung ban đầu
updateContent(currentLang);
