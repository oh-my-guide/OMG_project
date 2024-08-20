$(document).ready(function() {
    function validateForm() {
        let isValid = true;

        // 닉네임, 연락처, 성별, 생일이 모두 입력되었는지 확인
        if (!$('#phoneNumber').val() || !$('#gender').val() || !$('#birthdate').val()) {
            isValid = false;
        }

        $('#submit-button').prop('disabled', !isValid);

        return isValid;
    }

    // 폼 제출 이벤트 처리
    $('form').on('submit', function(event) {
        if (!validateForm()) {
            alert('모든 필드를 올바르게 입력해주세요.');
            event.preventDefault(); // 폼 제출 막기
        } else {
            alert("추가 정보가 저장되었습니다.")
        }
    });
});

