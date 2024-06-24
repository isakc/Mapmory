// picker.js

document.addEventListener('DOMContentLoaded', function() {
    const yearPicker = document.getElementById('yearPicker');
    const monthPicker = document.getElementById('monthPicker');

/*
    // 현재 연도를 기준으로 -50년부터 +10년까지 옵션 생성
    const currentYear = new Date().getFullYear();
    for (let i = currentYear - 10; i <= currentYear + 30; i++) {
        const option = document.createElement('option');
        option.value = i;
        option.textContent = i;
        yearPicker.appendChild(option);
    }
    */
   
    // 현재 연도를 기준으로 -10년부터 현재 연도까지 옵션 생성
    const currentYear = new Date().getFullYear();
    for (let i = currentYear - 10; i <= currentYear; i++) {
        const option = document.createElement('option');
        option.value = i;
        option.textContent = i;
        yearPicker.appendChild(option);
    }

    // 기본값을 현재 연도로 설정
    yearPicker.value = currentYear;

    // 연도나 달이 변경될 때 호출되는 함수
    function handleDateChange() {
        const selectedYear = yearPicker.value;
        const selectedMonth = monthPicker.value;
        console.log(`Selected Year: ${selectedYear}, Selected Month: ${selectedMonth}`);
        // 여기에 추가적인 로직을 넣어 원하는 작업을 수행할 수 있습니다.
    }

    yearPicker.addEventListener('change', handleDateChange);
    monthPicker.addEventListener('change', handleDateChange);
});
