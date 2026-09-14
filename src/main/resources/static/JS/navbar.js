const navToggle = document.getElementById("navToggle");
const navMenu = document.getElementById("navMenu");

navToggle.addEventListener('click', function (e){
    navMenu.classList.toggle('active');
});